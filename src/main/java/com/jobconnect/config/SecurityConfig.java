package com.jobconnect.config;

import com.jobconnect.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);  // Inject the UserService for user validation
        auth.setPasswordEncoder(passwordEncoder); // Inject the PasswordEncoder for password hashing
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Temporarily disabling CSRF for this example (be mindful in production)
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/", "/auth/**", "/css/**", "/js/**", "/images/**", "/assessment/**").permitAll()  // Permit these paths
                .anyRequest().authenticated()  // All other requests need authentication
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/auth/login")  // Custom login page URL
                .defaultSuccessUrl("/", true)  // Redirect to home page after login
                .permitAll()  // Allow everyone to access the login page
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))  // Logout path
                .logoutSuccessUrl("/auth/login?logout")  // Redirect to login after logout
                .permitAll()  // Allow everyone to log out
            );
        return http.build();
    }
}
