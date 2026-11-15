package org.example.dominio.Personas;

import org.example.dominio.ObjetosClave.Departamento;
import org.example.dominio.Enums.Puesto;

public class Empleado extends Persona {
    private Puesto puesto;
    private double salario;
    private String fechaIngreso;
    private Departamento departamento;

    public Empleado(String nombre, String domicilio, int dni, String telefono, double salario, Puesto puesto, String fechaIngreso,
                    Departamento departamento) {
        super(nombre, domicilio, dni, telefono);
        this.salario = salario;
        this.puesto = puesto;
        this.fechaIngreso = fechaIngreso;
        this.departamento = departamento;

    }

    public double getSalario(){
        return salario;
    }
    public void setSalario(double salario){
        this.salario = salario;
    }
    public Puesto getPuesto(){
        return puesto;
    }
    public void setPuesto(Puesto puesto){
        this.puesto = puesto;
    }
    public Departamento getDepartamento(){
        return departamento;
    }
    public void setDepartamento(Departamento departamento){
        this.departamento = departamento;
    }
    public String getFechaIngreso(){
        return fechaIngreso;
    }

    @Override
    public String mostrarDatos(){
        return getNombre() + " | DNI: " + getDni() + " | Puesto: " + puesto + " | Salario: $" + salario;
    }
    @Override
    public String toString() {
        return getNombre() + " | DNI: " + getDni() + " | Puesto: " + puesto;
    }
    public Empleado() {}
}
