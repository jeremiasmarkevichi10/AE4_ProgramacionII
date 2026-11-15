package org.example.dominio.ObjetosClave;

import org.example.dominio.Interfaces.Identificable;
import org.example.dominio.Interfaces.Vencible;
import org.example.dominio.Personas.Proveedor;
import java.time.format.DateTimeParseException;

public class Producto extends ItemComercial implements Identificable, Vencible {
    private String fechaVencimiento;
    private int stock;

    public Producto(String codigo, String nombre,
                    int precio, int stock, Proveedor proveedor) {
        super(codigo, nombre, precio, proveedor);
        this.stock = stock;
    }

    @Override
    public String getTipo() {
        return "Producto";
    }

    @Override
    public boolean estaVencido() {
        if (fechaVencimiento == null) return false;
        try {
            return java.time.LocalDate.parse(fechaVencimiento).isBefore(java.time.LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    public Producto() {}


    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setStockSuma(int stock) {
        this.stock += stock;
    }

    public void setStockResta(int stock) {
        this.stock -= stock;
    }
    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
}
