package com.mikeletux.dockeradapter.dockerclient.models;

public class DockerId {
    private final String Id;

    public DockerId(String id){
        this.Id = id;
    }

    public String getId() {
        return this.Id;
    }


}
