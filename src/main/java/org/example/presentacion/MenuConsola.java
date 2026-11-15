package org.example.presentacion;

import org.example.dominio.Enums.FormaPago;
import org.example.dominio.Enums.Puesto;
import org.example.dominio.Exceptions.CreditoInsuficienteException;
import org.example.dominio.Exceptions.FacturaSinItemsException;
import org.example.dominio.Exceptions.PagoExcedeDeudaException;
import org.example.dominio.ObjetosClave.Factura;
import org.example.dominio.ObjetosClave.Departamento;
import org.example.dominio.ObjetosClave.ItemComercial;
import org.example.dominio.ObjetosClave.Producto;
import org.example.dominio.ObjetosClave.Servicio;
import org.example.dominio.Personas.Cliente;
import org.example.dominio.Personas.Empleado;
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
        while (opcion != 12) {
            System.out.println("1.Registrar empleado");
            System.out.println("2.Mostrar empleados");
            System.out.println("3.Asignar Responsable");
            System.out.println("4.Mostrar Departamentos");
            System.out.println("5.Emitir factura y registrar pago");
            System.out.println("6.Mostrar catalogo");
            System.out.println("7.Mostrar productos vencidos");
            System.out.println("8.Mostrar facturas ordenadas por numero");
            System.out.println("9.Mostrar facturas ordenadas por total");
            System.out.println("10.Registrar cliente");
            System.out.println("11.Guardar datos");
            System.out.println("12.Salir");
            opcion = entrada.nextInt();
            entrada.nextLine();
            switch (opcion) {
                case 1:
                    registrarEmpleadoInteractivo();
                    break;
                case 2:
                    mostrarEmpleados();
                    break;
                case 3:
                    asignarResponsableInteractivo();
                    break;
                case 4:
                    mostrarDepartamentos();
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
                    mostrarCatalogo();
                    break;
                case 7:
                    mostrarProductosVencidos();
                    break;
                case 8:
                    mostrarFacturas(gestor.obtenerFacturasOrdenadasPorNumero());
                    break;
                case 9:
                    mostrarFacturas(gestor.obtenerFacturasOrdenadasPorTotal());
                    break;
                case 10:
                    registrarClienteInteractivo();
                    break;
                case 11:
                    guardarDatosInteractivo();
                    break;
                case 12:
                    guardarDatosInteractivo();
                    break;
                default:
                    System.out.println("Opcion invalida");
            }
        }
    }

    private void registrarEmpleadoInteractivo() {
        System.out.println("=== Registro de Empleados ===");
        System.out.println("== Informacion personal ==");
        System.out.println("Ingrese el nombre: ");
        String nombre = entrada.nextLine();
        System.out.println("Ingrese el domicilio: ");
        String domicilio = entrada.nextLine();
        System.out.println("Ingrese el dni: ");
        int dni = entrada.nextInt();
        entrada.nextLine();
        System.out.println("Ingrese el numero de telefono: ");
        String telefono = entrada.nextLine();
        System.out.println("== Informacion de empleo ==");
        System.out.println("Puesto (ADMINISTRATIVO / TECNICO / GERENTE): ");
        Puesto puesto = Puesto.valueOf(entrada.nextLine().trim().toUpperCase());
        System.out.println("Ingrese el salario: ");
        double salario = entrada.nextDouble();
        entrada.nextLine();
        System.out.println("Ingrese la fecha de ingreso: ");
        String fechaIngreso = entrada.nextLine();
        System.out.println("Departamentos disponibles: ");
        for (Departamento departamento : gestor.obtenerDepartamentos()) {
            System.out.println("-" + departamento.getNombre());
        }
        System.out.println("Ingrese el departamento a ingresar: ");
        String nombreDepartamento = entrada.nextLine();
        try {
            Empleado empleado = gestor.registrarEmpleado(nombre, domicilio, dni, telefono,
                    puesto, salario, fechaIngreso, nombreDepartamento);
            System.out.println("--- Empleado " + empleado.getNombre() + " registrado ---");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void mostrarEmpleados() {
        List<Empleado> empleados = gestor.obtenerEmpleados();
        if (empleados.isEmpty()) {
            System.out.println("No hay empleados registrados");
            return;
        }
        System.out.println("");
        System.out.println("=== Empleados ===");
        int contador = 0;
        for (Empleado empleado : empleados) {
            contador++;
            System.out.println("Empleado " + "(" + contador + ")");
            System.out.println(empleado.mostrarDatos());
            System.out.println("Domicilio " + empleado.getDomicilio());
            System.out.println("Telefono: " + empleado.getTelefono());
            System.out.println("Puesto: " + empleado.getPuesto());
            System.out.println("Salario: " + empleado.getSalario());
            System.out.println("Fecha de ingreso: " + empleado.getFechaIngreso());
            System.out.println("Departamento: " + empleado.getDepartamento().getNombre());
            System.out.println("===================================");
        }
    }

    private void asignarResponsableInteractivo() {
        if (gestor.obtenerEmpleados().isEmpty()) {
            System.out.println("No hay empleados para asignar responsable");
            return;
        }
        List<Departamento> departamentos = gestor.obtenerDepartamentosSinResponsable();
        if (departamentos.isEmpty()) {
            System.out.println("No hay departamentos sin responsable por el momento");
            return;
        }
        System.out.println("Departamentos sin responsable asignado: ");
        for (Departamento departamento : departamentos) {
            System.out.println("Nombre: " + departamento.getNombre());
            System.out.println("Presupuesto: " + departamento.getPresupuesto());
            System.out.println("Empleados: ");
            for (Empleado empleado : departamento.getListaEmpleados()) {
                System.out.println(empleado.getNombre() + " | " + empleado.getPuesto());
                System.out.println("--------------------------------------------");
            }
        }
        System.out.println("Ingrese el nombre del departamento a asignar responsable: ");
        String nombreDepartamento = entrada.nextLine().trim();
        boolean departamentoDisponible = false;
        for (Departamento departamento : departamentos) {
            if (departamento.getNombre().equalsIgnoreCase(nombreDepartamento)) {
                departamentoDisponible = true;
                break;
            }
        }
        if (!departamentoDisponible) {
            System.out.println("Ese departamento no existe o ya tiene responsable");
            return;
        }
        System.out.println("A continuacion ingrese el nombre del empleado responsable: ");
        String nombreEmpleado = entrada.nextLine().trim();
        try {
            Empleado responsable = gestor.asignarResponsable(nombreDepartamento, nombreEmpleado);
            System.out.println("Responsable " + responsable.getNombre()
                    + " asignado al departamento "
                    + responsable.getDepartamento().getNombre());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void mostrarDepartamentos() {
        System.out.println("");
        System.out.println("==== Departamentos ===");
        for (Departamento departamento : gestor.obtenerDepartamentos()) {
            System.out.println("Nombre: " + departamento.getNombre());
            System.out.println("Presupuesto: " + departamento.getPresupuesto());
            if (departamento.getResponsable() == null) {
                System.out.println("Responsable: nadie");
            } else {
                System.out.println("Responsable: "
                        + departamento.getResponsable().mostrarDatos());
            }
            if (departamento.getListaEmpleados().isEmpty()) {
                System.out.println("No hay empleados en este departamento");
                System.out.println("==================================");
            } else {
                System.out.println("Empleados: ");
                for (Empleado empleado : departamento.getListaEmpleados()) {
                    System.out.println(empleado.getNombre() + " | " + empleado.getPuesto());
                    System.out.println("==================================");
                }
            }
        }
    }

    private void mostrarCatalogo() {
        System.out.println("=== Catalogo ===");
        for (ItemComercial item : gestor.obtenerCatalogo()) {
            System.out.println(item.getTipo() + " | " + item.getCodigo() + " | "
                    + item.mostrarResumen());
        }
    }

    private void mostrarProductosVencidos() {
        System.out.println("=== Productos vencidos ===");
        List<Producto> productosVencidos = gestor.obtenerProductosVencidos();
        if (productosVencidos.isEmpty()) {
            System.out.println("No hay productos vencidos");
            return;
        }
        for (Producto producto : productosVencidos) {
            System.out.println(producto.getCodigo() + " | " + producto.mostrarResumen()
                    + " | Vencimiento: " + producto.getFechaVencimiento());
        }
    }

    private void mostrarFacturas(List<Factura> facturas) {
        for (Factura factura : facturas) {
            System.out.println(factura.getNumero() + " - $" + factura.getTotal());
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
            cliente = registrarClienteInteractivo();
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

    private Cliente registrarClienteInteractivo() {
        System.out.println("--- Registro de cliente ---");
        System.out.println("Ingrese el nombre del cliente: ");
        String nombre = entrada.nextLine();
        System.out.println("Ingrese el dni del cliente: ");
        int dni = entrada.nextInt();
        entrada.nextLine();

        Cliente cliente = gestor.registrarCliente(nombre, dni);
        System.out.println("Cliente registrado con categoria REGULAR y credito de $"
                + cliente.getLimiteCredito());
        return cliente;
    }

    private void guardarDatosInteractivo() {
        try {
            gestor.guardarDatos();
            System.out.println("Datos guardados correctamente");
        } catch (IOException e) {
            System.out.println("No se pudieron guardar todos los datos: " + e.getMessage());
        }
    }
}
