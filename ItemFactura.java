package com.example.inventario.model;

public class ItemFactura {
    private Producto producto;
    private int cantidad;

    public ItemFactura(Producto producto, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad > 0");
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return producto.getPrecioBase() * cantidad; }
}
