package com.panga.MobApp.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.panga.MobApp.Models.MeatItem;
import com.panga.MobApp.Services.MeatService;

@RestController
@RequestMapping("/api/meat")
public class MeatController {

    @Autowired
    private MeatService meatService;

    @GetMapping
    public List<MeatItem> getAllMeatItems() {
        List<MeatItem> list = meatService.getAllMeatItems();
        System.out.println("🔍 DB returned " + list.size() + " items.");
        return list;
    }

    @PutMapping
    public ResponseEntity<?> updateMeatItems(@RequestBody List<MeatItem> items) {
        meatService.updateMeatItems(items);
        return ResponseEntity.ok("Updated successfully");
    }
    
    @PostMapping
    public ResponseEntity<?> addMeatItem(@RequestBody MeatItem item) {
        meatService.addMeatItem(item);
        return ResponseEntity.ok("Item added successfully");
    }

}