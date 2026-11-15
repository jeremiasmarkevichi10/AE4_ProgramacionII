package org.example.dominio.Fabricas;

import org.example.dominio.ObjetosClave.Servicio;
import org.example.dominio.Personas.Proveedor;

import java.util.Random;

public class FabricaServicio extends FabricaItemComercial<Servicio> {
    public FabricaServicio() {
        super();
    }

    public FabricaServicio(Random random) {
        super(random);
    }

    @Override
    public Servicio crear(String nombre, Proveedor proveedor) {
        return new Servicio(generarCodigo(), nombre, generarPrecio(), proveedor);
    }
}
