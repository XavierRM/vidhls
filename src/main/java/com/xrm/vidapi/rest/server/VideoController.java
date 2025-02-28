package com.xrm.vidapi.rest.server;

import com.xrm.vidapi.impl.core.VideoRequest;
import com.xrm.vidapi.impl.db.VideoRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VideoController {

    @Autowired
    private VideoRequestRepository repository;

    @GetMapping("/endpoint1")
    public List<VideoRequest> getAllVideos() {
        return repository.findAll();
    }

    @PostMapping("/endpoint2")
    public ResponseEntity<VideoRequest> createVideo(@RequestBody VideoRequest request) {
        VideoRequest savedRequest = repository.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRequest);
    }
}
