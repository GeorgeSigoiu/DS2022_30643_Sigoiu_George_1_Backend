package org.sigoiugeorge.energy.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(@NotNull AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(@NotNull HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, FilterChain chain, @NotNull Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        List<String> collect = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        Jwt.createAccessToken(request, user.getUsername(), collect);

        String access_token = Jwt.createAccessToken(request, user.getUsername(), collect);
        String refresh_token = Jwt.createRefreshToken(request, user.getUsername());

        response.setHeader("access_token", access_token);
        response.setHeader("refresh_token", refresh_token);

        Map<String, String> headers = new HashMap<>();
        headers.put("access_token", access_token);
        headers.put("refresh_token", refresh_token);
        new ObjectMapper().writeValue(response.getOutputStream(), headers);
    }
}
