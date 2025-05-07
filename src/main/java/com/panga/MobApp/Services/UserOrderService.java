// src/main/java/com/panga/MobApp/Services/UserOrderService.java
package com.panga.MobApp.Services;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.panga.MobApp.Models.UserOrder;
import com.panga.MobApp.Repository.UserOrderRepository;

@Service
public class UserOrderService {

    @Autowired
    private UserOrderRepository orderRepository;

    public void saveOrders(List<UserOrder> orders) {
        if (orders != null && !orders.isEmpty()) {
            orderRepository.saveAll(orders);
        }
    }

    public List<UserOrder> getOrdersByUser(String username) {
        return orderRepository.findByUsernameOrderByOrderDateAsc(username);
    }
    
    public List<UserOrder> getReportUsageByUser(String username) {
        return orderRepository.findByUsernameAndReportUsageOrderByOrderDateAsc(username, 1);
    }
    public List<UserOrder> getAllReportUsageEntries() {
        return orderRepository.findByReportUsageOrderByOrderDateDesc(1);
    }
    
    public List<UserOrder> getAllNormalOrders()
    {
    	return orderRepository.findByReportUsageOrderByOrderDateDesc(0);
    }
    
    public int getUnseenOrderCount() {
        List<UserOrder> all = orderRepository.findByReportUsageOrderByOrderDateDesc(0);
        return (int) all.stream()
        	    .filter(o -> !o.isCheckedStatus())
        	    .collect(Collectors.groupingBy(o -> o.getUsername() + "|" + o.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
)))
        	    .keySet()
        	    .size();

    }
    
    public void markAllOrdersSeen() {
        List<UserOrder> unseen = orderRepository.findByReportUsageOrderByOrderDateDesc(0)
                                    .stream().filter(o -> !o.isCheckedStatus()).toList();
        unseen.forEach(order -> order.setCheckedStatus(true));
        orderRepository.saveAll(unseen);
    }

}
