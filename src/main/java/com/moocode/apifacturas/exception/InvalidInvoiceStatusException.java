package com.moocode.apifacturas.exception;

public class InvalidInvoiceStatusException extends RuntimeException {

	public InvalidInvoiceStatusException(String message) {
		super(message);
	}
}
