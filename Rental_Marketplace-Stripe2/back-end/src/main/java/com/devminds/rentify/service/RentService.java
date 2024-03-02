package com.devminds.rentify.service;

import com.devminds.rentify.dto.RentDto;
import com.devminds.rentify.entity.Rent;
import com.devminds.rentify.exception.ItemNotFoundException;
import com.devminds.rentify.repository.ItemRepository;
import com.devminds.rentify.repository.RentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RentService {
    private static final String ITEM_NOT_FOUND_MESSAGE = "Item with %d id not found.";
    private final RentRepository rentRepository;
    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RentService(RentRepository rentRepository, ItemRepository itemRepository, ModelMapper modelMapper) {
        this.rentRepository = rentRepository;
        this.itemRepository = itemRepository;
        this.modelMapper = modelMapper;
    }

    public List<RentDto> getAllRentsByItemId(Long id) {
        if (itemRepository.findById(id).isEmpty()) {
            throw new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, id));
        }

        return rentRepository.findByItemId(id)
                .stream()
                .filter(rent -> rent.getEndDate().isAfter(LocalDate.now().minusDays(1)))
                .map(this::mapRentToRentDto)
                .toList();
    }

    public RentDto rentItem(Long id, RentDto rentDto) {
        if (itemRepository.findById(id).isEmpty()) {
            throw new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, id));
        }

        Rent rent = mapRentDtoToRent(rentDto);

        return mapRentToRentDto(rentRepository.save(rent));
    }

    private RentDto mapRentToRentDto(Rent rent) {
        return this.modelMapper.map(rent, RentDto.class);
    }

    private Rent mapRentDtoToRent(RentDto rentDto) {
        return this.modelMapper.map(rentDto, Rent.class);
    }
}