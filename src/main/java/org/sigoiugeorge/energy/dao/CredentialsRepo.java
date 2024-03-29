package org.sigoiugeorge.energy.dao;

import org.sigoiugeorge.energy.model.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialsRepo extends JpaRepository<Credentials, Long> {
    Optional<Credentials> findByUsername(String username);
}
