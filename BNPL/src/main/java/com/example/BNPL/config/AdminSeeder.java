package com.example.BNPL.config;

import com.example.BNPL.entity.Role;
import com.example.BNPL.entity.User;
import com.example.BNPL.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@flexipay.com").isEmpty()) {
            User admin = User.builder()
                    .email("admin@flexipay.com")
                    .phone("0000000000")
                    .address("FlexiPay HQ")
                    .passwordHash(passwordEncoder.encode("Admin@123"))
                    .creditScore(850)
                    .monthlyIncome(new BigDecimal("999999"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Admin user created — email: admin@flexipay.com, password: Admin@123");
        } else {
            log.info("Admin user already exists, skipping seeding.");
        }
    }
}
