package dev.ioana.lessons_of_wellbeing.auth;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.ioana.lessons_of_wellbeing.security.SecurityUser;
import dev.ioana.lessons_of_wellbeing.user.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public SecurityUser loadUserByUsername(String email) {
        return userRepository.findByEmailIgnoreCase(email.trim())
            .map(SecurityUser::new)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password."));
    }
}
