package com.campustrash.repository;

import com.campustrash.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findAllByOrderByCreatedAtDesc();
    List<Order> findByStatusOrderByCreatedAtDesc(String status);
    List<Order> findByStatusInOrderByCreatedAtDesc(List<String> statuses);

    List<Order> findByUserId(String userId);
    List<Order> findByTakerId(String takerId);
    void deleteByUserId(String userId);
}
