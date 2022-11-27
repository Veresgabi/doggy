package com.example.doggy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan({"service", "controller", "configuration", "util"})
@EnableJpaRepositories("repository")
@EntityScan(basePackages="model")
public class DoggyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoggyApplication.class, args);
	}

}
