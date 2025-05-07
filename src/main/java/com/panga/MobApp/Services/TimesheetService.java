package com.panga.MobApp.Services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.panga.MobApp.Models.TimesheetEntry;
import com.panga.MobApp.Models.User;
import com.panga.MobApp.Repository.TimesheetEntryRepository;
import com.panga.MobApp.Repository.UserRepository;
import com.panga.MobApp.Security.JwtService;

@Service
public class TimesheetService {

    @Autowired
    private TimesheetEntryRepository repo;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtService jwtService;

    public TimesheetEntry saveOrUpdate(String username, LocalDate date, float hoursWorked, String note) {
        Optional<TimesheetEntry> existing = repo.findByUsernameAndDate(username, date);
        TimesheetEntry entry = existing.orElse(new TimesheetEntry());
        entry.setUsername(username);
        entry.setDate(date);
        entry.setHoursWorked(hoursWorked);
        entry.setNote(note);
        return repo.save(entry);
    }

    public List<TimesheetEntry> getEntriesForMonth(String username, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);

        LocalDate startDate = ym.atDay(1); // first day of the month
        LocalDate endDate = ym.atEndOfMonth(); // last day of the month

        return repo.findByUsernameAndDateBetweenIgnoreCase(username, startDate, endDate);
    }

    
    public List<Object> getAllUserSummaries(LocalDate start, LocalDate end) {
        List<TimesheetEntry> allEntries = repo.findByDateBetween(start, end);
        Map<String, Map<LocalDate, Float>> userMap = new HashMap<>();

        for (TimesheetEntry entry : allEntries) {
            String user = entry.getUsername();
            LocalDate date = entry.getDate();
            float hours = entry.getHoursWorked() != null ? entry.getHoursWorked() : 0;

            userMap.putIfAbsent(user, new HashMap<>());
            userMap.get(user).put(date, hours);
        }

        List<Object> summaries = new ArrayList<>();

        for (Map.Entry<String, Map<LocalDate, Float>> userEntry : userMap.entrySet()) {
            String username = userEntry.getKey();
            Map<LocalDate, Float> dateHours = userEntry.getValue();
            float total = 0;
            for (Float h : dateHours.values()) {
                total += h;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("username", username);
            result.put("totalHours", total);
            result.put("dailyBreakdown", dateHours);

            summaries.add(result);
        }

        return summaries;
    }
    
    public boolean isAdmin(String token) {
        String actualToken = token.replace("Bearer ", "");
        String username = jwtService.extractUsername(actualToken);  // ✅ Use jwtService

        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.isPresent() && userOpt.get().getAdmin() == 1;
    }




}
