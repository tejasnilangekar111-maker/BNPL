package com.example.BNPL.repository;

import com.example.BNPL.entity.EmiSchedule;
import com.example.BNPL.entity.EmiStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmiScheduleRepository extends JpaRepository<EmiSchedule, Long> {
    List<EmiSchedule> findByBnplPlanPlanIdAndStatus(Long planId, EmiStatus status);
    long countByStatus(EmiStatus status);

    @Query("SELECT e FROM EmiSchedule e JOIN FETCH e.bnplPlan p JOIN FETCH p.order o JOIN FETCH o.user WHERE e.scheduleId = :id")
    Optional<EmiSchedule> findByIdWithUser(@Param("id") Long id);
}
