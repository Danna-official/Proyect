package com.example.inventario.ui;

import com.example.inventario.db.BaseDatos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class HistorialVentasDialog extends JDialog {
    private JTable tablaVentas;
    private DefaultTableModel modelVentas;

    public HistorialVentasDialog(Frame owner) {
        super(owner, "Historial de Ventas", true);
        setSize(900, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        modelVentas = new DefaultTableModel(new Object[]{"# Venta", "Cliente", "Identificación", "Fecha", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaVentas = new JTable(modelVentas);
        JScrollPane scroll = new JScrollPane(tablaVentas);
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnActualizar = new JButton("🔄 Actualizar");
        JButton btnVerDetalle = new JButton("📄 Ver Detalle");
        JButton btnCerrar = new JButton("Cerrar");

        btnActualizar.addActionListener(e -> cargarVentas());
        btnVerDetalle.addActionListener(e -> mostrarDetalleSeleccion());
        btnCerrar.addActionListener(e -> dispose());

        bottom.add(btnActualizar);
        bottom.add(btnVerDetalle);
        bottom.add(btnCerrar);

        add(bottom, BorderLayout.SOUTH);

        cargarVentas();
    }

    /**
     * Carga las ventas desde la BD y las muestra en la tabla.
     */
    private void cargarVentas() {
        modelVentas.setRowCount(0);
        Connection conn = BaseDatos.getInstance().getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT id_venta, cliente_nombre, cliente_identificacion, fecha, total FROM ventas ORDER BY fecha DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            while (rs.next()) {
                int id = rs.getInt("id_venta");
                String cliente = rs.getString("cliente_nombre");
                String ident = rs.getString("cliente_identificacion");
                Timestamp ts = rs.getTimestamp("fecha");
                String fecha = ts != null ? sdf.format(ts) : "";
                double total = rs.getDouble("total");

                modelVentas.addRow(new Object[]{ id, cliente, ident, fecha, String.format("$%,.2f", total) });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar historial:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
    }

    /**
     * Obtiene la venta seleccionada y abre un diálogo con sus detalles.
     */
    private void mostrarDetalleSeleccion() {
        int fila = tablaVentas.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta para ver su detalle.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idObj = modelVentas.getValueAt(fila, 0);
        if (!(idObj instanceof Integer)) {
            JOptionPane.showMessageDialog(this, "ID de venta inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idVenta = (Integer) idObj;
        mostrarDetalleVenta(idVenta);
    }

    /**
     * Muestra un JDialog con la lista de productos (detalle) de la venta.
     */
    private void mostrarDetalleVenta(int idVenta) {
        JDialog dlg = new JDialog(this, "Detalle Venta #" + idVenta, true);
        dlg.setSize(700, 450);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        DefaultTableModel modelDetalle = new DefaultTableModel(new Object[]{"Producto", "Cantidad", "Precio Unit.", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        JTable tablaDetalle = new JTable(modelDetalle);
        dlg.add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblInfo = new JLabel("Detalle de la venta — ID: " + idVenta);
        arriba.add(lblInfo);
        dlg.add(arriba, BorderLayout.NORTH);

        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dlg.dispose());
        abajo.add(btnCerrar);
        dlg.add(abajo, BorderLayout.SOUTH);

        // Consultar la BD para los detalles
        Connection conn = BaseDatos.getInstance().getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT p.nombre_producto, d.cantidad, d.precio_unitario, d.subtotal " +
                     "FROM detalles_venta d " +
                     "LEFT JOIN productos p ON d.id_producto = p.id_producto " +
                     "WHERE d.id_venta = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                double subtotalCalc = 0.0;
                while (rs.next()) {
                    String nombre = rs.getString(1);
                    int cantidad = rs.getInt(2);
                    double precio = rs.getDouble(3);
                    double subtotal = rs.getDouble(4);
                    subtotalCalc += subtotal;
                    modelDetalle.addRow(new Object[]{
                        nombre != null ? nombre : "Producto eliminado",
                        cantidad,
                        String.format("$%,.2f", precio),
                        String.format("$%,.2f", subtotal)
                    });
                }
                // Opcional: mostrar totales al final
                if (modelDetalle.getRowCount() == 0) {
                    modelDetalle.addRow(new Object[]{"(Sin detalles encontrados)", "", "", ""});
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener detalle:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }

        dlg.setVisible(true);
    }
}
