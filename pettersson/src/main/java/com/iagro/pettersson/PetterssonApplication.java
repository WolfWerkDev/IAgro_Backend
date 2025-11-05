package com.iagro.pettersson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PetterssonApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetterssonApplication.class, args);
	}

}
