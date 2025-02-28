package com.xrm.vidapi.impl.core;

import jakarta.persistence.*;

@Entity
@Table(name = "video_requests")
public class VideoRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String video;
    private String finishCallbackUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getFinishCallbackUrl() {
        return finishCallbackUrl;
    }

    public void setFinishCallbackUrl(String finishCallbackUrl) {
        this.finishCallbackUrl = finishCallbackUrl;
    }
}