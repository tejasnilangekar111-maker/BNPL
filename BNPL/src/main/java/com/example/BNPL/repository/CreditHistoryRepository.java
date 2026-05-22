package com.example.BNPL.repository;

import com.example.BNPL.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface CreditHistoryRepository extends JpaRepository<CreditHistory, Long> {
    List<CreditHistory> findByUserUserIdOrderByDateDesc(Long userId);
}

