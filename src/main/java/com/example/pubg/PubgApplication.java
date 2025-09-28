package com.example.pubg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PubgApplication {

	public static void main(String[] args) {
		SpringApplication.run(PubgApplication.class, args);
	}

}
