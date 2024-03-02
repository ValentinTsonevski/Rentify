package com.devminds.rentify.repository;


import com.devminds.rentify.entity.Item;
import com.devminds.rentify.entity.LikedItem;
import com.devminds.rentify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.devminds.rentify.entity.LikedItem;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository

public interface LikedItemRepository extends JpaRepository<LikedItem , Long> {

    List<LikedItem> findByItem(Item item);

    List<LikedItem> findByUser(User user);

    boolean existsByUserAndItem(User user, Item item);


    @Query("SELECT l FROM LikedItem l WHERE l.user = :user AND l.item = :item")
    LikedItem findByUserAndItem(@Param("user") User user, @Param("item") Item item);
    

    Set<LikedItem> findByUserId(Long userId);


}
