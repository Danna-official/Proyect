package com.example.inventario;

import com.example.inventario.ui.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame {
    public static void main(String[] args) {
        // Opcional: FlatLaf
        try { UIManager.setLookAndFeel( new com.formdev.flatlaf.FlatLightLaf() ); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema de Inventario - Distribuidora de Maquillaje");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(560, 680);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());

            JPanel header = new JPanel();
            header.setBackground(new java.awt.Color(212,160,23));
            header.setPreferredSize(new Dimension(0,120));
            JLabel logo = new JLabel("");
            logo.setFont(new Font("Arial", Font.PLAIN, 48));
            header.add(logo);
            JLabel title = new JLabel("Sistema de Inventario");
            title.setFont(new Font("Arial", Font.BOLD, 22));
            title.setForeground(Color.WHITE);
            header.add(title);

            frame.add(header, BorderLayout.NORTH);

            JPanel center = new JPanel();
            center.setLayout(new GridLayout(6,1,10,10));
            center.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            center.add(createButton("FACTURACIÓN", e -> new VentanaFacturacion(frame)));
            center.add(createButton("Agregar Producto", e -> new AgregarProductoDialog(frame).setVisible(true)));
            center.add(createButton("Listar Productos", e -> new ListarProductosFrame().setVisible(true)));
            center.add(createButton("Actualizar Producto", e -> new ActualizarProductoDialog(frame).setVisible(true)));
            center.add(createButton("Eliminar Producto", e -> new EliminarProductoDialog(frame).setVisible(true)));

            frame.add(center, BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }

    private static JButton createButton(String text, java.awt.event.ActionListener al) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setBackground(new java.awt.Color(212,160,23));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.addActionListener(al);
        return b;
    }
}
