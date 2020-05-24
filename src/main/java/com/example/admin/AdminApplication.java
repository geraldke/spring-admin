package com.example.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.example.admin.storage.StorageProperties;
import com.example.admin.storage.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class AdminApplication {

	private static final Logger log = LoggerFactory.getLogger(AdminApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AdminApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}

	@Bean
	CommandLineRunner loadData(UserRepository repository) {
		return (args) -> {
			// save a couple of users
			repository.save(new User("Bruce", "Wayne"));
			repository.save(new User("Diana", "Prince"));

			// find all users
			log.info("Find all users:");
			log.info("-------------------------------");
			for (User user : repository.findAll()) {
				log.info(user.toString());
			}

			// find a user by ID
			User user = repository.findById(1L).get();
			log.info("User found with findOne(1L):");
			log.info("--------------------------------");
			log.info(user.toString());

			// find a user by last name
			log.info("User found with findByLastNameStartsWithIgnoreCase('Wayne'):");
			log.info("--------------------------------------------");
			for (User wayne : repository
					.findByLastNameStartsWithIgnoreCase("Wayne")) {
				log.info(wayne.toString());
			}
		};
	}
}
