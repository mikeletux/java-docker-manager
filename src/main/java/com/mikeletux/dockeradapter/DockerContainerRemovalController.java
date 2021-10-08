package com.mikeletux.dockeradapter;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import com.mikeletux.dockeradapter.dockerclient.IDockerClient;
import com.mikeletux.dockeradapter.dockerclient.exceptions.*;


@RestController
public class DockerContainerRemovalController {
    @Autowired
    IDockerClient dockerClient;

    @Autowired
    String containerId;

    @RequestMapping(path = "/docker/remove", method = RequestMethod.POST)
    public void stopAndRemoveContainer() {
        try {
            dockerClient.stopContainer(containerId);
            dockerClient.removeContainer(containerId);
        } catch (IOException | InterruptedException | DockerInternalServerErrorException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ContainerDoesNotExistException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch(ContainerIsRunningException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } 
    }
}
