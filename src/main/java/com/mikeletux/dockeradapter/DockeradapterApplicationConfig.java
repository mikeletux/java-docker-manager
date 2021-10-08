package com.mikeletux.dockeradapter;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.mikeletux.dockeradapter.dockerclient.DockerQuickClient;
import com.mikeletux.dockeradapter.dockerclient.IDockerClient;

import com.mikeletux.dockeradapter.httprestclient.HttpQuickClient;
import com.mikeletux.dockeradapter.httprestclient.IHttpClientInterface;
import com.mikeletux.dockeradapter.dockerclient.exceptions.*;

import java.util.Arrays;
import java.io.IOException;

import org.slf4j.Logger;

@Configuration
public class DockeradapterApplicationConfig {
    Logger logger = LoggerFactory.getLogger(DockeradapterApplicationConfig.class);
    
    @Value("${docker.apiEndpoint}")
    private String dockerAPIEndpoint;

    @Value("${docker.image}")
    private String dockerImage;

    @Value("${docker.imageTag}")
    private String dockerImageTag;

    @Value("${docker.imageArch}")
    private String dockerImageArch;

    @Value("${docker.containerName}")
    private String dockerContainerName;

    private IDockerClient dockerClient;

    @Bean
    public IDockerClient dockerClient() {
        IHttpClientInterface httpCustomClient = new HttpQuickClient();
        dockerClient = new DockerQuickClient(dockerAPIEndpoint, httpCustomClient);
        return dockerClient;
    }

    @Bean
    @DependsOn("dockerClient")
    public String containerId() {
        String id = "";
        try {
            if(!dockerClient.checkIfImageAlreadyExists(dockerImage, dockerImageTag)){
                logger.info("docker image " + dockerImage + ":" + dockerImageTag + " doesn't exist. Downloading...");
                dockerClient.pullImageFromRegistry(dockerImage, dockerImageTag, dockerImageArch);
                logger.info("docker image "+ dockerImage + ":" + dockerImageTag + " successfully downloaded");
            }
            id = dockerClient.createContainer(dockerContainerName, dockerImage, dockerImageTag, Arrays.asList(new String[]{"sleep","infinity"}));
            logger.info("docker container with name " + dockerContainerName + " successfully created");
            dockerClient.runContainer(id);
			do {
                logger.info("waiting for container " + dockerContainerName + " to be ready...");
				if (dockerClient.checkIfContainerIsReady(id)) break;
				Thread.sleep(1000);
			}while(true);
        

        }catch(IOException | InterruptedException e) {
            logger.error("there was some issue when contacting the remote docker daemon. " + 
            "Please contact your system administrator. Aborting...");
            System.exit(-1);
        }catch(ImageDoesNotExistException e){
            logger.error("the image tried to pull doesn't exist. Aborting...");
            System.exit(-1);
        }catch(ContainerDoesNotExistException e){
            logger.error("the container tried to access doesn't exist. Aborting...");
            System.exit(-1);
        }catch(ContainerAlreadyExistsException e){
            logger.error("there is already a container with name "+ dockerContainerName + 
                        ". Please stop it and/or remove it first before starting the application.");
            System.exit(-1);
        }catch(DockerInternalServerErrorException e){
            logger.error("there was some unknown issue at the docker API side. Aborting...");
            System.exit(-1);
        }
        logger.info("application successfully started :)");
        return id;
    }
}
