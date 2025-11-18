package com.example.inventario.model;

public class Producto {
    protected int id;
    protected String nombre;
    protected double precioBase;
    protected int stock;

    public Producto(int id, String nombre, double precioBase, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.stock = stock;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecioBase() { return precioBase; }
    public int getStock() { return stock; }

    public void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("Stock no puede ser negativo");
        this.stock = stock;
    }

    public void setPrecioBase(double precio) {
        if (precio <= 0) throw new IllegalArgumentException("Precio debe ser mayor a 0");
        this.precioBase = precio;
    }

    public double calcularPrecioFinal() {
        return precioBase * 1.19;
    }

    public void reducirStock(int cantidad) {
        if (cantidad > stock) throw new IllegalArgumentException("Stock insuficiente");
        stock -= cantidad;
    }
}
