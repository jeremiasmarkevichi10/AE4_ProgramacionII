package org.example.dominio.ObjetosClave;

import org.example.dominio.Interfaces.Identificable;
import org.example.dominio.Personas.Proveedor;

public class Servicio extends ItemComercial implements Identificable {

    public Servicio(String codigo, String nombre,
                    int precio, Proveedor proveedor) {
        super(codigo, nombre, precio, proveedor);
    }
    @Override
    public String getTipo(){
        return "Servicio";
    }
    public Servicio() {}
}