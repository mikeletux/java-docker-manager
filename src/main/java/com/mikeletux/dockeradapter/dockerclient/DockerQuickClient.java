package com.mikeletux.dockeradapter.dockerclient;

import com.mikeletux.dockeradapter.httprestclient.IHttpClientInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mikeletux.dockeradapter.httprestclient.HttpCustomResponse;
import com.mikeletux.dockeradapter.dockerclient.exceptions.*;
import com.mikeletux.dockeradapter.dockerclient.models.*;

import java.io.IOException;
import java.lang.InterruptedException;

import java.util.List;

public class DockerQuickClient implements IDockerClient{

    private String dockerAPIEndpoint;
    private IHttpClientInterface httpCustomClient;

    public DockerQuickClient(String dockerAPIEndpoint, IHttpClientInterface httpCustomClient){
        this.dockerAPIEndpoint = dockerAPIEndpoint;
        this.httpCustomClient = httpCustomClient;
    }

    @Override
    public boolean checkIfImageAlreadyExists(String dockerImage, String tag) throws IOException, InterruptedException, DockerInternalServerErrorException{
        HttpCustomResponse response = httpCustomClient.Get(dockerAPIEndpoint + "/images/" + dockerImage + ":" + tag + "/json");
        switch(response.getStatusCode()){
            case 200:
                return true;
            case 404:
                return false;
            default: // If error 500
                throw new DockerInternalServerErrorException("There was an unknown error at server side.");
        }
    }

    @Override
    public void pullImageFromRegistry(String dockerImage, String tag, String arch) throws IOException, InterruptedException, ImageDoesNotExistException, DockerInternalServerErrorException{
        HttpCustomResponse response = httpCustomClient.Post(dockerAPIEndpoint + "/images/create?" +
                                                                                "fromImage=" + dockerImage + "&" + 
                                                                                "tag=" + tag + "&" +
                                                                                "platform=" + arch, 
                                                                                ""); //No body needed
        switch(response.getStatusCode()){
            case 200:
                return;
            case 404:
                throw new ImageDoesNotExistException("The image you're tring to pull from docker registry does not exist");
            default: // If error 500
                throw new DockerInternalServerErrorException("There was an unknown error at server side.");
        }
    }

    @Override
    public String createContainer(String containerName, String image, String tag, List<String> cmd) throws IOException, InterruptedException, ImageDoesNotExistException, ContainerAlreadyExistsException, DockerInternalServerErrorException{
        Gson gson = new GsonBuilder().create();
        HttpCustomResponse response = httpCustomClient.Post(dockerAPIEndpoint + "/containers/create?" +
                                                                                "name=" + containerName,
                                                                                gson.toJson(new CreateContainerBody(image + ":" + tag, cmd)));
        switch(response.getStatusCode()){
            case 201:
                return gson.fromJson(response.getBody(), DockerId.class).getId();
            case 404:
                throw new ImageDoesNotExistException("The image you're tring to use does not exist");
            case 409:
                throw new ContainerAlreadyExistsException("a container image with the name " + containerName + " already exist. Please delete it or use another name.");
            default: // If error 500
                throw new DockerInternalServerErrorException("There was an unknown error at server side.");
            }
    }
    
    @Override
    public void runContainer(String containerId)
            throws IOException, InterruptedException, ContainerDoesNotExistException, DockerInternalServerErrorException {
        
        HttpCustomResponse response = httpCustomClient.Post(dockerAPIEndpoint + "/containers/" +
                                                                                containerId +
                                                                                "/start",
                                                                                "");
        switch(response.getStatusCode()){
            case 204:
            case 304:
                return;
            case 404:
                throw new ContainerDoesNotExistException("The container you're trying to start does not exist");
            default: // If error 500
                throw new DockerInternalServerErrorException("There was an unknown error at server side.");
        }
    }

    @Override
    public boolean checkIfContainerIsReady(String containerId) throws IOException, InterruptedException, ContainerDoesNotExistException, DockerInternalServerErrorException {
        Gson gson = new GsonBuilder().create();
        HttpCustomResponse response = httpCustomClient.Get(dockerAPIEndpoint + "/containers/" +
                                                                                containerId +
                                                                                "/json");
        switch (response.getStatusCode()) {
            case 200:
            break;
            case 404:
                throw new ContainerDoesNotExistException("The container you're trying to access does not exist");
            default: // If error 500
                throw new DockerInternalServerErrorException("There was an unknown error at server side.");
        }
        
        String status = gson.fromJson(response.getBody().toString(), ContainerInfo.class).getState().getStatus();

        if (status != "running") {
            return true;
        }

        return false;
    }

    @Override
    public String generateExecInstance(String containerId, List<String> commands)
            throws IOException, InterruptedException, ContainerDoesNotExistException, ContainerIsStoppedException, DockerInternalServerErrorException {
                
        Gson gson = new GsonBuilder().create();
        HttpCustomResponse response = httpCustomClient.Post(dockerAPIEndpoint + "/containers/" +
                                                                                containerId +
                                                                                "/exec",
                                                                                gson.toJson(new GenerateExecBody(false, true, true, true, false,commands)));
        switch(response.getStatusCode()){
            case 201:
                return gson.fromJson(response.getBody().toString(), DockerId.class).getId();
            case 404:
                throw new ContainerDoesNotExistException("The container you're trying to create exec instance does not exist");
            case 409:
                throw new ContainerIsStoppedException("The exec instance cannot be create because the container is stopped");
            default: // If error 500
                throw new DockerInternalServerErrorException("There was an unknown error at server side.");
        }
    }

    @Override
    public String startExecInstance(String execInstanceId)
            throws IOException, InterruptedException, ExecInstanceDoesNotExistException, ContainerIsStoppedException, DockerInternalServerErrorException {

        Gson gson = new GsonBuilder().create();
        HttpCustomResponse response = httpCustomClient.Post(dockerAPIEndpoint + "/exec/" +
                                                                                execInstanceId +
                                                                                "/start",
                                                                                gson.toJson(new StartExecBody(false, true)));
        switch(response.getStatusCode()){
            case 200:
                return response.getBody();
            case 404:
                throw new ExecInstanceDoesNotExistException("The exec instance you're trying to access does not exist");
            case 409:
                throw new ContainerIsStoppedException("The exec instance cannot be create because the container is stopped");
            default: // If error 500
                throw new DockerInternalServerErrorException("There was an unknown error at server side."); 
        }       
    }

    @Override
    public boolean stopContainer(String containerId)
            throws IOException, InterruptedException, ContainerDoesNotExistException, DockerInternalServerErrorException {
                HttpCustomResponse response = httpCustomClient.Post(dockerAPIEndpoint + "/containers/" +
                                                                                        containerId +
                                                                                        "/stop", "");
                switch(response.getStatusCode()){
                    case 204:
                        return true;
                    case 304:
                        return false;
                    case 404:
                        throw new ContainerDoesNotExistException("The container you're trying to stop does not exist");
                    default: // If error 500
                        throw new DockerInternalServerErrorException("There was an unknown error at server side."); 
                }   
    }

    @Override
    public void removeContainer(String containerId)
            throws IOException, InterruptedException, ContainerDoesNotExistException, ContainerIsRunningException, DockerInternalServerErrorException {
                HttpCustomResponse response = httpCustomClient.Delete(dockerAPIEndpoint + "/containers/" +
                                                                                            containerId);
                switch(response.getStatusCode()){
                    case 204:
                        return;
                    case 404:
                        throw new ContainerDoesNotExistException("The container you're trying to delete does not exist");
                    case 409:
                        throw new ContainerIsRunningException("A running container cannot be removed. Please stop it first");
                    default: // If error 500
                        throw new DockerInternalServerErrorException("There was an unknown error at server side."); 
                }
    } 
    
}