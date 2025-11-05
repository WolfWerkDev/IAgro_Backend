package com.iagro.pettersson.security;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("🛡️ Iniciando configuración de seguridad HTTP...");

        http
                // 🔓 CORS y CSRF deshabilitados (para APIs REST y Swagger)
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new CorsConfiguration();
                    corsConfig.setAllowedOriginPatterns(List.of("*"));
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setAllowCredentials(true);

                    // Se envuelve en un source
                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", corsConfig);
                    return source.getCorsConfiguration(request);
                }))

                // 🔐 Política de sesión sin estado (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 🧩 Configuración de rutas públicas y protegidas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/ws/**",
                                "/usuario/registrar",
                                "/admin/registro-superUser",
                                "/images/**",
                                "/uploads/**"
                        ).permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/error",
                                "/pettersson/v3/api-docs/**",
                                "/pettersson/swagger-ui/**",
                                "/pettersson/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasAuthority("SUPER_USER")
                        .anyRequest().authenticated()
                )

                // 👤 Permite usuarios anónimos
                .anonymous(Customizer.withDefaults())

                // 🚫 Manejo global de errores de autenticación
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            log.warn("🚫 Acceso no autorizado a: {}", req.getRequestURI());
                            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Acceso denegado");
                        })
                )

                // ⚙️ Desactiva frameOptions (para consola H2 y Swagger UI)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // 🧱 Filtro JWT antes del filtro de autenticación estándar
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("✅ Configuración de seguridad finalizada correctamente.");
        return http.build();
    }
}
