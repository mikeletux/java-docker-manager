package com.mikeletux.dockeradapter;

import com.mikeletux.dockeradapter.httprestclient.HttpCustomResponse;
import com.mikeletux.dockeradapter.httprestclient.HttpQuickClient;
import com.mikeletux.dockeradapter.httprestclient.IHttpClientInterface;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"server_port = 9000", 
									"docker_endpoint = http://192.168.1.150:2375", 
									"server_domain = localhost"})
@TestMethodOrder(OrderAnnotation.class)
class DockeradapterApplicationTests {	
	
    @Autowired
    String containerId;

	@Value("${restapi.domain}")
    private String restApiDomain;

    @Value("${server.port}")
    private String serverPort;
	
	@Test
	@Order(1)
	void contextLoads() {
		assertThat(containerId).hasSizeGreaterThan(0); // Check that id is greater that 0
	}

	@Test
	@Order(2)
	void checkContainerReturnsCPUandMemoryData() throws IOException, InterruptedException {
		IHttpClientInterface httpClient = new HttpQuickClient();
		HttpCustomResponse response = httpClient.Get("http://" + restApiDomain + ":" + serverPort + "/docker");
		assertThat(response.getStatusCode()).isEqualTo(200); // If ok, the endpoint returns 200 OK.
		assertThat(response.getBody()).contains("%Cpu(s)"); // First line of output data is about CPU
		assertThat(response.getBody()).contains("MiB Mem"); // Second line of outout data is about Mem
	}

	@Test
	@Order(3)
	void destroyContainerAndCheckIsGone() throws IOException, InterruptedException {
		IHttpClientInterface httpClient = new HttpQuickClient();
		HttpCustomResponse response = httpClient.Post("http://" + restApiDomain + ":" + serverPort + "/docker/remove", "");
		assertThat(response.getStatusCode()).isEqualTo(200); // If removed it will return a 200 OK

		response = httpClient.Post("http://" + restApiDomain + ":" + serverPort + "/docker/remove", "");
		assertThat(response.getStatusCode()).isEqualTo(404); // Since it was removed previously, it should return a 404
	}
}
