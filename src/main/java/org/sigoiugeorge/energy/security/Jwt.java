package org.sigoiugeorge.energy.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class Jwt {

    private static final int ACCESS_TOKEN_EXPIRATION_TIME_MIN = 10 * 60 * 1000;
    private static final int REFRESH_TOKEN_EXPIRATION_TIME_MIN = 30 * 60 * 1000;

    @Contract(" -> new")
    public static @NotNull Algorithm getCreationTokenAlgorithm() {
        return Algorithm.HMAC256("secret".getBytes());
    }

    @NotNull
    public static DecodedJWT getDecodedJWT(@NotNull String authorizationHeader) {
        String token = authorizationHeader.substring("Bearer ".length());
        Algorithm algorithm = Jwt.getCreationTokenAlgorithm();
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    @NotNull
    public static String createAccessToken(@NotNull HttpServletRequest request, @NotNull String username, @NotNull List<String> roles) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME_MIN))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", roles)
                .sign(getCreationTokenAlgorithm());
    }

    @NotNull
    public static String createRefreshToken(@NotNull HttpServletRequest request, @NotNull String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME_MIN))
                .withIssuer(request.getRequestURL().toString())
                .sign(getCreationTokenAlgorithm());
    }

    public static void handleExceptionInResponse(@NotNull HttpServletResponse response, @NotNull Exception exception) throws IOException {
        response.setHeader("error", exception.getMessage());
        response.setStatus(FORBIDDEN.value());
        Map<String, String> error = new HashMap<>();
        error.put("error_message", exception.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
