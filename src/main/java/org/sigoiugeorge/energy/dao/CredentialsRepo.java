package org.sigoiugeorge.energy.dao;

import org.sigoiugeorge.energy.model.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialsRepo extends JpaRepository<Credentials, Long> {
}
