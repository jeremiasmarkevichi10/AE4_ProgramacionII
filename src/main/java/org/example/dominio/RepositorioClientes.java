package org.example.dominio;

import org.example.dominio.Personas.Cliente;

import java.io.IOException;
import java.util.List;

public interface RepositorioClientes {
    void guardarTodos(List<Cliente> clientes) throws IOException;

    List<Cliente> obtenerTodos() throws IOException;
}
