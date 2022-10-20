package org.sigoiugeorge.energy.controller;

import lombok.RequiredArgsConstructor;
import org.sigoiugeorge.energy.model.User;
import org.sigoiugeorge.energy.service.api.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/add-user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = service.create(user);
        return ResponseEntity
                .ok()
                .body(createdUser);
    }

}
