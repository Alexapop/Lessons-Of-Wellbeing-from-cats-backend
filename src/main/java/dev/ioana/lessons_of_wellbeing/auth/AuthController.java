package dev.ioana.lessons_of_wellbeing.auth;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import dev.ioana.lessons_of_wellbeing.auth.dtos.RegisterDTORequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ioana.lessons_of_wellbeing.auth.dtos.LoginDTORequest;
import dev.ioana.lessons_of_wellbeing.auth.dtos.LoginDTOResponse;
import dev.ioana.lessons_of_wellbeing.user.dtos.UserDTOResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "${api-endpoint}/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTOResponse> register(@Valid @RequestBody RegisterDTORequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).cacheControl(CacheControl.noStore())
            .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDTOResponse> login(@Valid @RequestBody LoginDTORequest request) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTOResponse> me(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
            .body(authService.getCurrentUser(jwt.getSubject()));
    }
}
