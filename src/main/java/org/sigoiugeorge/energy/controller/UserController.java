package org.sigoiugeorge.energy.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.sigoiugeorge.energy.model.User;
import org.sigoiugeorge.energy.security.Jwt;
import org.sigoiugeorge.energy.service.api.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/add/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = service.create(user);
        return ResponseEntity
                .ok()
                .body(createdUser);
    }

    @GetMapping("/get/users")
    public ResponseEntity<List<User>> listAllUsers() {
        List<User> all = service.getAll();
        return ResponseEntity.ok().body(all);
    }

    @GetMapping("/token/refresh")
    public void refreshToken(@NotNull HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                DecodedJWT decodedJWT = Jwt.getDecodedJWT(authorizationHeader);
                String username = decodedJWT.getSubject();
                User user = service.getUser(username);

                String access_token = Jwt.createAccessToken(request, user.getCredentials().getUsername(), List.of(user.getRole()));
                String refresh_token = Jwt.createRefreshToken(request, user.getCredentials().getUsername());

                response.setHeader("access_token", access_token);
                response.setHeader("refresh_token", refresh_token);

            } catch (Exception exception) {
                Jwt.handleExceptionInResponse(response, exception);
            }
        } else {
            throw new RuntimeException("Refresh token is missing!");
        }
    }


}
