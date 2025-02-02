package br.com.ambevtech.ordermanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SupplierDuplicateEmailException extends RuntimeException {
    public SupplierDuplicateEmailException(String message) {
        super(message);
    }
}
