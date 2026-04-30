package com.example.bankcards.exception.exceptions;

import java.util.NoSuchElementException;


public class NotFoundException extends NoSuchElementException {

    public NotFoundException(String msg) {
        super(msg);
    }

}
