package org.example.dominio;

import org.example.dominio.ObjetosClave.*;

import java.util.Comparator;

public class ComparadorPorTotal implements Comparator<Factura> {
    @Override
    public int compare(Factura f1, Factura f2) {
        return Double.compare(f1.getTotal(), f2.getTotal());
    }
}
