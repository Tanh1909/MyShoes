package com.example.moduleapp.data.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageResponse {
    private Integer id;
    private String url;
    private boolean isPrimary;

    public boolean getIsPrimary() {
        return isPrimary;
    }
}
