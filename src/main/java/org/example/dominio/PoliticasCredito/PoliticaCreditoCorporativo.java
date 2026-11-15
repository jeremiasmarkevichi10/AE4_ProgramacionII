package org.example.dominio.PoliticasCredito;

import org.example.dominio.Enums.Categoria;

public class PoliticaCreditoCorporativo implements PoliticaCredito {
    @Override
    public Categoria getCategoria() {
        return Categoria.CORPORATIVO;
    }

    @Override
    public int calcularLimiteCredito() {
        return 100000;
    }
}
