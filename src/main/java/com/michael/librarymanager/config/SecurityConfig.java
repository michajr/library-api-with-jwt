package com.michael.librarymanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @Autowired
  public SecurityConfig(
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint
  ) {
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
    AuthenticationConfiguration auth
  ) throws Exception {
    return auth.getAuthenticationManager();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
  }

  @SuppressWarnings("deprecation")
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity)
    throws Exception {
    httpSecurity.csrf(crsf -> crsf.disable());
    httpSecurity.cors(Customizer.withDefaults());
    httpSecurity.exceptionHandling(ex ->
      ex.authenticationEntryPoint(jwtAuthenticationEntryPoint)
    );

    httpSecurity.sessionManagement(sm ->
      sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );

    httpSecurity.authorizeRequests(auth -> {
      auth.requestMatchers("api/auth/**").permitAll();

      auth
        .requestMatchers(HttpMethod.GET, "api/library/books")
        .hasAnyAuthority("USER", "ADMIN");

      auth
        .requestMatchers(HttpMethod.GET, "api/library/book/**")
        .hasAnyAuthority("USER", "ADMIN");

      auth
        .requestMatchers(HttpMethod.POST, "api/library/book")
        .hasAuthority("ADMIN");

      auth
        .requestMatchers(HttpMethod.PUT, "api/library/book/**")
        .hasAuthority("ADMIN");

      auth
        .requestMatchers(HttpMethod.DELETE, "api/library/book/**")
        .hasAuthority("ADMIN");

      auth.anyRequest().authenticated();
    });

    httpSecurity.addFilterBefore(
      jwtAuthenticationFilter(),
      UsernamePasswordAuthenticationFilter.class
    );

    httpSecurity.httpBasic(Customizer.withDefaults());
    return httpSecurity.build();
  }
}
