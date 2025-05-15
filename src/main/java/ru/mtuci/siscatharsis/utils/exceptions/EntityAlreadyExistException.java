package ru.mtuci.siscatharsis.utils.exceptions;

public class EntityAlreadyExistException extends RuntimeException {
    public EntityAlreadyExistException(String message) {
        super(message);
    }
}
