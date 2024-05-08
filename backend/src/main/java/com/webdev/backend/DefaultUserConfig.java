package com.webdev.backend;
import com.webdev.backend.model.User;
import com.webdev.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultUserConfig {

    @Autowired
    UserService userService;

    @Bean
    public CommandLineRunner createDefaultAdmin() {
        return args -> {
            User admin = userService.getByUsername("admin");
            if (admin == null) {
                admin = new User();
                admin.setUsername("admin");
                admin.setPassword("pwd");
                admin.setRole("admin");
                userService.registerUser(admin);
            }
        };
    }
}