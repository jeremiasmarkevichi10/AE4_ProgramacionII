package org.example.dominio.PoliticasCredito;

import org.example.dominio.Enums.Categoria;

public class PoliticaCreditoRegular implements PoliticaCredito {
    @Override
    public Categoria getCategoria() {
        return Categoria.REGULAR;
    }

    @Override
    public int calcularLimiteCredito() {
        return 30000;
    }
}
