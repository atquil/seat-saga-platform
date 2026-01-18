package com.atquil.seatsagaplatform.exception;

/**
 * @author atquil
 */
public class SeatNotAvailableException extends RuntimeException {
    public SeatNotAvailableException(String message) {
        super(message);
    }
}
