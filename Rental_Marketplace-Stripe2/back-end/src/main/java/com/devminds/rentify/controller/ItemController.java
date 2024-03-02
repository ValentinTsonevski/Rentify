package com.devminds.rentify.controller;

import com.devminds.rentify.dto.CreateItemDto;
import com.devminds.rentify.dto.EditItemDto;
import com.devminds.rentify.dto.ItemDto;
import com.devminds.rentify.service.ItemService;
import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/rentify/items")
public class ItemController {
    private static final String DEFAULT_PAGE_SIZE = "2";
    private static final String DEFAULT_FIRST_PAGE_NUMBER = "0";
    private static final String DEFAULT_SORTING_ORDER = "asc";
    private static final String SORT_CRITERIA_PRICE = "price";
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/create")
    public void createItem(@ModelAttribute CreateItemDto createItemDto, HttpServletRequest httpServletRequest) throws IOException, StripeException {
        this.itemService.saveItem(createItemDto,httpServletRequest);
    }

    @GetMapping
    public ResponseEntity<Page<ItemDto>> getAllItems(
            @RequestParam(defaultValue = DEFAULT_FIRST_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORTING_ORDER) String sortDirection) {

        Sort.Direction sortOrder = sortDirection.equalsIgnoreCase(DEFAULT_SORTING_ORDER) ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Page<ItemDto> items = itemService.getAllActiveItems(
                PageRequest.of(page, size, Sort.by(sortOrder, SORT_CRITERIA_PRICE)));

        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @GetMapping("/filter/category/{id}")
    public ResponseEntity<Page<ItemDto>> getItemsByCategoryId(
            @PathVariable Long id,
            @RequestParam(required = false) Float priceFrom,
            @RequestParam(required = false) Float priceTo,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = DEFAULT_FIRST_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORTING_ORDER) String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase(DEFAULT_SORTING_ORDER) ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, SORT_CRITERIA_PRICE);
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(
                itemService.getFilteredItems(String.valueOf(id), priceFrom, priceTo, address, searchTerm, pageable));
    }

    @GetMapping("/user/published/{userId}")
    public ResponseEntity<List<ItemDto>> getPublishedItemsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(itemService.getPublishedItemsByUserId(userId));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ItemDto>> getFilteredItems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Float priceFrom,
            @RequestParam(required = false) Float priceTo,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = DEFAULT_FIRST_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORTING_ORDER) String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase(DEFAULT_SORTING_ORDER) ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, SORT_CRITERIA_PRICE);
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(
                itemService.getFilteredItems(category, priceFrom, priceTo, address, searchTerm, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long id,
                                              @ModelAttribute EditItemDto editItemDto) throws IOException {
        return ResponseEntity.ok(itemService.updateItem(id, editItemDto));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<ItemDto> changeStatusOfItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.changeStatusOfItem(id));
    }
}
