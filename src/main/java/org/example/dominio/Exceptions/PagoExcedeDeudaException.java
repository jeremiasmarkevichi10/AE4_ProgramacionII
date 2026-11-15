package org.example.dominio.Exceptions;

public class PagoExcedeDeudaException extends Exception {
    public PagoExcedeDeudaException(String mensaje) {
        super(mensaje);
    }
}
