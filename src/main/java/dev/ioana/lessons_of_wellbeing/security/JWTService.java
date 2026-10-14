package dev.ioana.lessons_of_wellbeing.security;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import dev.ioana.lessons_of_wellbeing.user.UserEntity;

@Service
public class JWTService {
    private final JwtEncoder encoder;
    private final String issuer;
    private final long expiresIn;

    public JWTService(JwtEncoder encoder, @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.expires-in-seconds}") long expiresIn) {
        if (expiresIn <= 0 || expiresIn > 86400) {
            throw new IllegalArgumentException("JWT lifetime must be between 1 and 86400 seconds.");
        }
        this.encoder = encoder;
        this.issuer = issuer;
        this.expiresIn = expiresIn;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String generateToken(UserEntity user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .subject(user.getId().toString())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expiresIn))
            .claim("roles", List.of(user.getRole().name()))
            .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
