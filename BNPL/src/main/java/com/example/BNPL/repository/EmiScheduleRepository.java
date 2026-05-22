package com.example.BNPL.repository;

import com.example.BNPL.entity.EmiSchedule;
import com.example.BNPL.entity.EmiStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmiScheduleRepository extends JpaRepository<EmiSchedule, Long> {
    List<EmiSchedule> findByBnplPlanPlanIdAndStatus(Long planId, EmiStatus status);
}
