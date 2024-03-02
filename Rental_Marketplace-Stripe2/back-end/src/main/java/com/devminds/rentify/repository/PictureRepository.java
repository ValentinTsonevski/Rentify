package com.devminds.rentify.repository;

import com.devminds.rentify.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {
    List<Picture> findByItemId(Long id);

    void deleteByUrl(String url);
}
