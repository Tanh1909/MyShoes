package com.example.moduleapp.data.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReviewRequest {
    private Double rating;
    private String comment;
    private List<Integer> imageIds = new ArrayList<>();
}
