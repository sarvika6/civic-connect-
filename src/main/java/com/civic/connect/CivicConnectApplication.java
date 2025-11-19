package com.civic.connect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application entry point.
 * This class contains the main() method to start the embedded Tomcat server.
 */
@SpringBootApplication
public class CivicConnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CivicConnectApplication.class, args);
	}

}
