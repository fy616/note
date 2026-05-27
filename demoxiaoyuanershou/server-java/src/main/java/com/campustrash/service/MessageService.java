package com.campustrash.service;

import com.campustrash.dto.MessageRequest;
import com.campustrash.entity.Message;
import com.campustrash.entity.Order;
import com.campustrash.entity.User;
import com.campustrash.repository.MessageRepository;
import com.campustrash.repository.OrderRepository;
import com.campustrash.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public List<Message> getMessages(String orderId, String userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUserId().equals(userId) && !userId.equals(order.getTakerId())) {
            throw new RuntimeException("无权查看消息");
        }

        return messageRepository.findByOrderIdOrderByCreatedAtAsc(orderId);
    }

    public Message sendMessage(String orderId, String userId, MessageRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUserId().equals(userId) && !userId.equals(order.getTakerId())) {
            throw new RuntimeException("无权发送消息");
        }

        if (!"taken".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许聊天");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Message message = new Message();
        message.setOrderId(orderId);
        message.setSenderId(userId);
        message.setSenderName(user.getUsername());
        message.setContent(request.getContent());

        return messageRepository.save(message);
    }

    public long getUnreadCount(String orderId, String userId) {
        return messageRepository.countByOrderIdAndSenderIdNot(orderId, userId);
    }
}
