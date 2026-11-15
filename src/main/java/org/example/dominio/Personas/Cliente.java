package org.example.dominio.Personas;
import org.example.dominio.Enums.Categoria;
import org.example.dominio.Exceptions.CreditoInsuficienteException;
import org.example.dominio.ObjetosClave.Factura;
import org.example.dominio.ObjetosClave.ItemComercial;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.ArrayList;
public class Cliente extends Persona {
    private int limiteCredito;
    private Categoria categoria;
    private List<Factura> historialCompras = new ArrayList<>();

    public Cliente(String nombre, String domicilio, int dni, String telefono,
                   int limiteCredito, Categoria categoria) {
        super(nombre, domicilio, dni, telefono);
        this.limiteCredito = limiteCredito;
        this.categoria = categoria;
        this.historialCompras = new ArrayList<>();
    }
    public Cliente(String nombre, int dni, int limiteCredito) {
        this(nombre, "Sin especificar", dni, "0000-0000", limiteCredito, Categoria.REGULAR);
    }
    public int getLimiteCredito() {
        return limiteCredito;
    }

    public void validarCompra(double deuda) throws CreditoInsuficienteException {
        if (deuda > limiteCredito) {
            throw new CreditoInsuficienteException(getNombre() + " supera su limite de credito permitido");
        }
    }

    public void setLimiteCredito(int limiteCredito) {
        this.limiteCredito = limiteCredito;
    }
    public Categoria getCategoria(){
        return categoria;
    }
    public void setCategoria(Categoria categoria){
        this.categoria = categoria;
    }
    @JsonIgnore
    public List<Factura> getHistorialCompras(){
        return historialCompras;
    }

    public void setHistorialCompras(Factura factura) {
        historialCompras.add(factura);
    }

    @Override
    public String mostrarDatos(){
        return getNombre() + " | DNI: " + getDni() + " | Categoria: " + categoria + " | Credito: $" + limiteCredito;
    }
    public Cliente() {}
}

