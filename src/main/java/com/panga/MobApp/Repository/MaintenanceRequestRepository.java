package com.panga.MobApp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.panga.MobApp.Models.MaintenanceRequest;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findByUserUsername(String username);
    
    long countBySeenFalse();
    List<MaintenanceRequest> findBySeenFalse();
}
