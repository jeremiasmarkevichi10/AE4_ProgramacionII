package org.example.dominio.Fabricas;

import org.example.dominio.ObjetosClave.Producto;
import org.example.dominio.Personas.Proveedor;

import java.util.Random;

public class FabricaProducto extends FabricaItemComercial<Producto> {
    public FabricaProducto() {
        super();
    }

    public FabricaProducto(Random random) {
        super(random);
    }

    @Override
    public Producto crear(String nombre, Proveedor proveedor) {
        return new Producto(generarCodigo(), nombre, generarPrecio(), generarStock(), proveedor);
    }
}
