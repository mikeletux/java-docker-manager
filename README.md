# Java-docker-manager
This project consist of a spring app that connects to a Docker backend, spans a ubuntu container and shows live CPU/Memory information from inside the container.
The app also shows a delete button that can be use to terminate the container as per user request.

## Prerequisites
In order to run this project, the following prerequisites need to be met:
  - Operating system: Ubuntu 20.04
  - Docker version 2.10.8, build 3967b7d  

*(Other OSs and docker versions might work, but no testing has been done)*
  

To build this project manually, the following dependencies are needed:
  - OpenJDK 17
  - Maven 3.8.3
To run this project as a docker container, please reffer to *Running Java-docker-manager as a container* section.

## Changes needed in docker to access the Rest API  
By default, Docker host does not expose the HTTP Rest API for consumption. To do so, follow the following steps:
  - Open up the file at */lib/systemd/system/docker.service*
  - Find the line that starts with *ExecStart* and add the following string (feel free to use a different TCP port):
  ```
  -H=tcp://0.0.0.0:2375
  ```
  - Reload the docker daemon:
  ```
  systemctl daemon-reload
  ```
  - Restart the docker daemon:
  ```
  systemctl restart docker
  ```
After this, you will be able to use this adapter against your docker daemon.

## Adapter configuration
Before running the adapter, some configuration needs to be put in place. This config is injected into the project as environment variables.  
There are three flags:
  - **SERVER_PORT**: This is the TCP port where the Spring app will listen. If the app runs on port 9000 and it is deployed on the same machine, to access you would need to open up a browser and go to *https://localhost:9000*
  - **DOCKER_ENDPOINT**: This is the docker daemon API where the adapter will connect to. This is where the ubuntu container will be deployed.
  - **SERVER_DOMAIN**: This is the domain where the frontend will connect to get the memory/cpu information. Since everything is embedded into the same Spring app, this is recommended to be set to **localhost**.

An example of executing the Spring app directly from Maven could be:
```
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver_port=9000 -Ddocker_endpoint=http://192.168.1.150:2375 -Dserver_domain=localhost"
```

## Accessing and using the application
When the Spring app is running use a web browser and goes to the address where the app is deployed (don't forger the server port used). 
If the user has deploy the app in the same server as the docker backend, it would be *http://localhost:9000* (If 9000 was the TCP port chosen when executing the application).

The main interface will show statistics about CPU and memory usage. Below them a button labeled *Delete container* is shown. When pressed, the application will terminate the container (give it some seconds, the app will tell you when it's gone).

## Running Java-docker-manager as a container
The project comes along a Dockerfile, so users can run this project without thinking about dependencies.  

### Building the container
To build a container, please use the *docker build* command. Using a tag is recommeded:
```
docker build -t java-docker-manager .
```
### Running the container
To run the container, some configuration needs to be injected (for documentation about configuration, please reffer to the section *Adapter configuration* above). 
This is done through environmet variables. Use the following code snippet as reference when running the container:  

```
docker run -d -p 9000:9000 \
-e SERVER_PORT=9000 \
-e DOCKER_ENDPOINT=http://192.168.1.150:2375 \
-e SERVER_DOMAIN=localhost \
--name my-java-docker-manager \
java-docker-manager
```

## Aplication flows
To give the user some insight about how the application works, here are the workflows that the Spring app follows:
  - During Spring app start up:
    - The app will try to connect to the docker backend and will try to find out if the ubuntu docker image is present.
    - If the image is not present, it will download it from *hub.docker.com*. If it exists, it will use the local image.
    - The app will try to sping a new container using the above image. If there is a running container it will error and abort the app start up process (this can be seen on logs). If there is such container, a new container will be created and started.
  
  - Lifecycle of the app:
    - At this point users can start querying the app using a browser and it will display a simple HTML website with live CPU/memory statistics and a button labeled "Delete container".
    - This will continue indefinitely until the user press the *Delete container* button.
    - When the button is pressed, statistics will stall and after a while, an alert message will show up confirming that the container has been removed.

/Miguel Sama 2021