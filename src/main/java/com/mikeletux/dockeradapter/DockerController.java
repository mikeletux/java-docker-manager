package com.mikeletux.dockeradapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import com.mikeletux.dockeradapter.dockerclient.IDockerClient;
import com.mikeletux.dockeradapter.dockerclient.exceptions.*;

import java.util.Arrays;
import java.io.IOException;

@Controller
public class DockerController {
    
    @Autowired
    IDockerClient dockerClient;

    @Autowired
    String containerId;

    @Value("${restapi.domain}")
    private String restApiDomain;

    @Value("${server.port}")
    private String serverPort;

    @Value("${restapi.dockerCommandEndpoint}")
    private String dockerCommandEndpoint;

    @Value("${restapi.dockerRemoveEndpoint}")
    private String dockerRemoveEndpoint;

    @RequestMapping("/docker")
    public String dockerOutput(Model model){
        String result;
        try {
            String execId = dockerClient.generateExecInstance(containerId, Arrays.asList(new String[]{"/bin/sh", "-c", "top -b -n 1 | head -4 | tail -2"}));
            result = dockerClient.startExecInstance(execId);
        }catch(IOException | InterruptedException e) {
            result = "Communication issue with the docker API";
        }catch(ContainerDoesNotExistException e){
            result = "The container does not exist";
        }catch(ExecInstanceDoesNotExistException e){
            result = "The exec instance does not exist";
        }catch(ContainerIsStoppedException e){
            result = "The container where the command is retrieved is stopped. Please start it first.";
        }catch(DockerInternalServerErrorException e){
            result = "there was some unknown issue at the docker API side";
            System.exit(-1);
        }
        model.addAttribute("dockerOutput", result);
        return "docker";
    }

    @RequestMapping("/")
    public String homePage(Model model){
        model.addAttribute("restApiDomain", restApiDomain);
        model.addAttribute("serverPort", serverPort);
        model.addAttribute("dockerCommandEndpoint", dockerCommandEndpoint);
        model.addAttribute("dockerRemoveEndpoint", dockerRemoveEndpoint);
        return "index";
    }

}
