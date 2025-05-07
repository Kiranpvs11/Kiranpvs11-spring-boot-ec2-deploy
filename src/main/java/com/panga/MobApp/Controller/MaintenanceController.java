package com.panga.MobApp.Controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.panga.MobApp.Models.MaintenanceRequest;
import com.panga.MobApp.Models.User;
import com.panga.MobApp.Repository.MaintenanceRequestRepository;
import com.panga.MobApp.Repository.UserRepository;
import com.panga.MobApp.Services.S3Service;

import jakarta.annotation.PostConstruct;


@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {
	
	@PostConstruct
	public void init() {
	    System.out.println("✅ MaintenanceController initialized");
	}

	
	@Autowired
	private S3Service s3Service;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
    @Autowired
    private MaintenanceRequestRepository repo;

    @Autowired
    private UserRepository userRepo;

    @Value("${upload.directory}")
    private String uploadDir;

    @PostMapping("/save")
    public ResponseEntity<?> saveRequest(
            @RequestParam("type") String type,
            @RequestParam("description") String description,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) {
        System.out.println("✅ saveRequest endpoint HIT");

        try {
            // ✅ Get logged-in username from SecurityContext (works for multipart + JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            System.out.println("🔐 Authenticated user: " + username);

            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            MaintenanceRequest req = new MaintenanceRequest();
            req.setType(type);
            req.setDescription(description);
            req.setCreatedAt(LocalDateTime.now());
            req.setUser(user);

            List<String> imagePaths = new ArrayList<>();

            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            if (images != null && !images.isEmpty()) {
            	final long MAX_FILE_SIZE = 3 * 1024 * 1024; // 5MB in bytes

            	for (MultipartFile image : images) {
            		
            		long sizeInBytes = image.getSize();
            		double sizeInKB = sizeInBytes / 1024.0;
            		double sizeInMB = sizeInKB / 1024.0;

            		System.out.println("📦 Image size: " + sizeInBytes + " bytes (" +
            		                   String.format("%.2f", sizeInKB) + " KB, " +
            		                   String.format("%.2f", sizeInMB) + " MB)");

            	    if (image != null && !image.isEmpty()) {
            	        String contentType = image.getContentType();

            	        // ✅ File type validation
            	        if (contentType == null || 
            	            !(contentType.equalsIgnoreCase("image/jpeg") || 
            	              contentType.equalsIgnoreCase("image/png") || 
            	              contentType.equalsIgnoreCase("image/jpg"))) {
            	            return ResponseEntity.badRequest().body("❌ Invalid file type: Only JPEG and PNG are allowed.");
            	        }

            	        // ✅ File size validation
            	        if (image.getSize() > MAX_FILE_SIZE) {
            	            return ResponseEntity.badRequest().body("❌ Image size exceeds 5MB limit.");
            	        }

            	        String originalFilename = image.getOriginalFilename();
            	        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            	            originalFilename = "image_" + UUID.randomUUID() + ".jpg";
            	        }

            	        String filename = UUID.randomUUID() + "-" + StringUtils.cleanPath(originalFilename);
            	        File dest = new File(uploadFolder, filename);
            	        System.out.println("📸 Saving to: " + dest.getAbsolutePath());

            	        try {
            	        	String s3Url = s3Service.uploadFile(image, "maintenance");
            	        	imagePaths.add(s3Url);

            	        } catch (IOException e) {
            	            e.printStackTrace();
            	            return ResponseEntity.status(500).body("❌ Failed to save image: " + filename);
            	        }
            	    }
            	}


            }

            req.setImagePaths(imagePaths);
            repo.save(req);
            messagingTemplate.convertAndSend("/topic/unseen", "refresh");

            return ResponseEntity.ok("✅ Request submitted with " + imagePaths.size() + " image(s)");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(401).body("❌ Unauthorized: User not found");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Internal Server Error: " + e.getMessage());
        }
    }


    @GetMapping("/user")
    public List<MaintenanceRequest> getUserRequests(@AuthenticationPrincipal UserDetails userDetails) {
        return repo.findByUserUsername(userDetails.getUsername());
    }

    @GetMapping("/all")
    public List<MaintenanceRequest> getAllRequests() {
        return repo.findAll();
    }
    
    @GetMapping("/unseen-count")
    public ResponseEntity<Long> getUnseenCount() {
        return ResponseEntity.ok(repo.countBySeenFalse());
    }

    @PutMapping("/mark-seen")
    public ResponseEntity<?> markAllAsSeen() {
        List<MaintenanceRequest> unseen = repo.findBySeenFalse();
        for (MaintenanceRequest req : unseen) {
            req.setSeen(true);
        }
        repo.saveAll(unseen);
        return ResponseEntity.ok("Marked all maintenance requests as seen");
    }
    
    @PutMapping("/update-status")
    public ResponseEntity<?> updateRequestStatus(@RequestParam Long id, @RequestParam String status) {
        MaintenanceRequest req = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Request not found"));

        req.setStatus(status);
        repo.save(req);

        return ResponseEntity.ok("Status updated to " + status);
    }


}
