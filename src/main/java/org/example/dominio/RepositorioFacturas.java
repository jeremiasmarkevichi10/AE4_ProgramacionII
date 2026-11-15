package org.example.dominio;

import org.example.dominio.ObjetosClave.Factura;

import java.io.IOException;
import java.util.List;

public interface RepositorioFacturas {
    void guardarTodas(List<Factura> facturas) throws IOException;

    List<Factura> obtenerTodas() throws IOException;
}
