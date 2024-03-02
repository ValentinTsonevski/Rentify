package com.devminds.rentify.repository;

import com.devminds.rentify.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review , Long> {


    List<Review> findByItemId(Long itemId);

    boolean existsByUserIdAndItemId(Long userId, Long itemId);


    Review findByUserIdAndItemId(Long userId, Long itemId);
}
