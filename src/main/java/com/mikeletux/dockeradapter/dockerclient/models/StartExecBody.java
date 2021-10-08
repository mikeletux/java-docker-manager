package com.mikeletux.dockeradapter.dockerclient.models;

public class StartExecBody {
    private final boolean Detach;
    private final boolean Tty;

    public StartExecBody(boolean detach, boolean tty){
        this.Detach = detach;
        this.Tty = tty;
    }

    public boolean getDetach() {
        return this.Detach;
    }

    public boolean getTty() {
        return this.Tty;
    }

}
