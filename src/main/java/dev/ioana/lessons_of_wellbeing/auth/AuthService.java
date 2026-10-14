package dev.ioana.lessons_of_wellbeing.auth;

import java.nio.charset.StandardCharsets;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.ioana.lessons_of_wellbeing.auth.dtos.LoginDTORequest;
import dev.ioana.lessons_of_wellbeing.auth.dtos.LoginDTOResponse;
import dev.ioana.lessons_of_wellbeing.user.dtos.UserDTOResponse;
import dev.ioana.lessons_of_wellbeing.security.JWTService;
import dev.ioana.lessons_of_wellbeing.security.SecurityUser;
import dev.ioana.lessons_of_wellbeing.user.UserRepository;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager, JWTService jwtService,
            UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public LoginDTOResponse login(LoginDTORequest request) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadCredentialsException("Invalid email or password.");
        }
        var authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(
                request.email().trim(), request.password()));
        SecurityUser details = (SecurityUser) authentication.getPrincipal();
        String token = jwtService.generateToken(details.getUser());
        return new LoginDTOResponse(token, "Bearer", jwtService.getExpiresIn(),
            UserDTOResponse.from(details.getUser()));
    }

    public UserDTOResponse getCurrentUser(String subject) {
        try {
            Long id = Long.valueOf(subject);
            return userRepository.findById(id)
                .map(UserDTOResponse::from)
                .orElseThrow(() -> new UsernameNotFoundException("User no longer exists."));
        } catch (NumberFormatException exception) {
            throw new UsernameNotFoundException("User no longer exists.");
        }
    }
}
