package com.achintha.upload;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * @author Achintha Kalunayaka
 * @since 8/6/2024
 */

@Service
public class UploadService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public UploadService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateFilePathString() {
        final int MAX_LENGTH = 5;
        String randomString = "1239AMBcftA45678rgIoPL";
        StringBuilder generatedString = new StringBuilder();
        for(int i = 0; i < MAX_LENGTH; i++) {
            generatedString.append(randomString.charAt((int) (Math.random() * randomString.length())));
        }
        return generatedString.toString();
    }

    public void cloneRepo(String repoUrl) {
        String externalDirPath = "external-files"; // Change to your desired directory
        File externalDir = new File(externalDirPath + generateFilePathString());

        String localPath = externalDir.getPath();

        try {
            // Clone the repository
            Git git = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(new File(localPath))
                    .call();

            System.out.println("Repository cloned to: " + localPath);
            uploadFile(localPath);
        } catch (GitAPIException e) {
            e.printStackTrace();
            System.err.println("Error cloning repository: " + e.getMessage());
        }
    }

    public void uploadFile(String localPath)  {
        try {
        AwsBasicCredentials credentials = AwsBasicCredentials.create("905168f95f231a1b917dd98d68671cda", "d4d16126a9180c58e408370112a6a98d61c6daf36e87d6dd57b448df8384db77");

        S3Client s3 = S3Client.builder()
                .region(Region.of("auto")) // Set region to "auto"
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create("https://504a3c25d81ca77295bb657ca3a99697.r2.cloudflarestorage.com/deployment-portal"))
                .build();

        Path localRepoPath = Paths.get(localPath);

        Stream<Path> paths = Files.walk(localRepoPath);
        {
            paths.
                    filter(Files::isRegularFile).
                    filter(filePath -> (!filePath.toString().contains(".git"))).
                    forEach(filePath -> {
                        String key =  localRepoPath.relativize(filePath).toString().replace("\\", "/");

                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket("deployment-portal")
                        .key(key)
                        .build();
                PutObjectResponse putObjectResponse = s3.putObject(request, filePath);
                System.out.println(key);

            });
        }


        System.out.println("Object uploaded successfully");


        }
        catch (IOException e) {
            throw new RuntimeException("Failed to create output directory.");

        }

        }

        public void pushToQueue(String queueName, String message) {
            redisTemplate.opsForList().leftPush(queueName, message);
            System.out.println("Message pushed to queue: " + message);
        }

        public String popFromQueue(String queueName) {
            return (String) redisTemplate.opsForList().rightPop(queueName);
        }

}
