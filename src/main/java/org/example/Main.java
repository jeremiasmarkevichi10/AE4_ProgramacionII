package org.example;

import java.io.IOException;
import java.util.Scanner;

import org.example.servicio.GestorFunciones;
import org.example.Persistencia.FacturaRepository;
import org.example.presentacion.MenuConsola;
import org.example.dominio.Fabricas.FabricaProducto;
import org.example.dominio.Fabricas.FabricaServicio;
import org.example.dominio.PoliticasCredito.PoliticasCredito;
import org.example.Persistencia.ClienteRepository;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner entrada = new Scanner(System.in);
        GestorFunciones gestor = new GestorFunciones(
                new FacturaRepository(), new ClienteRepository(), PoliticasCredito.estandar(),
                new FabricaProducto(), new FabricaServicio());
        MenuConsola menu = new MenuConsola(gestor, entrada);
        menu.iniciar();
    }
}
