package com.panga.MobApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.panga.MobApp.Models.MeatItem;

@Repository
public interface MeatRepository extends JpaRepository<MeatItem, Long> {}

