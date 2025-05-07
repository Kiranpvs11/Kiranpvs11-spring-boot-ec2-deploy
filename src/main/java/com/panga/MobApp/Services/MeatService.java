package com.panga.MobApp.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.panga.MobApp.Models.MeatItem;
import com.panga.MobApp.Repository.MeatRepository;

@Service
public class MeatService {

    @Autowired
    private MeatRepository meatRepository;

    public List<MeatItem> getAllMeatItems() {
        return meatRepository.findAll();
    }

    public void updateMeatItems(List<MeatItem> items) {
        for (MeatItem item : items) {
            meatRepository.save(item);
        }
    }
    public void addMeatItem(MeatItem item) {
        meatRepository.save(item);
    }

}
