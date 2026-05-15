package com.example.InnerCityBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InnerCityBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(InnerCityBackendApplication.class, args);
	}

}
