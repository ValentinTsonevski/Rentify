package com.devminds.rentify.service;

import com.devminds.rentify.dto.HistoryDto;
import com.devminds.rentify.entity.History;
import com.devminds.rentify.exception.EmailNotFoundException;
import com.devminds.rentify.repository.HistoryRepository;
import com.devminds.rentify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewsService {
    private static final String EMAIL_NOT_FOUND_MESSAGE = "Email %s was not found.";
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public List<HistoryDto> getAllViews() {
        return historyRepository.findAll()
                .stream().map(this::mapHistoryToHistoryDto)
                .toList();
    }

    public List<HistoryDto> getAllViewsByUserId(Long id) {
        return historyRepository.findByUserId(id)
                .stream().map(this::mapHistoryToHistoryDto)
                .toList();
    }

    public List<HistoryDto> getAllViewsByUserEmail(String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new EmailNotFoundException(String.format(EMAIL_NOT_FOUND_MESSAGE, email));
        }

        return historyRepository.findByUserEmail(email)
                .stream().map(this::mapHistoryToHistoryDto)
                .toList();
    }

    public List<HistoryDto> getAllViewsByItemId(Long id) {
        return historyRepository.findByItemId(id)
                .stream().map(this::mapHistoryToHistoryDto)
                .toList();
    }

    public HistoryDto addView(HistoryDto historyDto) {
        History history = mapHistoryDtoToHistory(historyDto);
        return mapHistoryToHistoryDto(historyRepository.save(history));
    }

    private HistoryDto mapHistoryToHistoryDto(History history) {
        return modelMapper.map(history, HistoryDto.class);
    }

    private History mapHistoryDtoToHistory(HistoryDto historyDto) {
        return modelMapper.map(historyDto, History.class);
    }

}
