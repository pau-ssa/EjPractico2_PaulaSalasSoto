
package com.EjPractico2_PaulaSalasSoto.EjPractico2.domain;

public enum Especialidad {
    CARDIOLOGIA ("CARDIOLOGIA"),
    DERMATOLOGIA("DERMATOLOGIA"),
    PEDIATRIA("PEDIATRIA"),
    NEUROLOGIA("NEUROLOGIA");

    private final String valorBD;

    Especialidad(String valorBD) {
        this.valorBD = valorBD;
    }

    public String getValorBD() {
        return valorBD;
    }
}
  