package com.example.inventario.ui;

import com.example.inventario.db.BaseDatos;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ListarProductosFrame extends JFrame {
    private JTable tabla;
    private DefaultTableModel model;

    public ListarProductosFrame() {
        setTitle("Lista de Productos");
        setSize(1000,600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new Object[]{"ID","Nombre","Precio","Stock","Marca","Categoría"},0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        tabla = new JTable(model);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.addActionListener(e -> cargarProductos());
        bottom.add(btnActualizar);
        add(bottom, BorderLayout.SOUTH);

        cargarProductos();
    }

    private void cargarProductos() {
        model.setRowCount(0);
        BaseDatos bd = BaseDatos.getInstance();
        List<Object[]> prods = bd.obtenerProductos(null, false);
        for (Object[] p: prods) {
            model.addRow(new Object[] { p[0], p[1], String.format("$%,.2f", (Double)p[2]), p[3], p[4]==null?"N/A":p[4], p[5]==null?"N/A":p[5] });
        }
    }
}
