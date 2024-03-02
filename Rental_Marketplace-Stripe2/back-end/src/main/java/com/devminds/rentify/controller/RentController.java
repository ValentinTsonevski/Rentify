package com.devminds.rentify.controller;

import com.devminds.rentify.dto.RentDto;
import com.devminds.rentify.service.RentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rentify/rents")
public class RentController {

    private final RentService rentService;

    @Autowired
    public RentController(RentService rentService) {
        this.rentService = rentService;
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<List<RentDto>> getAllRentsByItemId(@PathVariable Long id) {
        return ResponseEntity.ok(rentService.getAllRentsByItemId(id));
    }

    @PostMapping("/item/{id}")
    public ResponseEntity<RentDto> rentItem(@PathVariable Long id, @RequestBody RentDto rentDto) {
        return ResponseEntity.ok(rentService.rentItem(id, rentDto));
    }
}