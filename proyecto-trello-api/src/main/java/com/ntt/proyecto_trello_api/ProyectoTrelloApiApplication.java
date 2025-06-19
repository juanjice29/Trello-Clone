package com.ntt.proyecto_trello_api;

import com.ntt.proyecto_trello_api.model.User;
import com.ntt.proyecto_trello_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class ProyectoTrelloApiApplication implements CommandLineRunner {
	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(ProyectoTrelloApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Create default admin user if it doesn't exist
		if (!userService.existsByUsername("admin")) {
			User admin = new User();
			admin.setUsername("admin");
			admin.setEmail("admin@taskmanager.com");
			admin.setFirstName("Admin");
			admin.setLastName("User");
			admin.setPassword("admin123");
			admin.setRole(User.Role.ADMIN);
			admin.setEnabled(true);

			userService.save(admin);
			System.out.println("Default admin user created: admin/admin123");
		}

		// Create demo user if it doesn't exist
		if (!userService.existsByUsername("demo")) {
			User demo = new User();
			demo.setUsername("demo");
			demo.setEmail("demo@taskmanager.com");
			demo.setFirstName("Demo");
			demo.setLastName("User");
			demo.setPassword("demo123");
			demo.setRole(User.Role.USER);
			demo.setEnabled(true);

			userService.save(demo);
			System.out.println("Demo user created: demo/demo123");
		}
	}
}
