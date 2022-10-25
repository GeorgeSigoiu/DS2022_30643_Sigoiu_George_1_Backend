package org.sigoiugeorge.energy.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.sigoiugeorge.energy.model.Credentials;
import org.sigoiugeorge.energy.model.MeteringDevice;
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
@CrossOrigin
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

    @GetMapping("/get/credentials-id/user-id={userId}")
    public ResponseEntity<Long> getUserCredentialsId(@PathVariable Long userId) {
        User user = service.get(userId);
        Long id = user.getCredentials().getId();
        return ResponseEntity.ok().body(id);
    }

    @DeleteMapping("/delete/user-id={userId}")
    public void deleteUser(@PathVariable Long userId) {
        service.remove(userId);
    }

    @PutMapping("/update/user-id={userId}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable Long userId) {
        User theUser = service.get(userId);
        System.out.println("update user: " + theUser);
        System.out.println("with: " + user);
        String name = user.getName();
        if (name != null) {
            theUser.setName(name);
        }
        String role = user.getRole();
        if (role != null) {
            theUser.setRole(role);
        }
        Credentials credentials = user.getCredentials();
        if (credentials != null) {
            theUser.setCredentials(credentials);
        }
        List<MeteringDevice> devices = user.getMeteringDevices();
        if (devices != null) {
            theUser.setMeteringDevices(devices);
        }
        System.out.println("the user: " + theUser);
        User update = service.update(theUser);
        return ResponseEntity.ok().body(update);
//        return ResponseEntity.ok().build();
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
