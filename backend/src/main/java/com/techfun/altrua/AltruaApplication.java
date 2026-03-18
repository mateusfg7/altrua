package com.techfun.altrua;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AltruaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AltruaApplication.class, args);
	}

}
