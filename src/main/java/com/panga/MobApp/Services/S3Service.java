package com.panga.MobApp.Services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.panga.MobApp.Config.S3Properties;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private S3Properties props;

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String key = folder + "/" + filename;

        // ✅ Resize image to max 800x800 using Thumbnailator
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        if (originalImage != null) {
            Thumbnails.of(originalImage)
                .size(800, 800)
                .outputFormat("jpg")
                .outputQuality(0.85)
                .toOutputStream(outputStream);
        }

        byte[] resizedBytes = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(resizedBytes);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(resizedBytes.length);
        metadata.setContentType("image/jpeg");

        // ✅ Use bucket name from properties
        String bucketName = props.getS3().getBucketName();

        s3Client.putObject(new PutObjectRequest(bucketName, key, inputStream, metadata));

        return s3Client.getUrl(bucketName, key).toString();
    }


}
