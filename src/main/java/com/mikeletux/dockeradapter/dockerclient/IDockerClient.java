package com.mikeletux.dockeradapter.dockerclient;

import com.mikeletux.dockeradapter.dockerclient.exceptions.ContainerDoesNotExistException;
import com.mikeletux.dockeradapter.dockerclient.exceptions.*;


import java.io.IOException;
import java.lang.InterruptedException;
import java.util.List;

public interface IDockerClient {
    // checkIfImageAlreadyExists figures out if an image is already in the local repository
    // Returns true if it is available in the local registry, false otherwise.
    boolean checkIfImageAlreadyExists(String dockerImage, String tag) throws IOException, InterruptedException, DockerInternalServerErrorException;

    // pullImageFromRegistry pulls an image from the docker registry given a docker Image name, image tag and image architecture
    void pullImageFromRegistry(String dockerImage, String tag, String arch) throws IOException, InterruptedException, ImageDoesNotExistException, DockerInternalServerErrorException;
    
    // createContainer creates a a container given a container name, image name, image tag and list of commands for cmd. 
    // It returns the ID of the new created container
    String createContainer(String containerName, String image, String tag, List<String> cmd) throws IOException, InterruptedException, ImageDoesNotExistException, ContainerAlreadyExistsException, DockerInternalServerErrorException;
    
    // runContainer starts a new container given a container ID.
    void runContainer(String containerId) throws IOException, InterruptedException, ContainerDoesNotExistException, DockerInternalServerErrorException;
    
    // checkIfContainerIsReady checks if a container is in running state
    // It returns true if it is running, false if in any other state
    boolean checkIfContainerIsReady(String containerId) throws IOException, InterruptedException, ContainerDoesNotExistException, DockerInternalServerErrorException;

    // generateExecInstance generates a new exec instance on a container given a container ID and a command to run
    // It returns the exec ID
    String generateExecInstance(String containerId, List<String> commands) throws IOException, InterruptedException, ContainerDoesNotExistException, ContainerIsStoppedException, DockerInternalServerErrorException;

    // startExecInstance start an exec instance on a docker daemon given an exec ID. 
    // It returns the stdout and stderr from inside the container
    String startExecInstance(String execInstanceId) throws IOException, InterruptedException, ExecInstanceDoesNotExistException, ContainerIsStoppedException, DockerInternalServerErrorException;

    // stopContainer stops a container given a container ID.
    // Returns true if the container is stopped and false is the container was already stopped
    boolean stopContainer(String containerId) throws IOException, InterruptedException, ContainerDoesNotExistException, DockerInternalServerErrorException;
    
    // removeContainer removes a container given a container ID
    void removeContainer(String containerId) throws IOException, InterruptedException, ContainerDoesNotExistException, ContainerIsRunningException, DockerInternalServerErrorException;
}
