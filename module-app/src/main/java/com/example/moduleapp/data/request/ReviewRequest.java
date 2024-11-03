package com.example.moduleapp.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    private Double rating;
    private String comment;
}
