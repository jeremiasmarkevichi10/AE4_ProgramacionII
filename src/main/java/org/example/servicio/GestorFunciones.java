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
import org.example.dominio.ObjetosClave.*;
import org.example.dominio.RepositorioClientes;
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
    private final RepositorioClientes clienteRepository;
    private final Map<Categoria, PoliticaCredito> politicasCredito;
    private final FabricaProducto fabricaProducto;
    private final FabricaServicio fabricaServicio;

    public GestorFunciones(RepositorioFacturas facturaRepository,
                           RepositorioClientes clienteRepository) throws IOException {
        this(facturaRepository, clienteRepository, PoliticasCredito.estandar());
    }

    public GestorFunciones(RepositorioFacturas facturaRepository,
                           RepositorioClientes clienteRepository,
                           Collection<PoliticaCredito> politicasCredito) throws IOException {
        this(facturaRepository, clienteRepository, politicasCredito,
                new FabricaProducto(), new FabricaServicio());
    }

    public GestorFunciones(RepositorioFacturas facturaRepository,
                           RepositorioClientes clienteRepository,
                           Collection<PoliticaCredito> politicasCredito,
                           FabricaProducto fabricaProducto, FabricaServicio fabricaServicio)
            throws IOException {
        this.facturaRepository = facturaRepository;
        this.clienteRepository = clienteRepository;
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
        this.listaProductos = new ArrayList<>();
        this.listaServicios = new ArrayList<>();
        this.listaFacturas = facturaRepository.obtenerTodas();
        this.listaClientes = clienteRepository.obtenerTodos();
        reconstruirHistorialesDeCompra();
    }

    public List<ItemComercial> obtenerCatalogo() {
        List<ItemComercial> catalogo = new ArrayList<>();
        catalogo.addAll(listaProductos);
        catalogo.addAll(listaServicios);
        return Collections.unmodifiableList(catalogo);
    }

    public List<Producto> obtenerProductosVencidos() {
        List<Producto> productosVencidos = new ArrayList<>();
        for (Producto producto : listaProductos) {
            if (producto.estaVencido()) {
                productosVencidos.add(producto);
            }
        }
        return Collections.unmodifiableList(productosVencidos);
    }

    Random random = new Random();
    int limiteCredito = 30000 + (random.nextInt(20001));

    public Empleado registrarEmpleado(String nombre, String domicilio, int dni, String telefono,
                                      Puesto puesto, double salario, String fechaIngreso,
                                      String departamentoElegido) {
        Departamento departamento = buscarDepartamentoPorNombre(departamentoElegido);
        if (departamento == null) {
            throw new IllegalArgumentException(
                    "Ese departamento no existe. No se puede registrar el empleado");
        }
        Empleado nuevoEmpleado = new Empleado(nombre, domicilio, dni, telefono, salario, puesto, fechaIngreso, departamento);
        departamento.agregarEmpleado(nuevoEmpleado);
        listaEmpleados.add(nuevoEmpleado);
        return nuevoEmpleado;
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

    public List<Empleado> obtenerEmpleados() {
        return Collections.unmodifiableList(listaEmpleados);
    }

    public List<Departamento> obtenerDepartamentos() {
        return Collections.unmodifiableList(listaDepartamentos);
    }

    public List<Departamento> obtenerDepartamentosSinResponsable() {
        List<Departamento> departamentosSinResponsable = new ArrayList<>();
        for (Departamento departamento : listaDepartamentos) {
            if (departamento.getResponsable() == null) {
                departamentosSinResponsable.add(departamento);
            }
        }
        return Collections.unmodifiableList(departamentosSinResponsable);
    }

    public Empleado asignarResponsable(String departamentoElegido, String empleadoElegido) {
        if (listaEmpleados.isEmpty()) {
            throw new IllegalStateException("No hay empleados para asignar responsable");
        }
        List<Departamento> departamentosFaltantes = obtenerDepartamentosSinResponsable();
        if (departamentosFaltantes.isEmpty()) {
            throw new IllegalStateException(
                    "No hay departamentos sin responsable por el momento");
        }
        for (Departamento r : departamentosFaltantes) {
            if (departamentoElegido.equalsIgnoreCase(r.getNombre())) {
                for (Empleado t : r.getListaEmpleados()) {
                    if (empleadoElegido.equalsIgnoreCase(t.getNombre())) {
                        r.setResponsable(t);
                        return t;
                    }
                }
                throw new IllegalArgumentException(
                        "Ese empleado no pertenece a ese departamento");
            }
        }
        throw new IllegalArgumentException(
                "Ese departamento no existe o ya tiene responsable");
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

        public void guardarDatos() throws IOException {
            clienteRepository.guardarTodos(listaClientes);
            facturaRepository.guardarTodas(listaFacturas);
        }

        private void reconstruirHistorialesDeCompra() {
            Map<Integer, Cliente> clientesPorDni = new HashMap<>();
            for (Cliente cliente : listaClientes) {
                clientesPorDni.putIfAbsent(cliente.getDni(), cliente);
            }
            for (Factura factura : listaFacturas) {
                Cliente clienteDeFactura = factura.getCliente();
                if (clienteDeFactura == null) {
                    continue;
                }
                Cliente cliente = clientesPorDni.get(clienteDeFactura.getDni());
                if (cliente == null) {
                    cliente = clienteDeFactura;
                    listaClientes.add(cliente);
                    clientesPorDni.put(cliente.getDni(), cliente);
                } else {
                    factura.setCliente(cliente);
                }
                cliente.setHistorialCompras(factura);
            }
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


    public List<Factura> obtenerFacturasOrdenadasPorNumero() {
        List<Factura> facturasOrdenadas = new ArrayList<>(listaFacturas);
        Collections.sort(facturasOrdenadas);
        return Collections.unmodifiableList(facturasOrdenadas);
    }

    public List<Factura> obtenerFacturasOrdenadasPorTotal() {
        List<Factura> facturasOrdenadas = new ArrayList<>(listaFacturas);
        Collections.sort(facturasOrdenadas, new ComparadorPorTotal());
        return Collections.unmodifiableList(facturasOrdenadas);
    }
    }
