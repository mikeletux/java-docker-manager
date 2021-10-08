FROM ubuntu:20.04

WORKDIR /workspaces/java-docker-manager

COPY . .

EXPOSE 9000

ENV JAVA_HOME=/opt/jdk-17
ENV PATH=$PATH:$JAVA_HOME/bin:/opt/apache-maven-3.8.3/bin

RUN apt update && \
        apt install -y wget && \
        wget https://download.java.net/java/GA/jdk17/0d483333a00540d886896bac774ff48b/35/GPL/openjdk-17_linux-x64_bin.tar.gz && \
        tar -xzvf openjdk-17_linux-x64_bin.tar.gz && \
        mv jdk-17 /opt/ && \
        wget https://dlcdn.apache.org/maven/maven-3/3.8.3/binaries/apache-maven-3.8.3-bin.tar.gz && \
        tar -xzvf apache-maven-3.8.3-bin.tar.gz && \
        mv apache-maven-3.8.3/ /opt

CMD mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver_port=$SERVER_PORT -Ddocker_endpoint=$DOCKER_ENDPOINT -Dserver_domain=$SERVER_DOMAIN"
