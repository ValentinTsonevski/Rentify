package com.devminds.rentify.controller;


import com.devminds.rentify.dto.ReviewDto;
import com.devminds.rentify.dto.UserReviewDto;
import com.devminds.rentify.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rentify/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/addReview/{userId}/{itemId}")
    public ResponseEntity<ReviewDto> addReview(@PathVariable Long userId, @PathVariable Long itemId,
                                               @RequestBody ReviewDto reviewDto) {

        return new ResponseEntity<>(reviewService.addReview(userId, itemId, reviewDto), HttpStatus.OK);
    }


    @GetMapping("/rating/{itemId}")
    public ResponseEntity<Double> addReview(@PathVariable Long itemId) {

        return new ResponseEntity<>( reviewService.getRating(itemId), HttpStatus.OK);

    }

    @GetMapping("/{itemId}")
    public ResponseEntity<List<UserReviewDto>> getReview(@PathVariable Long itemId) {

        return new ResponseEntity<>( reviewService.getReviews(itemId), HttpStatus.OK);

    }


    @GetMapping("/{userId}/{itemId}")
    public ResponseEntity<Boolean> getReviewsUser(@PathVariable Long userId , @PathVariable Long itemId) {

        return new ResponseEntity<>( reviewService.hasUserReviewedItem(userId, itemId ), HttpStatus.OK);

    }

    @GetMapping("/userReview/{userId}/{itemId}")
    public ResponseEntity<UserReviewDto> getUserReview(@PathVariable Long userId , @PathVariable Long itemId) {

        return new ResponseEntity<>( reviewService.getUserReview(userId, itemId ), HttpStatus.OK);

    }

    @PutMapping("/updateReview/{userId}/{itemId}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Long userId , @PathVariable Long itemId
            , @RequestBody ReviewDto reviewDto) {

        return new ResponseEntity<>( reviewService.updateReview(userId, itemId, reviewDto ), HttpStatus.OK);

    }
}
