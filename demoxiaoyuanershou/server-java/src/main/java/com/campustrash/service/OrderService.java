package com.campustrash.service;

import com.campustrash.dto.OrderRequest;
import com.campustrash.entity.Order;
import com.campustrash.entity.TaskCategory;
import com.campustrash.entity.User;
import com.campustrash.repository.OrderRepository;
import com.campustrash.repository.TaskCategoryRepository;
import com.campustrash.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TaskCategoryRepository categoryRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, TaskCategoryRepository categoryRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public Map<String, Object> getOrders(String status, String userId) {
        User user = userRepository.findById(userId).orElseThrow();
        boolean isAdmin = "admin".equals(user.getRole());

        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderRepository.findByStatusOrderByCreatedAtDesc(status);
        } else {
            orders = orderRepository.findAllByOrderByCreatedAtDesc();
        }

        if (!isAdmin) {
            orders = orders.stream()
                    .filter(o -> !List.of("pending_review", "rejected").contains(o.getStatus())
                            || o.getUserId().equals(userId))
                    .toList();
        }

        return Map.of("orders", orders);
    }

    public Map<String, Object> createOrder(OrderRequest req, String userId, String username) {
        User user = userRepository.findById(userId).orElseThrow();
        if (user.getIsFrozen() == 1) {
            throw new RuntimeException("账号已被冻结，无法发布订单");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setUsername(username);
        order.setDescription(req.getDescription());
        order.setLocation(req.getLocation());
        order.setReward(req.getReward() != null ? req.getReward() : 0);
        order.setContact(req.getContact() != null ? req.getContact() : "");
        order.setCategory(req.getCategory());
        order.setDeadline(req.getDeadline());

        // 根据分类决定是否需要审核
        if (req.getCategory() != null && !req.getCategory().isEmpty()) {
            TaskCategory category = categoryRepository.findByName(req.getCategory()).orElse(null);
            if (category != null && !category.getNeedReview()) {
                order.setStatus("approved");
            } else {
                order.setStatus("pending_review");
            }
        } else {
            order.setStatus("pending_review");
        }

        orderRepository.save(order);
        return Map.of("order", order);
    }

    @Transactional
    public Map<String, Object> resubmitOrder(String orderId, OrderRequest req, String userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("只能修改自己的订单");
        }
        if (!"rejected".equals(order.getStatus())) {
            throw new RuntimeException("只有被驳回的订单才能修改");
        }

        order.setDescription(req.getDescription());
        order.setLocation(req.getLocation());
        order.setReward(req.getReward() != null ? req.getReward() : 0);
        order.setContact(req.getContact() != null ? req.getContact() : "");
        order.setStatus("pending_review");
        order.setReviewMessage(null);

        orderRepository.save(order);
        return Map.of("order", order);
    }

    @Transactional
    public Map<String, Object> takeOrder(String orderId, String userId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"approved".equals(order.getStatus())) {
            throw new RuntimeException("订单暂不可接取");
        }
        if (order.getUserId().equals(userId)) {
            throw new RuntimeException("不能接自己的订单");
        }

        order.setStatus("taken");
        order.setTakerId(userId);
        order.setTakerName(username);

        orderRepository.save(order);
        return Map.of("order", order);
    }

    @Transactional
    public Map<String, Object> completeOrder(String orderId, String userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getTakerId().equals(userId)) {
            throw new RuntimeException("只有接单者可以完成订单");
        }
        if (!"taken".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus("completed");
        orderRepository.save(order);

        int points = order.getReward() > 0 ? order.getReward() : 1;
        User taker = userRepository.findById(userId).orElseThrow();
        taker.setPoints(taker.getPoints() + points);
        userRepository.save(taker);

        return Map.of("order", order);
    }

    public Map<String, Object> deleteOrder(String orderId, String userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("只能删除自己的订单");
        }

        orderRepository.delete(order);
        return Map.of("message", "删除成功");
    }
}
