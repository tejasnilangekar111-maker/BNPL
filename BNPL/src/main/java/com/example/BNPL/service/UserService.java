package com.example.BNPL.service;
import com.example.BNPL.dto.RegisterRequest;
import com.example.BNPL.entity.User;
import com.example.BNPL.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CreditScoringService creditScoringService;

    @SuppressWarnings("null")
    public User register(RegisterRequest req) {
        User user = User.builder()
                .email(req.getEmail())
                .phone(req.getPhone())
                .address(req.getAddress())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .monthlyIncome(req.getMonthlyIncome())
                .creditScore(650) // default starting score
                .build();
        return userRepository.save(user);
    }

    public void updateCreditScore(@NonNull Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Recalculate based on history
        int newScore = creditScoringService.calculateCreditScore(user, user.getCreditHistories());
        user.setCreditScore(newScore);
        userRepository.save(user);
    }

    public UserDetails loadUserByUsername(String email) {

    }
}
