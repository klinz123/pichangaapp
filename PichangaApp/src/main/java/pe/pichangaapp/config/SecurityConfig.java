package pe.pichangaapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pe.pichangaapp.security.LoginSuccessHandler;
import pe.pichangaapp.security.UsuarioUserDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioUserDetailsService userDetailsService;
    private final LoginSuccessHandler loginSuccessHandler;

    @Value("${app.seguridad.remember-me-key:${random.uuid}}")
    private String rememberMeKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // RNF01: BCrypt con factor de trabajo 12.
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/canchas", "/canchas/**", "/pichangas",
                                "/login", "/registro", "/error/**", "/favicon.ico",
                                "/css/**", "/js/**", "/vendor/**", "/images/**",
                                "/img/**", "/fonts/**").permitAll()
                        .requestMatchers("/pichangas/crear", "/pichangas/*/unirse", "/pichangas/*/salir",
                                "/pichangas/*/cancelar").authenticated()
                        .requestMatchers("/panel/**").hasRole("PROPIETARIO")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/perfil/**", "/mis-reservas/**", "/notificaciones/**",
                                "/reservas/**", "/pagos/**", "/calificaciones/**").authenticated()
                        .requestMatchers("/pichangas/*").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(loginSuccessHandler)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .sessionManagement(session -> session
                        .sessionFixation(fixation -> fixation.changeSessionId())
                        .maximumSessions(3)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/login?expirada"))
                .rememberMe(remember -> remember
                        .key(rememberMeKey)
                        .tokenValiditySeconds(60 * 60 * 24 * 7)
                        .userDetailsService(userDetailsService))
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .referrerPolicy(referrer -> referrer.policy(
                                org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; img-src 'self' data:; style-src 'self' 'unsafe-inline'; "
                                        + "script-src 'self'; font-src 'self'; form-action 'self'; "
                                        + "frame-ancestors 'none'; base-uri 'self'")))
                .exceptionHandling(handling -> handling.accessDeniedPage("/error/403"));

        return http.build();
    }
}
