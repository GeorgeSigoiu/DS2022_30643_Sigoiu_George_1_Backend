package org.sigoiugeorge.energy.dao;

import org.sigoiugeorge.energy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
}
