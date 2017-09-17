package com.secomp.clinica.model.enuns;

public enum TipoConsulta {

    NORMAL("Normal"),
    RETORNO("Retorno");

    private String consulta;

    public String getConsulta() {
        return consulta;
    }

    TipoConsulta(String consulta) {
        this.consulta = consulta;
    }
}