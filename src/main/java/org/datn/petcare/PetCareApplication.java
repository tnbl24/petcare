package org.datn.petcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
//@EnableJpaRepositories(basePackages = "org.datn.petcare.repository")
//@EnableElasticsearchRepositories(basePackages = "org.datn.petcare.repository.ElasticSearch")
public class PetCareApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetCareApplication.class, args);
	}

}
