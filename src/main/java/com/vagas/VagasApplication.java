package com.vagas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VagasApplication {

	public static void main(String[] args) {
		//TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(VagasApplication.class, args);
	}

}
