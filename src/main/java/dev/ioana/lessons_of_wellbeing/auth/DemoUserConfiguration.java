package dev.ioana.lessons_of_wellbeing.auth;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.ioana.lessons_of_wellbeing.user.UserEntity;
import dev.ioana.lessons_of_wellbeing.user.UserRepository;

@Configuration
@Profile("h2")
public class DemoUserConfiguration {
    @Bean
    public CommandLineRunner createDemoUser(UserRepository repository, PasswordEncoder encoder,
            @Value("${demo.user.email:demo@example.com}") String email,
            @Value("${demo.user.password:}") String password) {
        return args -> {
            if (password.isBlank() || repository.findByEmailIgnoreCase(email.trim()).isPresent()) {
                return;
            }
            if (password.length() < 12 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
                throw new IllegalArgumentException("The demo password must have at least 12 characters and at most 72 UTF-8 bytes.");
            }
            UserEntity user = new UserEntity();
            user.setFirstName("Demo");
            user.setLastName("User");
            user.setEmail(email.trim().toLowerCase(Locale.ROOT));
            user.setPassword(encoder.encode(password));
            repository.save(user);
        };
    }
}
