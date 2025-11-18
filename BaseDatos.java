
package com.example.inventario.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class BaseDatos {
    private static BaseDatos instancia;
    private final String host = "localhost";
    private final String port = "5432";
    private final String database = "inventario_distribuidora_maquillaje";
    private final String user = "postgres";
    private final String password = "Valentina05.";

    private BaseDatos() {}

    public static synchronized BaseDatos getInstance() {
        if (instancia == null) instancia = new BaseDatos();
        return instancia;
    }

    public Connection getConnection() {
        try {
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error de conexión a PostgreSQL:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public List<Object[]> obtenerProductos(String filtro, boolean soloConStock) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id_producto, nombre_producto, precio_venta, stock_actual, marca, categoria FROM productos WHERE 1=1";

        if (soloConStock) sql += " AND stock_actual > 0";
        if (filtro != null && !filtro.trim().isEmpty()) {
            sql += " AND nombre_producto ILIKE ?";
        }
        sql += " ORDER BY nombre_producto";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (filtro != null && !filtro.trim().isEmpty()) ps.setString(1, "%" + filtro + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("id_producto"),
                        rs.getString("nombre_producto"),
                        rs.getDouble("precio_venta"),
                        rs.getInt("stock_actual"),
                        rs.getString("marca"),
                        rs.getString("categoria")
                    };
                    lista.add(row);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al consultar productos:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return lista;
    }

    public Object[] obtenerProductoPorId(int id) {
        String sql = "SELECT id_producto, nombre_producto, precio_venta, stock_actual FROM productos WHERE id_producto = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[] {
                        rs.getInt("id_producto"),
                        rs.getString("nombre_producto"),
                        rs.getDouble("precio_venta"),
                        rs.getInt("stock_actual")
                    };
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener producto:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    public boolean agregarProducto(String nombre, String descripcion, String categoria, String marca,
                                   double valorUnitario, double precioVenta, int stock, String codigoBarras) {
        String sql = "INSERT INTO productos (nombre_producto, descripcion, categoria, marca, valor_unitario, precio_venta, stock_actual, codigo_barras) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setString(3, categoria);
            ps.setString(4, marca);
            ps.setDouble(5, valorUnitario);
            ps.setDouble(6, precioVenta);
            ps.setInt(7, stock);
            ps.setString(8, codigoBarras);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al agregar producto:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean actualizarPrecio(int idProducto, double nuevoPrecio) {
        String sql = "UPDATE productos SET precio_venta = ? WHERE id_producto = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, nuevoPrecio);
            ps.setInt(2, idProducto);
            int r = ps.executeUpdate();
            return r > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar precio:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean eliminarProducto(int idProducto) {
        String sql = "DELETE FROM productos WHERE id_producto = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            int r = ps.executeUpdate();
            return r > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar producto:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public Integer guardarVenta(String cliente, String identificacion, java.util.List<java.util.Map<String, Object>> items, double subtotal, double iva, double total) {
        String sqlVenta = "INSERT INTO ventas (cliente_nombre, cliente_identificacion, fecha, subtotal, iva, total) VALUES (?, ?, ?, ?, ?, ?) RETURNING id_venta";
        String sqlDetalle = "INSERT INTO detalles_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        String sqlStock = "UPDATE productos SET stock_actual = stock_actual - ? WHERE id_producto = ?";

        try (Connection c = getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement psVenta = c.prepareStatement(sqlVenta);
                 PreparedStatement psDetalle = c.prepareStatement(sqlDetalle);
                 PreparedStatement psStock = c.prepareStatement(sqlStock)) {

                psVenta.setString(1, cliente);
                psVenta.setString(2, identificacion);
                psVenta.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                psVenta.setDouble(4, subtotal);
                psVenta.setDouble(5, iva);
                psVenta.setDouble(6, total);

                try (ResultSet rs = psVenta.executeQuery()) {
                    if (rs.next()) {
                        int idVenta = rs.getInt(1);

                        for (java.util.Map<String, Object> item : items) {
                            int idProd = (Integer) item.get("id_producto");
                            int cantidad = (Integer) item.get("cantidad");
                            double precio = (Double) item.get("precio");
                            double subtotalItem = (Double) item.get("subtotal");

                            psDetalle.setInt(1, idVenta);
                            psDetalle.setInt(2, idProd);
                            psDetalle.setInt(3, cantidad);
                            psDetalle.setDouble(4, precio);
                            psDetalle.setDouble(5, subtotalItem);
                            psDetalle.executeUpdate();

                            psStock.setInt(1, cantidad);
                            psStock.setInt(2, idProd);
                            psStock.executeUpdate();
                        }

                        c.commit();
                        return idVenta;
                    } else {
                        c.rollback();
                        return null;
                    }
                }
            } catch (SQLException ex) {
                c.rollback();
                JOptionPane.showMessageDialog(null, "Error al guardar venta:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en BD:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
