package com.achintha.upload;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author Achintha Kalunayaka
 * @since 8/6/2024
 */

@Service
public class UploadService {

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
        String localPath = "C:\\Users\\Hp\\IdeaProjects\\deployment-portal\\upload-service\\src\\main\\resources\\" + generateFilePathString();

        try {
            // Clone the repository
            Git git = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(new File(localPath))
                    .call();

            System.out.println("Repository cloned to: " + localPath);
        } catch (GitAPIException e) {
            e.printStackTrace();
            System.err.println("Error cloning repository: " + e.getMessage());
        }
    }
}
