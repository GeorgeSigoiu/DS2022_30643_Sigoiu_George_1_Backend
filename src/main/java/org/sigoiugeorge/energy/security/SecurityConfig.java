package org.sigoiugeorge.energy.security;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        secureConfigure(http);
//        configure_permitall(http);
    }

    private void configure_permitall(@NotNull HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        http.csrf().disable();
        http.cors().configurationSource(request -> getCorsConfiguration());
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/get/users").permitAll();
        http.authorizeRequests().antMatchers("/login", "/token/refresh").permitAll();
        http.authorizeRequests().anyRequest().permitAll();
        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
//        http.addFilterBefore(customCorsFilter(), SessionManagementFilter.class);
    }

    private void secureConfigure(@NotNull HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
//        customAuthenticationFilter.setFilterProcessesUrl("/api/login");
        http.csrf().disable();
        http.cors().configurationSource(request -> getCorsConfiguration());
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        for links that we dont need auth, not secured
//        http.authorizeRequests().antMatchers("/get/users").permitAll();

        http.authorizeRequests().antMatchers("/login", "/token/refresh").permitAll();

        String[] clientAllowedLinks = new String[]{

        };
        http.authorizeRequests().antMatchers(GET, adminAllowedGetLinks()).hasAnyAuthority("admin");
        http.authorizeRequests().antMatchers(POST, adminAllowedPostLinks()).hasAnyAuthority("admin");
        http.authorizeRequests().antMatchers(GET, clientAllowedLinks).hasAnyAuthority("client");

        http.authorizeRequests().anyRequest().authenticated();
//        http.authorizeRequests().anyRequest().permitAll();

        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Contract(value = " -> new", pure = true)
    private String @NotNull [] adminAllowedGetLinks() {
        return new String[]{
                "/get/users",
                "/get/credentials",
        };
    }

    @Contract(value = " -> new", pure = true)
    private String @NotNull [] adminAllowedPostLinks() {
        return new String[]{
                "/add/credentials",
                "/add/user",
        };
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @NotNull
    private CorsConfiguration getCorsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3000/login"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
        configuration.setAllowedHeaders(List.of("*"));
        return configuration;
    }
}
