package com.devminds.rentify.dto;

import com.devminds.rentify.entity.Address;
import com.devminds.rentify.entity.Role;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlainUserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String phoneNumber;
    private String profilePicture;
    private List<Address> addresses;
    private Role role;
}
