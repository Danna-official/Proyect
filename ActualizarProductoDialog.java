package com.example.inventario.ui;

import com.example.inventario.db.BaseDatos;

import javax.swing.*;
import java.awt.*;

public class ActualizarProductoDialog extends JDialog {

    public ActualizarProductoDialog(Frame owner) {
        super(owner, "Actualizar Precio de Producto", true);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtId = new JTextField();
        JTextField txtNuevoPrecio = new JTextField();

        panel.add(new JLabel("ID del Producto:"));
        panel.add(txtId);

        panel.add(new JLabel("Nuevo Precio (ejemplo 12000.50):"));
        panel.add(txtNuevoPrecio);

        add(panel, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnActualizar = new JButton("💾 Actualizar");
        JButton btnCancelar = new JButton("Cancelar");

        botones.add(btnActualizar);
        botones.add(btnCancelar);

        add(botones, BorderLayout.SOUTH);

        // Acción botón actualizar
        btnActualizar.addActionListener(e -> {
            try {
                int idProducto = Integer.parseInt(txtId.getText().trim());
                double nuevoPrecio = Double.parseDouble(txtNuevoPrecio.getText().trim().replace(",", "."));

                BaseDatos bd = BaseDatos.getInstance();
                boolean ok = bd.actualizarPrecio(idProducto, nuevoPrecio);

                if (ok) {
                    JOptionPane.showMessageDialog(this, "Precio actualizado correctamente");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el producto o no se pudo actualizar");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Verifica los datos ingresados", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dispose());
    }
}
