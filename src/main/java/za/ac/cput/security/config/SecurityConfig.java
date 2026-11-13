package za.ac.cput.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import za.ac.cput.security.auth.CustomUserDetailsService;
import za.ac.cput.security.token.JwtAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/enquiries").permitAll()
                        .requestMatchers("/", "/error", "/favicon.ico").permitAll()

                        // Admin only – product write operations
                        .requestMatchers(HttpMethod.POST,   "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        // Admin only – dashboard API and order management
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/orders/stats").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/orders/*/status").hasRole("ADMIN")

                        // Admin only – user management
                        .requestMatchers(HttpMethod.GET,  "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users/admin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                        // Admin only – customer list / delete, payments, deliveries, bulk data
                        .requestMatchers(HttpMethod.GET,    "/api/customers").hasRole("ADMIN")
                        // DELETE /api/customers/{id}: owner or admin (checked by CustomerOwnershipPolicy)
                        .requestMatchers(HttpMethod.DELETE, "/api/orders/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/payment-details/getall", "/payment-details/update",
                                "/payment-details/delete/**").hasRole("ADMIN")
                        .requestMatchers("/delivery/create", "/delivery/update", "/delivery/delete/**",
                                "/delivery/getall", "/api/delivery/create", "/api/delivery/update",
                                "/api/delivery/delete/**", "/api/delivery/getall").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/order-items", "/shopping-cart/getall",
                                "/api/shipping-addresses").hasRole("ADMIN")
                        .requestMatchers("/api/logins/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/registrations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/registrations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/registrations/**").hasRole("ADMIN")

                        // Admin only – endpoints the website never uses for customers
                        // (they would let one customer read or change another customer's data)
                        .requestMatchers("/api/order-items/**").hasRole("ADMIN")
                        .requestMatchers("/delivery/**", "/api/delivery/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/shipping-addresses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/shipping-addresses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/shipping-addresses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/ratings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/ratings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/shopping-cart/create", "/shopping-cart/update").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/shopping-cart/delete").hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Authentication required. Please sign in.\"}");
                        })
                        .accessDeniedHandler((request, response, e) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Access denied. You do not have permission to do this.\"}");
                        })
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:5173"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
