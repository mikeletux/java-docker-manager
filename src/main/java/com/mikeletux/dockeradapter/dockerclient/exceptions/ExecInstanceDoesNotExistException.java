package com.mikeletux.dockeradapter.dockerclient.exceptions;

public class ExecInstanceDoesNotExistException extends Exception{
    public ExecInstanceDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
