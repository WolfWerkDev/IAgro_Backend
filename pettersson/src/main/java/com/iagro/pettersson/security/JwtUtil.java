package com.iagro.pettersson.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public String generarToken(Long idUsuario, boolean isSuperUser) {
        return JWT.create()
                .withSubject(String.valueOf(idUsuario))
                .withIssuer(issuer)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .withClaim("isSuperUser", isSuperUser)
                .sign(Algorithm.HMAC256(secret));
    }

    public DecodedJWT validarToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(issuer)
                .build();
        return verifier.verify(token);
    }

    public Long obtenerIdUsuario(String token) {
        DecodedJWT decoded = validarToken(token);
        return Long.parseLong(decoded.getSubject());
    }
}
