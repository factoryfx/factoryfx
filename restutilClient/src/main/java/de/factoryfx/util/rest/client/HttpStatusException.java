package de.factoryfx.util.rest.client;

public class HttpStatusException extends RuntimeException {

    public final int status;

    public HttpStatusException(int status, String message) {
        super(message);
        this.status = status;
    }
}
