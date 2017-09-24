package com.secomp.clinica.model.enums;

public enum Sexo {

    M("Masculino"), F("Feminino"), OUTRO("Outro");

    private String sexo;

    public String getSexo() {
        return sexo;
    }

    Sexo(String sexo) {
        this.sexo = sexo;
    }
}