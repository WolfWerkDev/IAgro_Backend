package com.iagro.pettersson.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        log.info("➡️ [{}] Petición entrante a: {}", method, path);

        // Detectar rutas públicas
        if (path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || path.equals("/swagger-ui.html")
                || path.equals("/login")
                || path.equals("/register")) {
            log.info("🟢 Ruta pública o Swagger detectada, se omite validación JWT: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        // Validar header Authorization
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            log.warn("⚠️ Petición sin header Authorization o formato incorrecto. Ruta: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.replace("Bearer ", "").trim();
        log.debug("🧾 Token recibido ({} caracteres)", token.length());

        try {
            DecodedJWT decodedJWT = jwtUtil.validarToken(token);
            log.info("✅ Token válido para usuario: {}", decodedJWT.getSubject());

            boolean isSuperUser = decodedJWT.getClaim("isSuperUser").asBoolean();
            if (isSuperUser) {
                log.info("👑 Usuario con rol SUPER_USER autenticado.");
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                decodedJWT.getSubject(), null,
                                List.of(new SimpleGrantedAuthority("SUPER_USER"))
                        )
                );
            } else {
                log.info("👤 Usuario estándar autenticado.");
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                decodedJWT.getSubject(), null, List.of()
                        )
                );
            }

        } catch (Exception e) {
            log.error("❌ Error validando token: {} — Causa: {}", e.getClass().getSimpleName(), e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido o expirado");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
