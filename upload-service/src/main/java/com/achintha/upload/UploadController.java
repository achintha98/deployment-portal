package com.achintha.upload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/customers")
public class UploadController {

    private final UploadService uploadService;

    @Autowired
    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping
    public ResponseEntity<String> deployment(@RequestBody DeploymentRequest deploymentRequest) {
        log.info("New Deployment Request {}", deploymentRequest.getUrl());

        // Create a JSON response
        String jsonResponse = "deploymentRequest";

        log.info("Generated String {}", uploadService.generateFilePathString());
        uploadService.cloneRepo(deploymentRequest.getUrl());

        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

}
