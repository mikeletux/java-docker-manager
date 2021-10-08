package com.mikeletux.dockeradapter.dockerclient.exceptions;

public class DockerInternalServerErrorException extends Exception{
    public DockerInternalServerErrorException(String errorMessage) {
        super(errorMessage);
    }
}
