package org.example.servicio;
import org.example.dominio.Enums.Categoria;
import org.example.dominio.Enums.Estado;
import org.example.dominio.Enums.FormaPago;
import org.example.dominio.Enums.Puesto;
import org.example.dominio.Exceptions.CreditoInsuficienteException;
import org.example.dominio.Exceptions.FacturaSinItemsException;
import org.example.dominio.Exceptions.PagoExcedeDeudaException;
import org.example.dominio.ComparadorPorTotal;
import org.example.dominio.Fabricas.FabricaProducto;
import org.example.dominio.Fabricas.FabricaServicio;
import org.example.dominio.Interfaces.Identificable;
import org.example.dominio.ObjetosClave.*;
import org.example.dominio.RepositorioFacturas;
import org.example.dominio.Personas.Cliente;
import org.example.dominio.Personas.Empleado;
import org.example.dominio.Personas.Proveedor;
import org.example.dominio.PoliticasCredito.PoliticaCredito;
import org.example.dominio.PoliticasCredito.PoliticasCredito;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumMap;


import java.util.*;

public class GestorFunciones {
    private List<Departamento> listaDepartamentos;
    private List<Empleado> listaEmpleados;
    private List<Cliente> listaClientes;
    private List<Producto> listaProductos;
    private List<Servicio> listaServicios;
    private List<Factura> listaFacturas;
    private final RepositorioFacturas facturaRepository;
    private final Map<Categoria, PoliticaCredito> politicasCredito;
    private final FabricaProducto fabricaProducto;
    private final FabricaServicio fabricaServicio;

    public GestorFunciones(RepositorioFacturas facturaRepository) {
        this(facturaRepository, new Scanner(System.in));
    }

    public GestorFunciones(RepositorioFacturas facturaRepository, Scanner entrada) {
        this(facturaRepository, entrada, PoliticasCredito.estandar(),
                new FabricaProducto(), new FabricaServicio());
    }

    public GestorFunciones(RepositorioFacturas facturaRepository, Scanner entrada,
                           Collection<PoliticaCredito> politicasCredito) {
        this(facturaRepository, entrada, politicasCredito,
                new FabricaProducto(), new FabricaServicio());
    }

    public GestorFunciones(RepositorioFacturas facturaRepository, Scanner entrada,
                           Collection<PoliticaCredito> politicasCredito,
                           FabricaProducto fabricaProducto, FabricaServicio fabricaServicio) {
        this.facturaRepository = facturaRepository;
        this.entrada = entrada;
        this.fabricaProducto = fabricaProducto;
        this.fabricaServicio = fabricaServicio;
        this.politicasCredito = new EnumMap<>(Categoria.class);
        for (PoliticaCredito politica : politicasCredito) {
            PoliticaCredito anterior = this.politicasCredito.putIfAbsent(
                    politica.getCategoria(), politica);
            if (anterior != null) {
                throw new IllegalArgumentException(
                        "Ya existe una politica de credito para " + politica.getCategoria());
            }
        }
        this.listaDepartamentos = new ArrayList<>();
        this.listaEmpleados = new ArrayList<>();
        this.listaClientes = new ArrayList<>();
        this.listaProductos = new ArrayList<>();
        this.listaServicios = new ArrayList<>();
        this.listaFacturas = new ArrayList<>();
        try {
            this.listaFacturas = facturaRepository.obtenerTodas();
        } catch (IOException e) {
            System.out.println("No se pudo leer facturas, se arranca vacio: " + e.getMessage());
            this.listaFacturas = new ArrayList<>();
        }
    }

    public void mostrarCatalogo() {
        List<Identificable> catalogo = new ArrayList<>();
        catalogo.addAll(listaProductos);
        catalogo.addAll(listaServicios);

        System.out.println("=== Catalogo ===");
        for (Identificable item : catalogo) {
            ItemComercial comercial = (ItemComercial) item;
            System.out.println(item.getTipo() + " | " + comercial.getCodigo() + " | "
                    + comercial.mostrarResumen());
        }
    }

    public void mostrarProductosVencidos() {
        System.out.println("=== Productos vencidos ===");
        boolean hayVencidos = false;
        for (Producto producto : listaProductos) {
            if (producto.estaVencido()) {
                System.out.println(producto.getCodigo() + " | " + producto.mostrarResumen()
                        + " | Vencimiento: " + producto.getFechaVencimiento());
                hayVencidos = true;
            }
        }
        if (!hayVencidos) {
            System.out.println("No hay productos vencidos");
        }
    }

    private final Scanner entrada;
    int contador = 0;
    Random random = new Random();
    int limiteCredito = 30000 + (random.nextInt(20001));

    public void registrarEmpleado() {
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
        String puestoTexto = entrada.nextLine().trim().toUpperCase();
        Puesto puesto = Puesto.valueOf(puestoTexto);
        System.out.println("Ingrese el salario: ");
        double salario = entrada.nextDouble();
        entrada.nextLine();
        System.out.println("Ingrese la fecha de ingreso: ");
        String fechaIngreso = entrada.nextLine();
        System.out.println("Departamentos disponibles: ");
        for (Departamento t : listaDepartamentos) {
            System.out.println("-" + t.getNombre());
        }
        System.out.println("Ingrese el departamento a ingresar: ");
        String departamentoElegido = entrada.nextLine();
        Departamento departamento = buscarDepartamentoPorNombre(departamentoElegido);
        if (departamento == null) {
            System.out.println("Ese departamento no existe. No se puede registrar el empleado");
            return;
        }
        Empleado nuevoEmpleado = new Empleado(nombre, domicilio, dni, telefono, salario, puesto, fechaIngreso, departamento);
        departamento.agregarEmpleado(nuevoEmpleado);
        listaEmpleados.add(nuevoEmpleado);
        System.out.println("--- Empleado " + nuevoEmpleado.getNombre() + " registrado ---");
    }

    public void agregarDepartamento(Departamento d) {
        listaDepartamentos.add(d);
    }

    public void inicializarDatosDePrueba() {
        Proveedor proveedor = new Proveedor("Distribuidora Sur", "Ruta 12 km 5", 20111222, "1235343235",
                "Distribuidora Sur SRL", "30-12345678-9");

        agregarDepartamento(new Departamento("Sistemas", 500000, null));
        agregarDepartamento(new Departamento("Ventas", 300000, null));

        Producto mouse = fabricaProducto.crear("Mouse", proveedor);
        Producto teclado = fabricaProducto.crear("Teclado", proveedor);
        Producto pilas = fabricaProducto.crear("Pilas alcalinas", proveedor);
        listaProductos.add(mouse);
        listaProductos.add(teclado);
        listaProductos.add(pilas);
        pilas.setFechaVencimiento("2025-01-01");
        teclado.setFechaVencimiento(LocalDate.now().plusDays(30).toString());
        listaServicios.add(fabricaServicio.crear("Limpieza de hardware", proveedor));
        listaServicios.add(fabricaServicio.crear("Formateo de equipo", proveedor));
    }

    private Departamento buscarDepartamentoPorNombre(String nombreBuscado) {
        for (Departamento t : listaDepartamentos) {
            if (t.getNombre().equalsIgnoreCase(nombreBuscado)) {
                return t;
            }

        }
        return null;
    }

    public void mostrarEmpleados() {
        if (listaEmpleados.isEmpty()) {
            System.out.println("No hay empleados registrados");
        }
        System.out.println("");
        System.out.println("=== Empleados ===");
        for (Empleado e : listaEmpleados) {
            contador++;
            System.out.println("Empleado " + "(" + contador + ")");
            System.out.println(e.mostrarDatos());
            System.out.println("Domicilio " + e.getDomicilio());
            System.out.println("Telefono: " + e.getTelefono());
            System.out.println("Puesto: " + e.getPuesto());
            System.out.println("Salario: " + e.getSalario());
            System.out.println("Fecha de ingreso: " + e.getFechaIngreso());
            System.out.println("Departamento: " + e.getDepartamento().getNombre());
            System.out.println("===================================");
        }
    }

    public void asignarResponsable() {
        if (listaEmpleados.isEmpty()) {
            System.out.println("No hay empleados para asignar responsable");
            return;
        }
        List<Departamento> departamentosFaltantes = new ArrayList<>();
        for (Departamento t : listaDepartamentos) {
            if (t.getResponsable() == null) {
                departamentosFaltantes.add(t);
            }
        }
        if (departamentosFaltantes.isEmpty()) {
            System.out.println("No hay departamentos sin responsable por el momento");
            return;
        }
        System.out.println("Departamentos sin responsable asignado: ");
        for (Departamento r : departamentosFaltantes) {
            System.out.println("Nombre: " + r.getNombre());
            System.out.println("Presupuesto: " + r.getPresupuesto());
            System.out.println("Empleados: ");
            for (Empleado y : r.getListaEmpleados()) {
                System.out.println(y.getNombre() + " | " + y.getPuesto());
                System.out.println("--------------------------------------------");
            }
        }
        System.out.println("Ingrese el nombre del departamento a asignar responsable: ");
        boolean departamentoEncontrado = false;
        String departamentoElegido = entrada.nextLine().trim();
        for (Departamento r : departamentosFaltantes) {
            if (departamentoElegido.equalsIgnoreCase(r.getNombre())) {
                departamentoEncontrado = true;
                System.out.println("A continuacion ingrese el nombre del empleado responsable: ");
                String empleadoElegido = entrada.nextLine().trim();
                boolean empleadoEncontrado = false;
                for (Empleado t : r.getListaEmpleados()) {
                    if (empleadoElegido.equalsIgnoreCase(t.getNombre())) {
                        empleadoEncontrado = true;
                        r.setResponsable(t);
                        System.out.println("Responsable " + t.getNombre() + " asignado al departamento " + r.getNombre());
                        break;
                    }
                }
                if (!empleadoEncontrado) {
                    System.out.println("Ese empleado no pertenece a ese departamento");
                }
                break;
            }
        }
        if (!departamentoEncontrado) {
            System.out.println("Ese departamento no existe o ya tiene responsable");
        }

    }

    public void mostrarDepartamentos() {
        System.out.println("");
        System.out.println("==== Departamentos ===");
        for (Departamento t : listaDepartamentos) {
            System.out.println("Nombre: " + t.getNombre());
            System.out.println("Presupuesto: " + t.getPresupuesto());
            if (t.getResponsable() == null) {
                System.out.println("Responsable: nadie");
            } else {
                System.out.println("Responsable: " + t.getResponsable().mostrarDatos());
            }
            if (t.getListaEmpleados().isEmpty()) {
                System.out.println("No hay empleados en este departamento");
                System.out.println("==================================");
            } else {
                System.out.println("Empleados: ");
                for (Empleado y : t.getListaEmpleados()) {
                    System.out.println(y.getNombre() + " | " + y.getPuesto());
                    System.out.println("==================================");
                }
            }
        }
    }

        public Cliente buscarCliente (String nombre,int dni){
            for (Cliente c : listaClientes) {
                if (c.getNombre().equalsIgnoreCase(nombre) && c.getDni() == dni) {
                    return c;
                }
            }
            return null;
        }

        public Cliente registrarCliente(String nombre, int dni) {
            return registrarCliente(nombre, dni, Categoria.REGULAR);
        }

        public Cliente registrarCliente(String nombre, int dni, Categoria categoria) {
            Cliente cliente = new Cliente(nombre, "Sin especificar", dni, "0000-0000",
                    calcularLimiteCredito(categoria), categoria);
            listaClientes.add(cliente);
            return cliente;
        }

        public List<Producto> obtenerProductos() {
            return Collections.unmodifiableList(listaProductos);
        }

        public List<Servicio> obtenerServicios() {
            return Collections.unmodifiableList(listaServicios);
        }

        public Producto buscarProductoPorCodigo(String codigo) {
            for (Producto producto : listaProductos) {
                if (codigo.equalsIgnoreCase(producto.getCodigo())) {
                    return producto;
                }
            }
            return null;
        }

        public Servicio buscarServicioPorCodigo(String codigo) {
            for (Servicio servicio : listaServicios) {
                if (codigo.equalsIgnoreCase(servicio.getCodigo())) {
                    return servicio;
                }
            }
            return null;
        }
        private int calcularLimiteCredito(Categoria categoria) {
            PoliticaCredito politica = politicasCredito.get(categoria);
            if (politica == null) {
                throw new IllegalArgumentException(
                        "No hay una politica de credito configurada para " + categoria);
            }
            return politica.calcularLimiteCredito();
        }

        private String generarCodigoAleatorio () {
            String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            StringBuilder codigo = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                int indice = random.nextInt(letras.length());
                codigo.append(letras.charAt(indice));
            }
            return codigo.toString();
        }
    public Factura registrarPago(Cliente cliente, List<ItemComercial> carrito,
                                 FormaPago formaPago, double montoPagado)
            throws FacturaSinItemsException, CreditoInsuficienteException,
            PagoExcedeDeudaException, IOException {
        if (carrito.isEmpty()) {
            throw new FacturaSinItemsException("No se puede generar una factura sin items");
        }

        double total = 0;
        for (ItemComercial item : carrito) {
            total += item.getPrecio();
        }

        if (montoPagado > total) {
            throw new PagoExcedeDeudaException("El monto abonado no puede superar el total de la factura");
        }
        if (montoPagado < total) {
            cliente.validarCompra(total - montoPagado);
        }

        Estado estado;
        if (montoPagado <= 0) {
            estado = Estado.PENDIENTE;
        } else if (montoPagado < total) {
            estado = Estado.PARCIAL;
        } else {
            estado = Estado.CANCELADO;
        }

        Pago pago = new Pago(montoPagado, java.time.LocalDate.now().toString(), formaPago, estado);
        String numeroFactura = generarCodigoAleatorio();
        Factura factura = new Factura(numeroFactura, java.time.LocalDate.now().toString(), cliente, carrito, formaPago, pago);

        cliente.getHistorialCompras().add(factura);
        listaFacturas.add(factura);
        for (ItemComercial item : carrito) {
            if (item instanceof Producto) {
                ((Producto) item).setStockResta(1);
            }
        }
        facturaRepository.guardarTodas(listaFacturas);
        return factura;
    }


    public void mostrarFacturasOrdenadasPorNumero() {
        List<Factura> facturasOrdenadas = new ArrayList<>(listaFacturas);
        Collections.sort(facturasOrdenadas);
        for (Factura f : facturasOrdenadas) {
            System.out.println(f.getNumero() + " - $" + f.getTotal());
        }
    }

    public void mostrarFacturasOrdenadasPorTotal() {
        List<Factura> facturasOrdenadas = new ArrayList<>(listaFacturas);
        Collections.sort(facturasOrdenadas, new ComparadorPorTotal());
        for (Factura f : facturasOrdenadas) {
            System.out.println(f.getNumero() + " - $" + f.getTotal());
        }
    }
    }
