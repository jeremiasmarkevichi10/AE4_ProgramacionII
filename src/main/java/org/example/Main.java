package org.example;

import java.util.Scanner;

import org.example.servicio.GestorFunciones;
import org.example.persistencia.FacturaRepository;
import org.example.presentacion.MenuConsola;
import org.example.dominio.Fabricas.FabricaProducto;
import org.example.dominio.Fabricas.FabricaServicio;
import org.example.dominio.PoliticasCredito.PoliticasCredito;

public class Main {
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        GestorFunciones gestor = new GestorFunciones(
                new FacturaRepository(), entrada, PoliticasCredito.estandar(),
                new FabricaProducto(), new FabricaServicio());
        MenuConsola menu = new MenuConsola(gestor, entrada);
        menu.iniciar();
    }
}
