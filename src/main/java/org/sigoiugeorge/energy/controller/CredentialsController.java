package org.sigoiugeorge.energy.controller;

import lombok.RequiredArgsConstructor;
import org.sigoiugeorge.energy.model.Credentials;
import org.sigoiugeorge.energy.service.api.CredentialsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class CredentialsController {

    private final CredentialsService service;

    @GetMapping("/get/credentials")
    public ResponseEntity<List<Credentials>> getAllCredentials() {
        return ResponseEntity.ok().body(service.getAll());
    }

    @PostMapping("/add/credentials")
    public ResponseEntity<Credentials> addCredentials(@RequestBody Credentials credentials) {
        Credentials credentials1 = service.create(credentials);
        return ResponseEntity.ok().body(credentials1);
    }

    @DeleteMapping("/delete/credentials-id={credentialsId}")
    public void deleteCredentials(@PathVariable Long credentialsId) {
        service.remove(credentialsId);
    }

    @PutMapping("/update/credentials-id={credentialsId}")
    public ResponseEntity<Credentials> updateCredentials(@RequestBody Credentials credentials, @PathVariable Long credentialsId){
        Credentials update = service.update(credentials);
        return ResponseEntity.ok().body(update);
    }
}
