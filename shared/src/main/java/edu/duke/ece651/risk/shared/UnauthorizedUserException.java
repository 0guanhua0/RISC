package edu.duke.ece651.risk.shared;

public class UnauthorizedUserException extends Exception{
    public UnauthorizedUserException(String message) {
        super(message);
    }
}
