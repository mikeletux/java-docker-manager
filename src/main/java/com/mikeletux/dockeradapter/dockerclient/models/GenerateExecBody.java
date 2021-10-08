package com.mikeletux.dockeradapter.dockerclient.models;

import java.util.List;

public class GenerateExecBody {
    private final boolean AttachStdin;
    private final boolean AttachStdout;
    private final boolean AttachStderr;
    private final boolean Tty;
    private final List<String> Cmd;

    public GenerateExecBody(boolean attachStdin, boolean attachStdout, boolean attachStderr, boolean tty, boolean detach, List<String> cmd) {
        this.AttachStdin = attachStdin;
        this.AttachStdout = attachStdout;
        this.AttachStderr = attachStderr;
        this.Tty = tty;
        this.Cmd = cmd;
    }

    public boolean getAttachStdin() {
        return this.AttachStdin;
    }

    public boolean getAttachStdout() {
        return this.AttachStdout;
    }

    public boolean getAttachStderr() {
        return this.AttachStderr;
    }

    public boolean getTty() {
        return this.Tty;
    }

    public List<String> getCmd() {
        return this.Cmd;
    }
}
