package org.sigoiugeorge.energy.dao;

import org.sigoiugeorge.energy.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepo extends JpaRepository<Message, Long> {
}
