package com.example.moduleapp.controller;

import com.example.common.data.request.PageRequest;
import com.example.common.data.response.ApiResponse;
import com.example.moduleapp.data.request.ReviewRequest;
import com.example.moduleapp.service.ReviewService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@Log4j2
public class ReviewController {
    private final ReviewService reviewService;

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse> update(@PathVariable Integer id, @RequestBody ReviewRequest request) {
        return reviewService.review(id, request).map(ApiResponse::success);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse> findNotReviewYet(PageRequest pageRequest, boolean isReview) {
        return reviewService.findReview(pageRequest,isReview).map(ApiResponse::success);
    }
}
