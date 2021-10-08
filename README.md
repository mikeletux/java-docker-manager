# Java-docker-manager
This project consist of a Spring app that connects to a Docker backend, spans a Ubuntu container and shows live CPU/Memory information from inside the container.
The app also shows a delete button that can be use to terminate the container as per user request.

## Prerequisites
In order to run this project, the following prerequisites need to be met:
  - Operating system: Ubuntu 20.04
  - Docker version 2.10.8, build 3967b7d (This version implements Docker Engine API *v.1.41*)
  
*(These are the requirements for the Docker backend instance. Other OSs and Docker versions might work, but no testing has been done)*
  

To build this project manually, the following dependencies are needed:
  - OpenJDK 17
  - Maven 3.8.3  
  
To run this project as a Docker container, please reffer to *Running Java-docker-manager as a container* section.

## Changes needed in Docker backend to access the Rest API  
By default, Docker host does not expose the HTTP Rest API for consumption, it only does it locally through UNIX sockets.
To enable the HTTP Rest API, follow the following steps:
  - Open up the file at */lib/systemd/system/docker.service*
  - Find the line that starts with *ExecStart* and add the following string (feel free to use a different TCP port):
  ```
  -H=tcp://0.0.0.0:2375
  ```
  - Reload the Docker daemon:
  ```
  systemctl daemon-reload
  ```
  - Restart the Docker daemon:
  ```
  systemctl restart docker
  ```
After this, you will be able to use this adapter against your Docker backend.
  
This project leverages on HTTP rather than UNIX sockets for accessing the rest API. This is because through HTTP users can control both local or remote Docker
backends. In the future, UNIX sockets feature could be implemented by implementing the *IHttpClientInterface*.

## Adapter configuration
Before running the adapter, some configuration needs to be put in place. This config is injected into the project as environment variables.  
There are three flags:
  - **SERVER_PORT**: This is the TCP port where the Spring app will listen. If the app runs on port 9000 and it is deployed on the same machine, to access you would need to open up a browser and go to *https://localhost:9000*
  - **DOCKER_ENDPOINT**: This is the Docker backend API where the adapter will connect to. This is where the Ubuntu container will be deployed.
  - **SERVER_DOMAIN**: This is the domain where the frontend will connect to get the memory/cpu information. Since everything is embedded into the same Spring app, this is recommended to be set to **localhost**.

An example of executing the Spring app directly from Maven could be:
```
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver_port=9000 -Ddocker_endpoint=http://192.168.1.150:2375 -Dserver_domain=localhost"
```

## Accessing and using the application
When the Spring app is running use a web browser and go to the address where the app is deployed (don't forget the server port used). 
If the user has deployed the app in the same server as the Docker backend, it would be *http://localhost:9000* (If 9000 was the TCP port chosen when executing the application).

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
    - The app will try to connect to the Docker backend and will try to find out if the Ubuntu Docker image is present.
    - If the image is not present, it will download it from *hub.docker.com*. If it exists, it will use the local image.
    - The app will try to spin a new container using the above image. If there is a running container it will error and abort the app start up process (this can be seen on logs). If there is no such container, a new container will be created and started.
  
  - Lifecycle of the app:
    - At this point users can start querying the app using a browser and it will display a simple HTML website with live CPU/memory statistics and a button labeled "Delete container".
    - This will continue indefinitely until the user press the *Delete container* button.
    - When the button is pressed, statistics will stall and after a while, an alert message will show up confirming that the container has been removed.

## Acceptance testing
The acceptance tests are run in an actual instance of the application.  
To run them, some variables need to be set. Those are *server_port*, *docker_endpoint* and *server_domain*, although the last one can be left as *localhost* for most scenarios. Please refer to *Adapter configuration* for more info about config variables.  

There are three tests in total:
  - **First one**: Check that there's a container ID, meaning that the app spins correctly the Ubuntu container.
  - **Second one**: Does an HTTP get against */docker* endpoint and check if response was 200 OK, and gets information about CPU and memory.
  - **Third one**: Tries to delete the container twice. Since in the first call the container exists, it should return 200 OK. The second time the container doesn't exist, so it should return 404 NOT FOUND.

/Miguel Sama 2021