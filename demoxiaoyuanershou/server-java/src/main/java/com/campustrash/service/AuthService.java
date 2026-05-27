package com.campustrash.service;

import com.campustrash.dto.LoginRequest;
import com.campustrash.dto.RegisterRequest;
import com.campustrash.entity.User;
import com.campustrash.repository.UserRepository;
import com.campustrash.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Map<String, Object> register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setPhone(req.getPhone());
        user.setDormitory(req.getDormitory());

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        return Map.of(
                "token", token,
                "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "phone", user.getPhone(),
                        "dormitory", user.getDormitory(),
                        "points", user.getPoints(),
                        "role", user.getRole(),
                        "isFrozen", user.getIsFrozen()
                )
        );
    }

    public Map<String, Object> login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (user.getIsFrozen() == 1) {
            throw new RuntimeException("账号已被冻结，请联系管理员");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        return Map.of(
                "token", token,
                "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "phone", user.getPhone(),
                        "dormitory", user.getDormitory(),
                        "points", user.getPoints(),
                        "role", user.getRole(),
                        "isFrozen", user.getIsFrozen()
                )
        );
    }

    public Map<String, Object> getMe(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return Map.of("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "phone", user.getPhone(),
                "dormitory", user.getDormitory(),
                "points", user.getPoints(),
                "role", user.getRole(),
                "isFrozen", user.getIsFrozen()
        ));
    }
}
