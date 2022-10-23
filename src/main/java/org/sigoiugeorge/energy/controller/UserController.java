package org.sigoiugeorge.energy.controller;

import lombok.RequiredArgsConstructor;
import org.sigoiugeorge.energy.model.User;
import org.sigoiugeorge.energy.service.api.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/get/users")
    public ResponseEntity<List<User>> listAllUsers(){
        List<User> all = service.getAll();
        return ResponseEntity.ok().body(all);
    }

}
