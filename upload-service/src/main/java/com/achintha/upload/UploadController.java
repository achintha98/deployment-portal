package com.achintha.upload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/customers")
public class UploadController {

    private final UploadService uploadService;

    @Autowired
    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @GetMapping("/push/{message}")
    public String pushToQueue(@PathVariable("message") String message) {
        uploadService.pushToQueue("myQueue", message);
        return "Message pushed: " + message;
    }

    @GetMapping("/pop")
    public String popFromQueue() {
        String message = uploadService.popFromQueue("myQueue");
        return "Message popped: " + (message != null ? message : "Queue is empty");
    }


    @PostMapping
    public ResponseEntity<String> deployment(@RequestBody DeploymentRequest deploymentRequest) {
        log.info("New Deployment Request {}", deploymentRequest.getUrl());

        // Create a JSON response
        String jsonResponse = "deploymentRequest";

        log.info("Generated String {}", uploadService.generateFilePathString());
//        uploadService.cloneRepo(deploymentRequest.getUrl());
        uploadService.uploadFile("external-files27c4B");

        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

}
