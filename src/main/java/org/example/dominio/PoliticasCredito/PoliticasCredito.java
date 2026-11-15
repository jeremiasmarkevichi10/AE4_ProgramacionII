package org.example.dominio.PoliticasCredito;

import java.util.List;

public final class PoliticasCredito {
    private PoliticasCredito() {
    }

    public static List<PoliticaCredito> estandar() {
        return List.of(
                new PoliticaCreditoRegular(),
                new PoliticaCreditoPremium(),
                new PoliticaCreditoCorporativo()
        );
    }
}
