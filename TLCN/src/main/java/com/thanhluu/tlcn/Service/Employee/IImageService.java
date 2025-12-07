package com.thanhluu.tlcn.Service.Employee;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface IImageService {
    
    /**
     * Upload image to MinIO and return the URL
     * @param file MultipartFile to upload
     * @param folderName Folder name in bucket (e.g., "products", "users")
     * @return URL of uploaded image
     */
    String uploadImage(MultipartFile file, String folderName);
    
    /**
     * Get image as InputStream from MinIO
     * @param imageUrl URL of the image
     * @return InputStream of the image
     */
    InputStream getImage(String imageUrl);

    /**
     * Delete image from MinIO
     * @param imageUrl URL of the image to delete
     * @return true if deleted successfully
     */
    boolean deleteImage(String imageUrl);

    byte[] downloadImage(String imageUrl);

    /**
     * Check if image exists in MinIO
     * @param imageUrl URL of the image
     * @return true if image exists
     */
    boolean imageExists(String imageUrl);
}
