package com.panga.MobApp.Repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.panga.MobApp.Models.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    boolean existsByName(String name); // ✅ Custom query to check if an item already exists
    boolean existsByNameIgnoreCase(String name);
    List<Item> findByMarkDeletionFalse();
    List<Item> findAllByNameIgnoreCase(String name);



}
