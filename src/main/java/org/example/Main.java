package org.example;

import java.util.Scanner;

import org.example.servicio.GestorFunciones;
import org.example.persistencia.FacturaRepository;
import org.example.presentacion.MenuConsola;

public class Main {
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        GestorFunciones gestor = new GestorFunciones(new FacturaRepository(), entrada);
        MenuConsola menu = new MenuConsola(gestor, entrada);
        menu.iniciar();
    }
}
