package com.smstar.storage;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Slf4j
@Service
public class OracleStorageService {

    @Value("${oracle.oci.config-file}")
    private String ociConfigFile;

    @Value("${oracle.oci.profile}")
    private String ociProfile;

    @Value("${oracle.oci.namespace}")
    private String namespace;

    @Value("${oracle.oci.bucket-name}")
    private String bucketName;

    private ObjectStorage client;

    @PostConstruct
    public void init() {
        try {
            ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(ociConfigFile, ociProfile);
            ConfigFileAuthenticationDetailsProvider provider =
                    new ConfigFileAuthenticationDetailsProvider(configFile);
            client = ObjectStorageClient.builder().build(provider);
            log.info("Oracle OCI Object Storage client initialized");
        } catch (IOException e) {
            log.warn("Oracle OCI config not found — storage will be unavailable: {}", e.getMessage());
        }
    }

    /**
     * Uploads a file to Oracle Bucket and returns the object name (path).
     */
    public String uploadFile(String objectName, InputStream data, String contentType) {
        if (client == null) {
            log.warn("Oracle client not initialized. Returning mock URL for: {}", objectName);
            return objectName;
        }
        PutObjectRequest request = PutObjectRequest.builder()
                .namespaceName(namespace)
                .bucketName(bucketName)
                .objectName(objectName)
                .putObjectBody(data)
                .contentType(contentType)
                .build();
        client.putObject(request);
        log.info("Uploaded file to Oracle Bucket: {}", objectName);
        return objectName;
    }

    /**
     * Generates a pre-authenticated (time-limited) URL for streaming/download.
     */
    public String getPreAuthUrl(String objectName) {
        if (client == null) {
            return "/placeholder-stream/" + objectName;
        }
        // Pre-auth URL valid for 2 hours
        Date expiry = new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000L);

        CreatePreauthenticatedRequestDetails details = CreatePreauthenticatedRequestDetails.builder()
                .name("stream-" + System.currentTimeMillis())
                .objectName(objectName)
                .accessType(CreatePreauthenticatedRequestDetails.AccessType.ObjectRead)
                .timeExpires(expiry)
                .build();

        CreatePreauthenticatedRequestRequest req = CreatePreauthenticatedRequestRequest.builder()
                .namespaceName(namespace)
                .bucketName(bucketName)
                .createPreauthenticatedRequestDetails(details)
                .build();

        CreatePreauthenticatedRequestResponse response = client.createPreauthenticatedRequest(req);
        return response.getPreauthenticatedRequest().getFullPath();
    }

    /**
     * Deletes an object from Oracle Bucket.
     */
    public void deleteFile(String objectName) {
        if (client == null) {
            log.warn("Oracle client not initialized. Skip delete: {}", objectName);
            return;
        }
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .namespaceName(namespace)
                .bucketName(bucketName)
                .objectName(objectName)
                .build();
        client.deleteObject(request);
        log.info("Deleted file from Oracle Bucket: {}", objectName);
    }
}
