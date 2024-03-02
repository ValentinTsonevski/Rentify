package com.devminds.rentify.controller;

import com.devminds.rentify.dto.PictureDto;
import com.devminds.rentify.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/rentify/pictures")
public class PictureController {
    private final PictureService pictureService;

    @Autowired
    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    @GetMapping
    public ResponseEntity<List<PictureDto>> getAllPictures() {
        return ResponseEntity.ok(pictureService.getAllPictures());
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<List<PictureDto>> getAllPicturesByItemId(@PathVariable Long id) {
        return ResponseEntity.ok(pictureService.getPicturesByItemId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PictureDto> getPictureById(@PathVariable Long id) {
        return ResponseEntity.ok(pictureService.getPictureById(id));
    }

//    @GetMapping("/thumbnails")
//    public ResponseEntity<List<PictureDto>> getThumbnails() {
//        return ResponseEntity.ok(pictureService.getThumbnails());
//    }
}
