package com.devminds.rentify.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class StorageService {
    private static final String FOLDER_NAME = "Hidden_Resources/";

    @Value("${application.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;

    @Autowired
    public StorageService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public URL uploadFile(MultipartFile file) throws IOException {
        File fileObj = convertMultiPartFileToFile(file);
        String fileName = FOLDER_NAME + System.currentTimeMillis();
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        fileObj.delete();

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileName)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 3600000));

        return s3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }

    public List<URL> uploadFiles(List<MultipartFile> files) throws IOException {
        List<URL> preSignedUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            preSignedUrls.add(uploadFile(file));
        }

        return preSignedUrls;
    }

    public byte[] downloadFile(String fileName) throws IOException {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            throw new IOException("Cannot download the file.", e);
        }
    }

    public List<String> listObjectsInBucket() {
        List<String> result = new ArrayList<>();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucketName)
                .withPrefix(FOLDER_NAME);

        ObjectListing objectListing;

        do {
            objectListing = s3Client.listObjects(listObjectsRequest);
            for (S3ObjectSummary objectSummary :
                    objectListing.getObjectSummaries()) {
                result.add(objectSummary.getKey().substring(FOLDER_NAME.length()));
            }
            listObjectsRequest.setMarker(objectListing.getNextMarker());
        } while (objectListing.isTruncated());

        return result;
    }

    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, FOLDER_NAME + fileName);
        return fileName + " removed.";
    }

    private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new IOException("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }
}
