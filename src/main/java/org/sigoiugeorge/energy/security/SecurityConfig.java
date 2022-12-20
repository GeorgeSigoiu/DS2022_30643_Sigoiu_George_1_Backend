package org.sigoiugeorge.energy.security;

import lombok.RequiredArgsConstructor;
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

import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(@NotNull AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        secureConfigure(http);
//        unsecureConfigure(http);
    }

    private void unsecureConfigure(@NotNull HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        http.csrf().disable();
        http.cors().configurationSource(request -> getCorsConfiguration());
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/get/users").permitAll();
        http.authorizeRequests().antMatchers("/login", "/token/refresh").permitAll();
        http.authorizeRequests().anyRequest().permitAll();
        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    private void secureConfigure(@NotNull HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        http.csrf().disable();
        http.cors().configurationSource(request -> getCorsConfiguration());
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/login", "/token/refresh", "/ws-message","/ws-message/*","/ws-message/**","/","/hello").permitAll();

        http.authorizeRequests().antMatchers(GET, SecurityUtils.adminAllowedGetLinks()).hasAnyAuthority("admin");
        http.authorizeRequests().antMatchers(POST, SecurityUtils.adminAllowedPostLinks()).hasAnyAuthority("admin");
        http.authorizeRequests().antMatchers(DELETE, SecurityUtils.adminAllowedDeleteLinks()).hasAnyAuthority("admin");
        http.authorizeRequests().antMatchers(PUT, SecurityUtils.adminAllowedPutLinks()).hasAnyAuthority("admin");

        http.authorizeRequests().antMatchers(GET, SecurityUtils.clientAllowedGetLinks()).hasAnyAuthority("client");
        http.authorizeRequests().antMatchers(POST, SecurityUtils.clientAllowedPostLinks()).hasAnyAuthority("client");

        http.authorizeRequests().antMatchers(GET, SecurityUtils.commonAllowedGetLinks()).hasAnyAuthority("client", "admin");
        http.authorizeRequests().antMatchers(POST, SecurityUtils.commonAllowedPostLinks()).hasAnyAuthority("client", "admin");
        http.authorizeRequests().antMatchers(PUT, SecurityUtils.commonAllowedPutLinks()).hasAnyAuthority("client", "admin");

        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @NotNull
    private CorsConfiguration getCorsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        return configuration;
    }
}
