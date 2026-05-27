package com.campustrash.service;

import com.campustrash.dto.RejectRequest;
import com.campustrash.entity.Order;
import com.campustrash.entity.User;
import com.campustrash.repository.OrderRepository;
import com.campustrash.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public AdminService(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public void checkAdmin(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!"admin".equals(user.getRole())) {
            throw new RuntimeException("需要管理员权限");
        }
    }

    public Map<String, Object> getUsers() {
        List<User> users = userRepository.findAllByOrderByCreatedAtDesc();
        return Map.of("users", users);
    }

    @Transactional
    public Map<String, String> freezeUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if ("admin".equals(user.getRole())) {
            throw new RuntimeException("不能冻结管理员账号");
        }
        user.setIsFrozen(1);
        userRepository.save(user);
        return Map.of("message", "账号已冻结");
    }

    @Transactional
    public Map<String, String> unfreezeUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setIsFrozen(0);
        userRepository.save(user);
        return Map.of("message", "账号已解冻");
    }

    @Transactional
    public Map<String, String> deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if ("admin".equals(user.getRole())) {
            throw new RuntimeException("不能删除管理员账号");
        }
        orderRepository.deleteByUserId(id);
        userRepository.delete(user);
        return Map.of("message", "用户已删除");
    }

    public Map<String, Object> getReviewOrders() {
        List<Order> orders = orderRepository.findByStatusInOrderByCreatedAtDesc(
                List.of("pending_review", "rejected"));
        return Map.of("orders", orders);
    }

    @Transactional
    public Map<String, Object> approveOrder(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        if (!"pending_review".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不是待审核");
        }
        order.setStatus("approved");
        orderRepository.save(order);
        return Map.of("order", order);
    }

    @Transactional
    public Map<String, Object> rejectOrder(String id, RejectRequest req) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        if (!"pending_review".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不是待审核");
        }
        order.setStatus("rejected");
        order.setReviewMessage(req.getMessage());
        orderRepository.save(order);
        return Map.of("order", order);
    }
}
