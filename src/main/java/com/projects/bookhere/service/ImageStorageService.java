package com.projects.bookhere.service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.projects.bookhere.exception.GCSUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/* Handle images upload to GCS */
@Service
public class ImageStorageService {
    @Value("${gcs.bucket}")  //Map to gcs.bucket in application.properties
    private String bucketName;

    //Upload and save images to GCS and return its ulr
    public String save(MultipartFile file) throws GCSUploadException {
        //Load credentials and connect to GCS
        Credentials credentials = null;
        try {
            credentials = GoogleCredentials.fromStream(getClass().getClassLoader().getResourceAsStream("credentials.json"));
        } catch (IOException e) {
            throw new GCSUploadException("Failed to load GCP credentials");
        }
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        //Upload input file as a jpeg image
        String filename = UUID.randomUUID().toString();  //Generate a random name
        BlobInfo blobInfo = null;
        try {
            blobInfo = storage.createFrom(
                    BlobInfo
                            .newBuilder(bucketName, filename)
                            .setContentType("image/jpeg")
                            .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                            .build(),
                    file.getInputStream());
        } catch (IOException e) {
            throw new GCSUploadException("Failed to upload images to GCS");
        }

        //Return image url
        return blobInfo.getMediaLink();
    }
}
