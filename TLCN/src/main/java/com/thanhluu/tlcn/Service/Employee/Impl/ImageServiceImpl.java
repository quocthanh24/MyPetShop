package com.thanhluu.tlcn.Service.Employee.Impl;

import com.thanhluu.tlcn.Service.Employee.IImageService;
import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class ImageServiceImpl implements IImageService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Override
    public String uploadImage(MultipartFile file, String folderName) {
        try {
            // Ensure bucket exists with public policy
            ensureBucketExists();

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".jpg";
            
            String fileName = folderName + "/" + UUID.randomUUID().toString() + extension;

            // Upload file to MinIO
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            // Generate public URL (not presigned)
            String url = buildPublicUrl(fileName);
            return url;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
    }

    /**
     * Build public URL for MinIO object
     * Format: http://endpoint/bucket-name/object-name
     */
    private String buildPublicUrl(String objectName) {
        // Remove leading http:// or https:// if present
        String cleanEndpoint = endpoint.replaceFirst("^https?://", "");
        return "http://" + cleanEndpoint + "/" + bucketName + "/" + objectName;
    }

    @Override
    public InputStream getImage(String imageUrl) {
        try {
            // Extract object name from URL
            String objectName = extractObjectNameFromUrl(imageUrl);
            
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to get image: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteImage(String imageUrl) {
        try {
            // Extract object name from URL
            String objectName = extractObjectNameFromUrl(imageUrl);
            
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
            return true;
        } catch (Exception e) {
            System.err.println("Failed to delete image: " + e.getMessage());
            return false;
        }
    }

    @Override
    public byte[] downloadImage(String imageUrl) {
        try {
            // Extract object name from URL
            String objectName = extractObjectNameFromUrl(imageUrl);

            InputStream inputStream = minioClient.getObject(
              GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build()
            );

            // Convert InputStream to byte array
            return inputStream.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to download image: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean imageExists(String imageUrl) {
        try {
            // Extract object name from URL
            String objectName = extractObjectNameFromUrl(imageUrl);
            
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean bucketExists = minioClient.bucketExists(
            BucketExistsArgs.builder()
                .bucket(bucketName)
                .build()
        );

        if (!bucketExists) {
            // Create bucket
            minioClient.makeBucket(
                MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build()
            );
            
            // Set bucket policy to public read
            setPublicBucketPolicy();
        }
    }

    /**
     * Set bucket policy to allow public read access
     */
    private void setPublicBucketPolicy() {
        try {
            // MinIO public read policy
            String policy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {
                        "AWS": ["*"]
                      },
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(bucketName);
            
            minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(policy)
                    .build()
            );
        } catch (Exception e) {
            System.err.println("Warning: Failed to set public bucket policy: " + e.getMessage());
        }
    }

    private String extractObjectNameFromUrl(String imageUrl) {
        // Extract object name from MinIO URL
        // URL format: http://localhost:9000/bucket-name/object-name (public URL)
        // URL format: http://localhost:9000/bucket-name/object-name?query=params (presigned URL)
        
        // Remove query parameters if present
        String urlWithoutQuery = imageUrl.split("\\?")[0];
        
        String[] parts = urlWithoutQuery.split("/");
        if (parts.length >= 2) {
            // Join all parts after bucket name
            StringBuilder objectName = new StringBuilder();
            boolean foundBucket = false;
            for (String part : parts) {
                if (foundBucket) {
                    if (objectName.length() > 0) {
                        objectName.append("/");
                    }
                    objectName.append(part);
                } else if (part.equals(bucketName)) {
                    foundBucket = true;
                }
            }
            return objectName.toString();
        }
        throw new IllegalArgumentException("Invalid image URL format: " + imageUrl);
    }
}
