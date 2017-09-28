package com.secomp.clinica.model.enums;

public enum Role {
    ROLE_SECRETARIA("Secretaria");

    private String role;

    public String getRole() {
        return role;
    }

    Role(String role) {
        this.role = role;
    }
}
