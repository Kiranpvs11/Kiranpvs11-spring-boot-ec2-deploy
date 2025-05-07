package com.panga.MobApp.Controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.panga.MobApp.Models.TimesheetEntry;
import com.panga.MobApp.Models.User;
import com.panga.MobApp.Repository.TimesheetEntryRepository;
import com.panga.MobApp.Repository.UserRepository;
import com.panga.MobApp.Security.JwtService;
import com.panga.MobApp.Services.TimesheetService;


@RestController
@RequestMapping("/api/timesheet")
public class TimesheetController {

    @Autowired
    private TimesheetService timesheetService;
    
    @Autowired
    private TimesheetEntryRepository repo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/save")
    public ResponseEntity<?> saveEntry(@RequestBody Map<String, Object> request, Principal principal) {
        String username = principal.getName();
        LocalDate date = LocalDate.parse(request.get("date").toString());
        float hours = Float.parseFloat(request.get("hoursWorked").toString());
        String note = request.getOrDefault("note", "").toString();

        TimesheetEntry entry = timesheetService.saveOrUpdate(username, date, hours, note);
        return ResponseEntity.ok(entry);
    }

    @GetMapping("/month")
    public ResponseEntity<?> getEntries(@RequestParam int year, @RequestParam int month, Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(timesheetService.getEntriesForMonth(username, year, month));
    }
    
    
    @GetMapping("/admin/summary")
    public List<Object> getAllUserSummaries(LocalDate startDate, LocalDate endDate) {
        List<User> allUsers = userRepository.findAll();
        List<Object> summaries = new ArrayList<>();

        for (User user : allUsers) {
            if (user.getAdmin() == 1) continue; // ✅ Skip admins

            String username = user.getUsername();

            // ✅ Fetch entries (case-insensitive match)
            List<TimesheetEntry> entries = repo.findByUsernameAndDateBetweenIgnoreCase(username, startDate, endDate);

            Map<LocalDate, Map<String, Object>> dateHours = new HashMap<>();
            float total = 0;
            for (TimesheetEntry entry : entries) {
                float hours = entry.getHoursWorked() != null ? entry.getHoursWorked() : 0;
                String note = entry.getNote() != null ? entry.getNote() : "";

                Map<String, Object> entryData = new HashMap<>();
                entryData.put("hours", hours);
                entryData.put("note", note);

                dateHours.put(entry.getDate(), entryData);
                total += hours;
            }
            Map<String, Object> result = new HashMap<>();
            result.put("username", username);
            result.put("totalHours", total);
            result.put("dailyBreakdown", dateHours);

            summaries.add(result);
        }

        return summaries;
    }



	
}
