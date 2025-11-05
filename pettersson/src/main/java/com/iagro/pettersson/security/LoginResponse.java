package com.iagro.pettersson.security;

public record LoginResponse(
        String token,
        boolean isSuperUser
) {
}
