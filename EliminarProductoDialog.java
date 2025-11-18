package com.example.inventario.ui;

import com.example.inventario.db.BaseDatos;

import javax.swing.*;
import java.awt.*;

public class EliminarProductoDialog extends JDialog {

    public EliminarProductoDialog(Frame owner) {
        super(owner, "Eliminar Producto", true);
        setSize(400, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtId = new JTextField();

        panel.add(new JLabel("ID del Producto a eliminar:"));
        panel.add(txtId);

        add(panel, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEliminar = new JButton("🗑️ Eliminar");
        JButton btnCancelar = new JButton("Cancelar");

        botones.add(btnEliminar);
        botones.add(btnCancelar);

        add(botones, BorderLayout.SOUTH);

        btnEliminar.addActionListener(e -> {
            try {
                int idProducto = Integer.parseInt(txtId.getText().trim());

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "¿Estás seguro de eliminar el producto con ID " + idProducto + "?",
                        "Confirmación",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm != JOptionPane.YES_OPTION) return;

                BaseDatos bd = BaseDatos.getInstance();
                boolean ok = bd.eliminarProducto(idProducto);

                if (ok) {
                    JOptionPane.showMessageDialog(this, "Producto eliminado con éxito");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el producto o no pudo ser eliminado");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El ID debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dispose());
    }
}
