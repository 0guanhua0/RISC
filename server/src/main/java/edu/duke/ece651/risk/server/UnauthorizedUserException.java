package edu.duke.ece651.risk.server;

public class UnauthorizedUserException extends Exception{
    public UnauthorizedUserException(String message) {
        super(message);
    }
}
