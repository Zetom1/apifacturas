package com.moocode.apifacturas.exception;

public class InvalidCredentialException extends RuntimeException {

	public InvalidCredentialException() {
		super("Correo electrónico o contraseña incorrecta");
	}
}
