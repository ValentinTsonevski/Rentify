package com.devminds.rentify.repository;

import com.devminds.rentify.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {
    List<Item> findByCategoryId(Long id);

    List<Item> findByUserId(Long userId);

    Page<Item> findByCategoryId(Long id, Pageable pageable);

    Page<Item> findByIsActive(Boolean status, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE i.category.id = :categoryId AND i.isActive = :isActive")
    Page<Item> findByCategoryIdAndIsActive(Long categoryId, Boolean isActive, Pageable pageable);
}
