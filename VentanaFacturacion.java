package com.example.inventario.ui;

import com.example.inventario.db.BaseDatos;
import com.example.inventario.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class VentanaFacturacion extends JDialog {
    private JComboBox<String> comboProductos;
    private List<Object[]> productosDisponibles;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtCantidad, txtCliente, txtIdentificacion, txtBuscar;
    private JLabel lblSubtotal, lblIva, lblTotal;
    private Factura facturaActual;
    private List<Map<String, Object>> itemsConId; // para guardar a BD

    public VentanaFacturacion(Frame owner) {
        super(owner, "Facturación", true);
        setSize(1000, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        facturaActual = new Factura();
        itemsConId = new ArrayList<>();

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(212,160,23));
        header.setPreferredSize(new Dimension(0,80));
        JLabel title = new JLabel("💄 DISTRIBUIDORA DE MAQUILLAJE");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Center panels
        JPanel center = new JPanel();
        center.setLayout(new BorderLayout());

        // Cliente panel
        JPanel clientePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientePanel.setBorder(BorderFactory.createTitledBorder("DATOS DEL CLIENTE"));
        txtCliente = new JTextField(30);
        txtIdentificacion = new JTextField(15);
        clientePanel.add(new JLabel("Nombre:"));
        clientePanel.add(txtCliente);
        clientePanel.add(new JLabel("Cédula/NIT:"));
        clientePanel.add(txtIdentificacion);

        center.add(clientePanel, BorderLayout.NORTH);

        // Producto selection
        JPanel productoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        productoPanel.setBorder(BorderFactory.createTitledBorder("AÑADIR PRODUCTOS"));
        txtBuscar = new JTextField(20);
        txtBuscar.addActionListener(e -> cargarProductos(txtBuscar.getText()));
        productoPanel.add(new JLabel("Buscar:"));
        productoPanel.add(txtBuscar);

        comboProductos = new JComboBox<>();
        comboProductos.setPreferredSize(new Dimension(500, 25));
        productoPanel.add(new JLabel("Producto:"));
        productoPanel.add(comboProductos);

        txtCantidad = new JTextField("1",5);
        productoPanel.add(new JLabel("Cantidad:"));
        productoPanel.add(txtCantidad);

        JButton btnAgregar = new JButton("➕ Agregar al Carrito");
        btnAgregar.addActionListener(e -> agregarItem());
        productoPanel.add(btnAgregar);

        center.add(productoPanel, BorderLayout.CENTER);

        // Tabla
        tableModel = new DefaultTableModel(new Object[]{"Producto","Cantidad","Precio Unit.","Subtotal"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(900, 220));
        center.add(sp, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        // South: botones + totales
        JPanel south = new JPanel(new BorderLayout());

        JPanel botones = new JPanel();
        JButton btnEliminar = new JButton("🗑️ Eliminar Producto");
        btnEliminar.addActionListener(e -> eliminarItem());
        JButton btnFinalizar = new JButton("💾 Finalizar y Guardar");
        btnFinalizar.addActionListener(e -> finalizarFactura());
        JButton btnHistorial = new JButton("📜 Ver Historial");
        btnHistorial.addActionListener(e -> 
                new HistorialVentasDialog((Frame) getOwner()).setVisible(true)
        );
        botones.add(btnEliminar);
        botones.add(btnFinalizar);
        botones.add(btnHistorial);

        south.add(botones, BorderLayout.NORTH);

        JPanel totales = new JPanel(new GridLayout(3,1));
        lblSubtotal = new JLabel("Subtotal: $0.00");
        lblIva = new JLabel("IVA (19%): $0.00");
        lblTotal = new JLabel("TOTAL A PAGAR: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        totales.add(lblSubtotal);
        totales.add(lblIva);
        totales.add(lblTotal);

        south.add(totales, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);

        cargarProductos(null);
        setVisible(true);
    }

    private void cargarProductos(String filtro) {
        BaseDatos bd = BaseDatos.getInstance();
        productosDisponibles = bd.obtenerProductos(filtro, true);
        comboProductos.removeAllItems();
        for (Object[] p : productosDisponibles) {
            String nombre = String.format("%s - $%,.2f (Stock: %d)", p[1], (Double)p[2], (Integer)p[3]);
            comboProductos.addItem(nombre);
        }
        if (comboProductos.getItemCount() > 0) comboProductos.setSelectedIndex(0);
    }

    private void agregarItem() {
        try {
            int idx = comboProductos.getSelectedIndex();
            if (idx < 0) { JOptionPane.showMessageDialog(this, "Seleccione un producto"); return; }
            Object[] prodBD = productosDisponibles.get(idx);
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();

            int stock = (Integer)prodBD[3];
            if (cantidad > stock) {
                JOptionPane.showMessageDialog(this, "Stock insuficiente. Disponible: " + stock);
                return;
            }

            // Crear item
            Producto p = new Producto((Integer)prodBD[0], (String)prodBD[1], (Double)prodBD[2], (Integer)prodBD[3]);
            ItemFactura item = new ItemFactura(p, cantidad);
            facturaActual.agregarItem(item);

            // Guardar para BD
            Map<String,Object> mapa = new HashMap<>();
            mapa.put("id_producto", (Integer)prodBD[0]);
            mapa.put("cantidad", cantidad);
            mapa.put("precio", (Double)prodBD[2]);
            mapa.put("subtotal", item.getSubtotal());
            itemsConId.add(mapa);

            // Mostrar en tabla
            tableModel.addRow(new Object[] {
                prodBD[1],
                cantidad,
                String.format("$%,.2f", (Double)prodBD[2]),
                String.format("$%,.2f", item.getSubtotal())
            });

            actualizarTotales();
            txtCantidad.setText("1");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar producto: " + ex.getMessage());
        }
    }

    private void eliminarItem() {
        int sel = table.getSelectedRow();
        if (sel < 0) { JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar"); return; }
        facturaActual.eliminarItem(sel);
        itemsConId.remove(sel);
        tableModel.removeRow(sel);
        actualizarTotales();
    }

    private void actualizarTotales() {
        double subtotal = facturaActual.calcularSubtotal();
        double iva = facturaActual.calcularIVA();
        double total = facturaActual.calcularTotal();
        lblSubtotal.setText(String.format("Subtotal: $%,.2f", subtotal));
        lblIva.setText(String.format("IVA (19%%): $%,.2f", iva));
        lblTotal.setText(String.format("TOTAL A PAGAR: $%,.2f", total));
    }

    private void finalizarFactura() {
        String cliente = txtCliente.getText().trim();
        String identificacion = txtIdentificacion.getText().trim();
        if (cliente.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese el nombre del cliente"); return; }
        if (identificacion.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese la identificación"); return; }
        if (facturaActual.getItems().isEmpty()) { JOptionPane.showMessageDialog(this, "La factura está vacía"); return; }

        facturaActual.setCliente(cliente);
        facturaActual.finalizar();

        BaseDatos bd = BaseDatos.getInstance();
        Integer idVenta = bd.guardarVenta(cliente, identificacion, itemsConId, facturaActual.calcularSubtotal(), facturaActual.calcularIVA(), facturaActual.calcularTotal());

        if (idVenta != null) {
            JOptionPane.showMessageDialog(this, "Venta guardada con ID " + idVenta);
            // Bloquear edición
            txtBuscar.setEnabled(false);
            comboProductos.setEnabled(false);
            txtCantidad.setEnabled(false);
            txtCliente.setEnabled(false);
            txtIdentificacion.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la venta");
        }
    }
}
