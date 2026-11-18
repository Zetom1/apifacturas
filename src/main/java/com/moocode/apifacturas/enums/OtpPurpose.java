package com.moocode.apifacturas.enums;

public enum OtpPurpose {
    REGISTRATION("verificar tu correo"),
    LOGIN("iniciar sesión"),
    PASSWORD_RESET("recuperar tu contraseña");

    private final String description;
    OtpPurpose(String description) {
        this.description = description;
    }

    public String getDescription() {return  description;}
}
