package org.example;
import java.util.Scanner;

import org.example.dominio.Exceptions.CreditoInsuficienteException;
import org.example.dominio.Exceptions.FacturaSinItemsException;
import org.example.dominio.Exceptions.PagoExcedeDeudaException;
import org.example.servicio.GestorFunciones;
import org.example.persistencia.FacturaRepository;

public class Main {
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        GestorFunciones gestor = new GestorFunciones(new FacturaRepository(), entrada);
        gestor.inicializarDatosDePrueba();
        int opcion = 0;
        while(opcion != 10) {
            System.out.println("1.Registrar empleado");
            System.out.println("2.Mostrar empleados");
            System.out.println("3.Asignar Responsable");
            System.out.println("4.Mostrar Departamentos");
            System.out.println("5.Registrar pago");
            System.out.println("6.Mostrar catalogo");
            System.out.println("7.Mostrar productos vencidos");
            System.out.println("8.Mostrar facturas ordenadas por numero");
            System.out.println("9.Mostrar facturas ordenadas por total");
            System.out.println("10.Salir");
            opcion = entrada.nextInt();
            entrada.nextLine();
            switch (opcion) {
                case 1:
                    gestor.registrarEmpleado();
                    break;
                case 2:
                    gestor.mostrarEmpleados();
                    break;
                case 3:
                    gestor.asignarResponsable();
                    break;
                case 4:
                    gestor.mostrarDepartamentos();
                    break;
                case 5:
                    try {
                        gestor.registrarPago();
                    } catch (FacturaSinItemsException | CreditoInsuficienteException
                             | PagoExcedeDeudaException e) {
                        System.out.println("No se pudo completar la compra: " + e.getMessage());
                    }
                    break;
                case 6:
                    gestor.mostrarCatalogo();
                    break;
                case 7:
                    gestor.mostrarProductosVencidos();
                    break;
                case 8:
                    gestor.mostrarFacturasOrdenadasPorNumero();
                    break;
                case 9:
                    gestor.mostrarFacturasOrdenadasPorTotal();
                    break;
                case 10:
                    break;
                default:
                    System.out.println("Opcion invalida");

            }
        }



    }
}
