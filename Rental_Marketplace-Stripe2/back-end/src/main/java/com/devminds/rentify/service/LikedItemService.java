package com.devminds.rentify.service;


import com.devminds.rentify.entity.Item;
import com.devminds.rentify.entity.LikedItem;
import com.devminds.rentify.entity.User;
import com.devminds.rentify.repository.LikedItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Set;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikedItemService {

  private final  LikedItemRepository likedItemRepository;


  private final ItemService itemService;

    public void saveLike(User user, Item item) {
        LikedItem existingLike = likedItemRepository.findByUserAndItem(user, item);

        if (existingLike == null) {
            LikedItem likedItem = new LikedItem();
            likedItem.setUser(user);
            likedItem.setItem(item);
            likedItemRepository.save(likedItem);
        }
    }



    public List<Item> getLikedItemsByUser(User user) {
        List<LikedItem> likedItems = likedItemRepository.findByUser(user);
        return likedItems.stream()
                .map(LikedItem::getItem)
                .collect(Collectors.toList());
    }

    public List<User> getUsersWhoLikedItem(Item item) {
        List<LikedItem> likedItems = likedItemRepository.findByItem(item);
        return likedItems.stream()
                .map(LikedItem::getUser)
                .collect(Collectors.toList());
    }

    public boolean hasUserLikedItem(User user, Item item) {
        return likedItemRepository.existsByUserAndItem(user, item);
    }

    public void unlikeItem(User user, Item item) {
        LikedItem likedItemToDelete = likedItemRepository.findByUserAndItem(user,item);
        if (likedItemToDelete !=null) {
            likedItemRepository.delete(likedItemToDelete);
        }

    }

    public Set<Long> getLikedItemsByUserId(Long userId) {
        Set<LikedItem> likedItems = likedItemRepository.findByUserId(userId);
        return likedItems.stream().map(LikedItem::getItemId).collect(Collectors.toSet());

    }

}




