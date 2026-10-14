package dev.ioana.lessons_of_wellbeing.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.web.servlet.MockMvc;

import dev.ioana.lessons_of_wellbeing.user.UserEntity;
import dev.ioana.lessons_of_wellbeing.user.UserRepository;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(properties = {
    "jwt.secret=YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY=",
    "demo.user.password=",
    "spring.datasource.url=jdbc:h2:mem:auth-tests"
})
@AutoConfigureMockMvc
class AuthIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private UserRepository users;
    @Autowired private PasswordEncoder encoder;
    @Autowired private ObjectMapper mapper;
    @Autowired private JwtEncoder jwtEncoder;
    @Autowired private JwtDecoder jwtDecoder;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        users.deleteAll();
        user = new UserEntity();
        user.setFirstName("Elena");
        user.setLastName("Pop");
        user.setEmail("elena@example.com");
        user.setPassword(encoder.encode("TestPassword123!"));
        user = users.save(user);
    }

    @Test
    void loginAndMeReturnPublicUserDataWithValidSignedToken() throws Exception {
        String body = mvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"elena@example.com","password":"TestPassword123!"}
                    """))
            .andExpect(status().isOk())
            .andExpect(header().string("Cache-Control", "no-store"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").value(900))
            .andExpect(jsonPath("$.user.firstName").value("Elena"))
            .andExpect(jsonPath("$.user.role").value("USER"))
            .andExpect(jsonPath("$.user.password").doesNotExist())
            .andReturn().getResponse().getContentAsString();

        String token = mapper.readTree(body).get("accessToken").asString();
        Jwt jwt = jwtDecoder.decode(token);
        assertThat(jwt.getSubject()).isEqualTo(user.getId().toString());
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("USER");

        mvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(header().string("Cache-Control", "no-store"))
            .andExpect(jsonPath("$.email").value("elena@example.com"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void wrongPasswordAndUnknownEmailReturnSameError() throws Exception {
        for (String email : new String[] {"elena@example.com", "missing@example.com"}) {
            mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"" + email + "\",\"password\":\"incorrect\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password."));
        }
    }

    @Test
    void invalidAndMalformedInputReturnBadRequest() throws Exception {
        for (String body : new String[] {
                "{\"email\":\"invalid\",\"password\":\"\"}", "{", "{}"}) {
            mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Test
    void protectedEndpointRejectsMissingMalformedExpiredAndWrongIssuerTokens() throws Exception {
        mvc.perform(get("/api/v1/auth/me")).andExpect(status().isUnauthorized());
        for (String token : new String[] {
                "invalid.token.value",
                token("lessons-of-wellbeing", Instant.now().minusSeconds(10)),
                token("another-issuer", Instant.now().plusSeconds(300))}) {
            mvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
        }
    }

    @Test
    void tamperedSignatureIsRejected() throws Exception {
        String token = token("lessons-of-wellbeing", Instant.now().plusSeconds(300));
        int signatureStart = token.lastIndexOf('.') + 1;
        char replacement = token.charAt(signatureStart) == 'a' ? 'b' : 'a';
        String tampered = token.substring(0, signatureStart) + replacement + token.substring(signatureStart + 1);
        mvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + tampered))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deletedUserCannotLoadProfile() throws Exception {
        String token = token("lessons-of-wellbeing", Instant.now().plusSeconds(300));
        users.delete(user);
        mvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Please sign in again."));
    }

    @Test
    void loginIgnoresEmailCase() throws Exception {
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"Elena@Example.com\",\"password\":\"TestPassword123!\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user.email").value("elena@example.com"));
    }

    @Test
    void publicCalendarAndBrowserPreflightRemainAccessible() throws Exception {
        mvc.perform(get("/api/v1/sessions")).andExpect(status().isOk());
        mvc.perform(options("/api/v1/auth/login")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "content-type"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    private String token(String issuer, Instant expiresAt) {
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(issuer)
            .subject(user.getId().toString()).issuedAt(Instant.now().minusSeconds(60))
            .expiresAt(expiresAt).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(
            JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }
}
