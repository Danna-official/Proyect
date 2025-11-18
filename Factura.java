package com.example.inventario.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Factura {
    private static int numeroFactura = 1000;
    private static final double IVA = 0.19;

    private int numero;
    private String cliente;
    private LocalDateTime fecha;
    private List<ItemFactura> items;
    private boolean finalizada;

    public Factura() {
        this.numero = numeroFactura++;
        this.cliente = "Cliente General";
        this.fecha = LocalDateTime.now();
        this.items = new ArrayList<>();
        this.finalizada = false;
    }

    public int getNumero() { return numero; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) {
        if (finalizada) throw new IllegalStateException("Factura finalizada");
        this.cliente = cliente;
    }

    public void agregarItem(ItemFactura it) {
        if (finalizada) throw new IllegalStateException("No se pueden agregar items");
        items.add(it);
    }

    public ItemFactura eliminarItem(int idx) {
        if (finalizada) throw new IllegalStateException("No se pueden eliminar items");
        return items.remove(idx);
    }

    public double calcularSubtotal() {
        return items.stream().mapToDouble(ItemFactura::getSubtotal).sum();
    }

    public double calcularIVA() { return calcularSubtotal() * IVA; }
    public double calcularTotal() { return calcularSubtotal() + calcularIVA(); }

    public void finalizar() {
        if (items.isEmpty()) throw new IllegalStateException("Factura vacía");
        this.finalizada = true;
    }

    public List<ItemFactura> getItems() { return new ArrayList<>(items); }
}
