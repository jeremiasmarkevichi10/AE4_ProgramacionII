package org.example.presentacion;

import org.example.dominio.Enums.FormaPago;
import org.example.dominio.Exceptions.CreditoInsuficienteException;
import org.example.dominio.Exceptions.FacturaSinItemsException;
import org.example.dominio.Exceptions.PagoExcedeDeudaException;
import org.example.dominio.ObjetosClave.Factura;
import org.example.dominio.ObjetosClave.ItemComercial;
import org.example.dominio.ObjetosClave.Producto;
import org.example.dominio.ObjetosClave.Servicio;
import org.example.dominio.Personas.Cliente;
import org.example.servicio.GestorFunciones;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuConsola {
    private final GestorFunciones gestor;
    private final Scanner entrada;

    public MenuConsola(GestorFunciones gestor, Scanner entrada) {
        this.gestor = gestor;
        this.entrada = entrada;
    }

    public void iniciar() {
        gestor.inicializarDatosDePrueba();
        int opcion = 0;
        while (opcion != 10) {
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
                        registrarPagoInteractivo();
                    } catch (FacturaSinItemsException | CreditoInsuficienteException
                             | PagoExcedeDeudaException e) {
                        System.out.println("No se pudo completar la compra: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("La factura se genero, pero no se pudo guardar: " + e.getMessage());
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

    private void registrarPagoInteractivo() throws FacturaSinItemsException,
            CreditoInsuficienteException, PagoExcedeDeudaException, IOException {
            System.out.println("==== Registro de compra ====");
            System.out.println("Esta el cliente registrado en el sistema? (s/n): ");
            String respuesta = entrada.nextLine().trim().toLowerCase();

            Cliente cliente = null;
            if (respuesta.equals("s") || respuesta.equals("si")) {
                boolean seguirBuscando = true;
                while (seguirBuscando && cliente == null) {
                    System.out.println("Ingrese el nombre del cliente: ");
                    String nombreIngresado = entrada.nextLine();
                    System.out.println("Ingrese el dni del cliente: ");
                    int dniIngresado = entrada.nextInt();
                    entrada.nextLine();

                    cliente = gestor.buscarCliente(nombreIngresado, dniIngresado);
                    if (cliente == null) {
                        System.out.println("No se encontro ningun cliente con esos datos. Desea intentar de nuevo? (s/n): ");
                        String reintentar = entrada.nextLine().trim().toLowerCase();
                        seguirBuscando = reintentar.equals("s") || reintentar.equals("si");
                    }
                }
            }

            if (cliente == null) {
                System.out.println("--- Registro de nuevo cliente ---");
                System.out.println("Ingrese el nombre del cliente: ");
                String nombreNuevo = entrada.nextLine();
                System.out.println("Ingrese el dni del cliente: ");
                int dniNuevo = entrada.nextInt();
                entrada.nextLine();
                cliente = gestor.registrarCliente(nombreNuevo, dniNuevo);
                System.out.println("Cliente registrado con categoria REGULAR y credito de $"
                        + cliente.getLimiteCredito());
            }

            System.out.println(cliente.saludar());
            List<ItemComercial> carrito = new ArrayList<>();
            boolean seguirComprando = true;
            while (seguirComprando) {
                System.out.println("===== Productos =====");
                for (Producto producto : gestor.obtenerProductos()) {
                    System.out.println(producto.getCodigo() + " | " + producto.mostrarResumen()
                            + " | Stock: " + producto.getStock());
                }
                System.out.println("===== Servicios =====");
                for (Servicio servicio : gestor.obtenerServicios()) {
                    System.out.println(servicio.getCodigo() + " | " + servicio.mostrarResumen());
                }

                System.out.println("Desea comprar producto o servicio? (p/s): ");
                String tipoItem = entrada.nextLine().trim().toLowerCase();
                if (tipoItem.equals("p")) {
                    System.out.println("Ingrese el codigo del producto: ");
                    Producto producto = gestor.buscarProductoPorCodigo(entrada.nextLine().trim());
                    if (producto == null) {
                        System.out.println("Producto no encontrado");
                    } else if (producto.estaVencido()) {
                        System.out.println("No se puede agregar un producto vencido");
                    } else if (producto.getStock() <= 0) {
                        System.out.println("Sin stock disponible");
                    } else {
                        int cantidadEnCarrito = 0;
                        for (ItemComercial item : carrito) {
                            if (item == producto) {
                                cantidadEnCarrito++;
                            }
                        }
                        if (cantidadEnCarrito >= producto.getStock()) {
                            System.out.println("No hay mas stock disponible");
                        } else {
                            carrito.add(producto);
                            System.out.println("Producto agregado al carrito");
                        }
                    }
                } else if (tipoItem.equals("s")) {
                    System.out.println("Ingrese el codigo del servicio: ");
                    Servicio servicio = gestor.buscarServicioPorCodigo(entrada.nextLine().trim());
                    if (servicio == null) {
                        System.out.println("Servicio no encontrado");
                    } else {
                        carrito.add(servicio);
                        System.out.println("Servicio agregado al carrito");
                    }
                } else {
                    System.out.println("Opcion invalida");
                }

                System.out.println("Desea agregar algo mas? (s/n): ");
                seguirComprando = entrada.nextLine().trim().toLowerCase().startsWith("s");
            }

            if (carrito.isEmpty()) {
                throw new FacturaSinItemsException("No se puede generar una factura sin items");
            }
            double total = 0;
            for (ItemComercial item : carrito) {
                total += item.getPrecio();
            }
            System.out.println("Total a pagar: $" + total);
            System.out.println("Forma de pago (EFECTIVO/TARJETA/TRANSFERENCIA): ");
            FormaPago formaPago = FormaPago.valueOf(entrada.nextLine().trim().toUpperCase());
            System.out.println("Ingrese el monto que abona el cliente: ");
            double montoPagado = entrada.nextDouble();
            entrada.nextLine();

            Factura factura = gestor.registrarPago(cliente, carrito, formaPago, montoPagado);
            System.out.println("===== Factura generada =====");
            System.out.println(factura);
            System.out.println("Estado del pago: " + factura.getPago().getEstado());
        }
    }
