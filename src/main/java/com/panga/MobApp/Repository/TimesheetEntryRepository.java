package com.panga.MobApp.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.panga.MobApp.Models.TimesheetEntry;
import com.panga.MobApp.Models.User;

public interface TimesheetEntryRepository extends JpaRepository<TimesheetEntry, Long> {
    List<TimesheetEntry> findByUsernameAndDateBetween(String username, LocalDate start, LocalDate end);
    Optional<TimesheetEntry> findByUsernameAndDate(String username, LocalDate date);
    List<TimesheetEntry> findByDateBetween(LocalDate start, LocalDate end);
    List<TimesheetEntry> findByUsernameAndDateBetweenIgnoreCase(String username, LocalDate startDate, LocalDate endDate);




}
