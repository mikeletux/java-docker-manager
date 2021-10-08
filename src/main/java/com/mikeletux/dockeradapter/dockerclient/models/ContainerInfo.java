package com.mikeletux.dockeradapter.dockerclient.models;

public class ContainerInfo {
    public class ContainerStatus {
        private String Status;
        private String Running;
        private String Paused;
        private String Restarting;
        
        public ContainerStatus(String status, String running, String paused, String restarting){
            this.Status = status;
            this.Running = running;
            this.Paused = paused;
            this.Restarting = restarting;
        }

        public String getStatus(){
            return Status;
        }

        public String getRunning(){
            return Running;
        }

        public String getPaused(){
            return Paused;
        }

        public String getRestarting(){
            return Restarting;
        }
    }

    private ContainerStatus State;

    public ContainerInfo(ContainerStatus state){
        this.State = state;
    }

    public ContainerStatus getState() {
        return State;
    }
}


