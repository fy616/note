package com.campustrash.config;

import com.campustrash.entity.User;
import com.campustrash.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isPresent()) {
            System.out.println("管理员账号已存在，跳过");
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setPhone("13800000000");
        admin.setDormitory("行政楼");
        admin.setRole("admin");

        userRepository.save(admin);
        System.out.println("管理员账号创建成功");
        System.out.println("用户名: admin");
        System.out.println("密码: admin123");
    }
}
