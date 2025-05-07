package com.panga.MobApp.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.panga.MobApp.Models.Item;
import com.panga.MobApp.Models.UnitType;
import com.panga.MobApp.Services.ItemService;
import com.panga.MobApp.Services.S3Service;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemController {
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private S3Service s3Service;
	
	int count=1;

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // ✅ Admin Only - Add Item
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Item> addItem(@RequestBody Item item) {
    	System.out.println("Add is Called");
        return ResponseEntity.ok(itemService.addItem(item));
    }
    
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadItemWithImage(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("stockStatus") String stockStatus,
            @RequestParam("availableStock") int stock,
            @RequestParam("unitType") String unitType,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {
        System.out.println("Upload Method is called");

        String imagePath = null;

        if (imageFile != null && !imageFile.isEmpty()) {
        	imagePath = s3Service.uploadFile(imageFile, "items"); // ✅ folder: items/
        }

        Item item = new Item();
        item.setName(name);
        item.setCategory(category);
        item.setStockStatus(stockStatus);
        item.setAvailableStock(stock);
        item.setUnitType(UnitType.valueOf(unitType));
        item.setImagePath(imagePath);

        try {
            Item savedItem = itemService.addItem(item);
            return ResponseEntity.ok(savedItem);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("exists")) {
                return ResponseEntity.status(409).body("Item already exists!");
            }
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    }



    // ✅ Admin Only - Update Item
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
    	System.out.println("Count " + " = " + count);
    	System.out.println(item);
    	count++;
        return ResponseEntity.ok(itemService.updateItem(id, item));
    }

    // ✅ Admin Only - Delete Item
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok("Item deleted successfully");
    }

    // ✅ User & Admin - View All Items
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/all")
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    // ✅ User & Admin - View Item by ID
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Item>> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }
}
