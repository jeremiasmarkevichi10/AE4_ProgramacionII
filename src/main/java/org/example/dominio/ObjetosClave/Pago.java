package org.example.dominio.ObjetosClave;

import org.example.dominio.Enums.Estado;
import org.example.dominio.Enums.FormaPago;

public class Pago {
    private double monto;
    private String fecha;
    private FormaPago formapago;
    private Estado estado;

    public Pago(double monto, String fecha, FormaPago formapago,
                Estado estado){
        this.monto = monto;
        this.fecha = fecha;
        this.formapago = formapago;
        this.estado = estado;
    }
    public double getMonto(){
        return monto;
    }
    public void setMonto(double monto) {
        this.monto = monto;
    }
    public Pago() {}
    public String getFecha(){
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public FormaPago getFormapago(){
        return formapago;
    }
    public void setFormapago(FormaPago formapago) {
        this.formapago = formapago;
    }
    public Estado getEstado(){
        return estado;
    }
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}
