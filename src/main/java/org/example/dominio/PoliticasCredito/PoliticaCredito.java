package org.example.dominio.PoliticasCredito;

import org.example.dominio.Enums.Categoria;

public interface PoliticaCredito {
    Categoria getCategoria();

    int calcularLimiteCredito();
}
