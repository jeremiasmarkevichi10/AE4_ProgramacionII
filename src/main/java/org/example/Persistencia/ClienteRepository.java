package org.example.Persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dominio.Personas.Cliente;
import org.example.dominio.RepositorioClientes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepository implements RepositorioClientes {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File archivo = new File("datos/clientes.json");

    @Override
    public void guardarTodos(List<Cliente> clientes) throws IOException {
        File carpeta = archivo.getParentFile();
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        mapper.writerFor(new TypeReference<List<Cliente>>() {})
                .withDefaultPrettyPrinter()
                .writeValue(archivo, clientes);
    }

    @Override
    public List<Cliente> obtenerTodos() throws IOException {
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        return mapper.readValue(archivo, new TypeReference<List<Cliente>>() {});
    }
}
