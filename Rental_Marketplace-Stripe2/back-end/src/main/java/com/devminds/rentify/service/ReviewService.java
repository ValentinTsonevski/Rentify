package com.devminds.rentify.service;

import com.devminds.rentify.dto.ReviewDto;
import com.devminds.rentify.dto.UserReviewDto;
import com.devminds.rentify.entity.Item;
import com.devminds.rentify.entity.Review;
import com.devminds.rentify.entity.User;
import com.devminds.rentify.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final ItemService itemService;

    public ReviewDto addReview(Long userId, Long itemId, ReviewDto reviewDto) {

        Review review = new Review();
        try {

            User existingUser = userService.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

            Item itemToReview = itemService.findById(itemId);

            review.setRating(reviewDto.getRatingStars());
            review.setComments(reviewDto.getComments());
            review.setUser(existingUser);
            review.setItem(itemToReview);

            reviewRepository.save(review);

        } catch (EntityNotFoundException e) {

        }
        return mapReviewToReviewDto(review);

    }


    private ReviewDto mapReviewToReviewDto(Review review) {
        return modelMapper.map(review, ReviewDto.class);
    }

    private Review mapReviewDtoToIReview(ReviewDto reviewDto) {
        return modelMapper.map(reviewDto, Review.class);
    }

    public Double getRating(Long itemId) {
        double sum = 0;
        List<Review> reviews = reviewRepository.findByItemId(itemId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        for (Review review : reviews) {
            sum += review.getRating();
        }

        double average = sum / reviews.size();

        return Math.round(average * 10.0) / 10.0;
    }


    public List<UserReviewDto> getReviews(Long itemId) {
        List<Review> reviews = reviewRepository.findByItemId(itemId);
        return reviews.stream()
                .filter(review -> review.getComments() != null)
                .map(review -> UserReviewDto.builder()
                        .firstName(review.getUser().getFirstName())
                        .lastName(review.getUser().getLastName())
                        .stars(review.getRating())
                        .comment(review.getComments())
                        .profilePicture(review.getUser().getProfilePicture())
                        .build())
                .collect(Collectors.toList());
    }

    public boolean hasUserReviewedItem(Long userId, Long itemId) {
        return reviewRepository.existsByUserIdAndItemId(userId, itemId);
    }


    public UserReviewDto getUserReview(Long userId, Long itemId) {
        Review review = reviewRepository.findByUserIdAndItemId(userId, itemId);
        if (review == null) {
            return null;
        }

        return UserReviewDto.builder()
                .firstName(review.getUser().getFirstName())
                .lastName(review.getUser().getLastName())
                .stars(review.getRating())
                .comment(review.getComments())
                .profilePicture(review.getUser().getProfilePicture())
                .build();
    }

    public ReviewDto updateReview(Long userId, Long itemId, ReviewDto reviewDto) {
            Review existingReview = reviewRepository.findByUserIdAndItemId(userId, itemId);

            if(existingReview != null){
                existingReview.setRating(reviewDto.getRatingStars());
                existingReview.setComments(reviewDto.getComments());

                reviewRepository.save(existingReview);
            }

            return reviewDto;
    }
}

