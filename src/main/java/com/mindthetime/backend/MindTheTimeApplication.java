package com.mindthetime.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MindTheTimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MindTheTimeApplication.class, args);
	}

}
