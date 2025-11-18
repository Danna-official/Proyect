package com.example.inventario.model;

public class Maquillaje extends Producto {
    private String marca;
    private String categoria;

    public Maquillaje(int id, String nombre, double precioBase, int stock, String marca, String categoria) {
        super(id, nombre, precioBase, stock);
        this.marca = marca;
        this.categoria = categoria;
    }

    public String getMarca() { return marca; }
    public String getCategoria() { return categoria; }

    @Override
    public double calcularPrecioFinal() {
        return super.calcularPrecioFinal() * 1.05;
    }
}
