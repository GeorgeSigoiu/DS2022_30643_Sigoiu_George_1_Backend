package org.sigoiugeorge.energy.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.sigoiugeorge.energy.model.Credentials;
import org.sigoiugeorge.energy.model.MeteringDevice;
import org.sigoiugeorge.energy.model.User;
import org.sigoiugeorge.energy.security.Jwt;
import org.sigoiugeorge.energy.service.api.MeteringDeviceService;
import org.sigoiugeorge.energy.service.api.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MeteringDeviceService deviceService;

    @PostMapping("/add/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.create(user);
        return ResponseEntity
                .ok()
                .body(createdUser);
    }

    @GetMapping("/get/user-id={userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        User user = userService.get(userId);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/get/user-username={username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUser(username);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/get/users")
    public ResponseEntity<List<User>> listAllUsers() {
        List<User> all = userService.getAll();
        return ResponseEntity.ok().body(all);
    }

    @GetMapping("/get/credentials-id/user-id={userId}")
    public ResponseEntity<Long> getUserCredentialsId(@PathVariable Long userId) {
        User user = userService.get(userId);
        Long id = user.getCredentials().getId();
        return ResponseEntity.ok().body(id);
    }

    @GetMapping("/get/devices-for-user/user-id={userId}")
    public ResponseEntity<List<MeteringDevice>> getDevicesFromUser(@PathVariable Long userId) {
        User user = userService.get(userId);
        List<MeteringDevice> devices = user.getMeteringDevices();
        return ResponseEntity.ok().body(devices);
    }

    @DeleteMapping("/delete/user-id={userId}")
    public void deleteUser(@PathVariable Long userId) {
        List<MeteringDevice> devices = deviceService.getAll();
        for (MeteringDevice device : devices) {
            User user = device.getUser();
            if (user == null) {
                continue;
            }
            if (user.getId().equals(userId)) {
                device.setUser(null);
                deviceService.update(device);
            }
        }
        userService.remove(userId);
    }

    @PutMapping("/update/user-id={userId}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable Long userId) {
        User theUser = userService.get(userId);
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
        User update = userService.update(theUser);
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
                User user = userService.getUser(username);

                String access_token = Jwt.createAccessToken(request, user.getCredentials().getUsername(), List.of(user.getRole()));
                String refresh_token = Jwt.createRefreshToken(request, user.getCredentials().getUsername());

                response.setHeader("access_token", access_token);
                response.setHeader("refresh_token", refresh_token);

                Map<String, String> headers = new HashMap<>();
                headers.put("access_token", access_token);
                headers.put("refresh_token", refresh_token);
                headers.put("username", username);
                new ObjectMapper().writeValue(response.getOutputStream(), headers);
            } catch (Exception exception) {
                Jwt.handleExceptionInResponse(response, exception);
            }
        } else {
            throw new RuntimeException("Refresh token is missing!");
        }
    }


}
