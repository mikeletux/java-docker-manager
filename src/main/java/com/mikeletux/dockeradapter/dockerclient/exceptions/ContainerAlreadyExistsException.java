package com.mikeletux.dockeradapter.dockerclient.exceptions;

public class ContainerAlreadyExistsException extends Exception{
    public ContainerAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
