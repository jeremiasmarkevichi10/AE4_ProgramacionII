package org.example.dominio.Fabricas;

import org.example.dominio.ObjetosClave.ItemComercial;
import org.example.dominio.Personas.Proveedor;

import java.util.Random;

public abstract class FabricaItemComercial<T extends ItemComercial> {
    private final Random random;

    protected FabricaItemComercial() {
        this(new Random());
    }

    protected FabricaItemComercial(Random random) {
        this.random = random;
    }

    public abstract T crear(String nombre, Proveedor proveedor);

    protected String generarCodigo() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            codigo.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return codigo.toString();
    }

    protected int generarPrecio() {
        return 6000 + random.nextInt(70001);
    }

    protected int generarStock() {
        return 2 + random.nextInt(11);
    }
}
