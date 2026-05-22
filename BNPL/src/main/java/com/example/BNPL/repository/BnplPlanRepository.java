package com.example.BNPL.repository;

import com.example.BNPL.entity.BnplPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

@Repository
public interface BnplPlanRepository extends JpaRepository<BnplPlan, Long> {
    
    @NonNull
    @Override
    <S extends BnplPlan> S save(@NonNull S entity);
}
