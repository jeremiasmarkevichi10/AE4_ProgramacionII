package org.example.dominio.PoliticasCredito;

import org.example.dominio.Enums.Categoria;

public class PoliticaCreditoPremium implements PoliticaCredito {
    @Override
    public Categoria getCategoria() {
        return Categoria.PREMIUM;
    }

    @Override
    public int calcularLimiteCredito() {
        return 50000;
    }
}
