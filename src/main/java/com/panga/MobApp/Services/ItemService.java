package com.panga.MobApp.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.panga.MobApp.Models.Item;
import com.panga.MobApp.Repository.ItemRepository;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // ✅ Add a new item (Prevents duplicate names)
    public Item addItem(Item item) {
        List<Item> existingItems = itemRepository.findAllByNameIgnoreCase(item.getName());

        for (Item existing : existingItems) {
            if (!existing.isMarkDeletion()) {
                throw new RuntimeException("Item already exists!");
            }
        }

        return itemRepository.save(item);
    }


    // ✅ Get all items
    public List<Item> getAllItems() {
    	return itemRepository.findByMarkDeletionFalse();
    }

    // ✅ Get item by ID
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    // ✅ Update an existing item
    public Item updateItem(Long id, Item newItem) {
        return itemRepository.findById(id).map(existingItem -> {
            existingItem.setName(newItem.getName());
            existingItem.setCategory(newItem.getCategory());
            existingItem.setImagePath(newItem.getImagePath());
            existingItem.setStockStatus(newItem.getStockStatus());
            existingItem.setAvailableStock(newItem.getAvailableStock());
            return itemRepository.save(existingItem);
        }).orElseThrow(() -> new RuntimeException("Item not found"));
    }

    // ✅ Delete an item by ID
    public void deleteItem(Long id) {
        Optional<Item> itemOpt = itemRepository.findById(id);
        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            try {
                itemRepository.deleteById(id); // Try delete (e.g., if no FK ref)
            } catch (Exception e) {
                // FK constraint likely triggered, mark for deletion
                item.setMarkDeletion(true);
                itemRepository.save(item);
            }
        } else {
            throw new RuntimeException("Item not found");
        }
    }

    
    public boolean itemExistsByName(String name) {
        return itemRepository.existsByNameIgnoreCase(name.trim());
    }
    
 

}
