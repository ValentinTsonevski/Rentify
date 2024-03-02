package com.devminds.rentify.dto;

import com.devminds.rentify.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateItemDto {

    private String name;

    private String description;

    private BigDecimal price;

    private LocalDateTime postedDate;

    private float deposit;

    private String category;

    private User user;

    private List<MultipartFile> pictures;

    private String city;
    private String street;
    private String postCode;
    private String streetNumber;

}
