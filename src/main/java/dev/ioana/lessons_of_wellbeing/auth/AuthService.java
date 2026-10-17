package dev.ioana.lessons_of_wellbeing.auth;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import dev.ioana.lessons_of_wellbeing.auth.dtos.RegisterDTORequest;
import dev.ioana.lessons_of_wellbeing.user.UserEntity;

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
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JWTService jwtService,
            UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTOResponse register(RegisterDTORequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        int passwordBytes = request.password().getBytes(StandardCharsets.UTF_8).length;
        String phoneNumber = request.phoneNumber().trim();
        int phoneDigits = phoneNumber.replaceAll("\\D", "").length();
        if (passwordBytes > 72 || phoneDigits < 7 || phoneDigits > 15) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        UserEntity user = new UserEntity();
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setAddress(request.address().trim());
        user.setPassword(passwordEncoder.encode(request.password()));
        try {
            return UserDTOResponse.from(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException exception) {
            // The unique database constraint also protects simultaneous registrations.
            if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
            throw exception;
        }
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
