package com.mikeletux.dockeradapter.dockerclient.exceptions;

public class ContainerIsStoppedException extends Exception{
    public ContainerIsStoppedException(String errorMessage) {
        super(errorMessage);
    }
}
