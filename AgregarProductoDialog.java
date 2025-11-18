package com.example.inventario.ui;

import com.example.inventario.db.BaseDatos;
import javax.swing.*;
import java.awt.*;

public class AgregarProductoDialog extends JDialog {
    public AgregarProductoDialog(Frame owner) {
        super(owner, "Agregar Producto", true);
        setSize(500,600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel main = new JPanel();
        main.setLayout(new GridLayout(0,1,5,5));
        main.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JTextField txtNombre = new JTextField();
        JTextField txtDescripcion = new JTextField();
        JTextField txtCategoria = new JTextField();
        JTextField txtMarca = new JTextField();
        JTextField txtValorUnitario = new JTextField();
        JTextField txtPrecioVenta = new JTextField();
        JTextField txtStock = new JTextField();
        JTextField txtCodigo = new JTextField();

        main.add(new JLabel("Nombre *")); main.add(txtNombre);
        main.add(new JLabel("Descripción")); main.add(txtDescripcion);
        main.add(new JLabel("Categoría")); main.add(txtCategoria);
        main.add(new JLabel("Marca")); main.add(txtMarca);
        main.add(new JLabel("Valor Unitario *")); main.add(txtValorUnitario);
        main.add(new JLabel("Precio Venta *")); main.add(txtPrecioVenta);
        main.add(new JLabel("Stock")); main.add(txtStock);
        main.add(new JLabel("Código de Barras")); main.add(txtCodigo);

        add(main, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton btnGuardar = new JButton("💾 Guardar");
        btnGuardar.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty() || txtValorUnitario.getText().trim().isEmpty() || txtPrecioVenta.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Llena los campos obligatorios");
                return;
            }
            try {
                double valUnit = Double.parseDouble(txtValorUnitario.getText().replace(",", "."));
                double precio = Double.parseDouble(txtPrecioVenta.getText().replace(",", "."));
                int stock = txtStock.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtStock.getText().trim());
                BaseDatos bd = BaseDatos.getInstance();
                boolean ok = bd.agregarProducto(txtNombre.getText().trim(), txtDescripcion.getText().trim(), txtCategoria.getText().trim(),
                        txtMarca.getText().trim(), valUnit, precio, stock, txtCodigo.getText().trim());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Producto agregado correctamente");
                    dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valores numéricos inválidos");
            }
        });
        JButton btnCancel = new JButton("✖ Cancelar");
        btnCancel.addActionListener(e -> dispose());
        buttons.add(btnGuardar);
        buttons.add(btnCancel);
        add(buttons, BorderLayout.SOUTH);
    }
}
