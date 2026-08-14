    
package com.EjPractico2_PaulaSalasSoto.EjPractico2.domain;

public enum EstadoCita {
    PROGRAMADA("PROGRAMADA"),
    COMPLETADA("COMPLETADA"),
    CANCELADA("CANCELADA");

    private final String valorBD;

    EstadoCita(String valorBD) {
        this.valorBD = valorBD;
    }

    public String getValorBD() {
        return valorBD;
    }
}