package com.mikeletux.dockeradapter.dockerclient.models;
import java.util.List;

public class CreateContainerBody {
    private final String Image;
    private final List<String> Cmd;

    public CreateContainerBody(String image, List<String> cmd){
        this.Image = image;
        this.Cmd = cmd;
    }

    public String getImage() {
        return this.Image;
    }

    public List<String> getCmd() {
        return this.Cmd;
    }
}

    
