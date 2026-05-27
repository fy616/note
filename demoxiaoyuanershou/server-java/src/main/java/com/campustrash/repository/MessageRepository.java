package com.campustrash.repository;

import com.campustrash.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findByOrderIdOrderByCreatedAtAsc(String orderId);
    long countByOrderIdAndSenderIdNot(String orderId, String senderId);
}
