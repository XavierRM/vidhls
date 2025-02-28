package com.xrm.vidapi.impl.db;

import com.xrm.vidapi.impl.core.VideoRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRequestRepository extends JpaRepository<VideoRequest, Long> {
}