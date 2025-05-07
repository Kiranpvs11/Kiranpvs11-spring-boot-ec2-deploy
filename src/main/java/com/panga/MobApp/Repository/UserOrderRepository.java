// src/main/java/com/panga/MobApp/Repository/UserOrderRepository.java
package com.panga.MobApp.Repository;

import com.panga.MobApp.Models.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {

    // ✅ This must be public (even though it's technically optional in interfaces)
    List<UserOrder> findByUsernameOrderByOrderDateAsc(String username);
    
    // Get all normal orders (reportUsage = 0)
    List<UserOrder> findByUsernameAndReportUsageOrderByOrderDateAsc(String username, int reportUsage);

    
 // Get all report usage records (reportUsage = 1)
    List<UserOrder> findByReportUsageOrderByOrderDateDesc(int reportUsage);
}
 