package com.devminds.rentify.service;

import com.devminds.rentify.config.JwtService;
import com.devminds.rentify.config.storage.StorageConfig;
import com.devminds.rentify.dto.AddressDto;
import com.devminds.rentify.dto.UpdatedUserInfoDto;
import com.devminds.rentify.dto.UserDto;
import com.devminds.rentify.entity.Address;
import com.devminds.rentify.entity.User;
import com.devminds.rentify.exception.AddressNotFoundException;
import com.devminds.rentify.exception.DuplicateEntityException;
import com.devminds.rentify.repository.AddressRepository;
import com.devminds.rentify.repository.RoleRepository;
import com.devminds.rentify.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND_MESSAGE = "User with %d id not found.";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AddressRepository addressRepository;

    private final StorageService storageService;

    @Value("${google-client-key}")
    private String googleClientId;

    @Override
    public User saveUser(User user) {

        checkIfIsDuplicateByEmail(user);

        checkIfIsDuplicateByPhoneNumber(user);


        return this.userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(long id) {

        return userRepository.findById(id);
    }

    private void checkIfIsDuplicateByPhoneNumber(User user) {
        userRepository.findByPhone(user.getPhoneNumber())
                .ifPresent(existingUser -> {
                    throw new DuplicateEntityException("User", "phoneNumber", user.getPhoneNumber());
                });
    }


    private void checkIfIsDuplicateByEmail(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(existingUser -> {
                    throw new DuplicateEntityException("User", "Email", user.getEmail());
                });

    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapUserToUserDto)
                .toList();
    }

    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapUserToUserDto)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, id)));
    }

    private UserDto mapUserToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }


    private GoogleIdToken isValidGoogleToken(String googleCredentials) throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(googleCredentials);
        return idToken;
    }

    public String mapGoogleTokenToOurToken(String googleCredentials) throws GeneralSecurityException, IOException {
        GoogleIdToken idToken = isValidGoogleToken(googleCredentials);
        if (idToken != null) {
            Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");
            String pictureUrl = (String) payload.get("picture");

            User user = new User();
            user.setEmail(email);
            user.setFirstName(givenName);
            user.setLastName(familyName);
            user.setProfilePicture(pictureUrl);
            user.setRole(this.roleRepository.findUserRole());


            if (this.userRepository.findByEmail(email).isEmpty()) {
                User googleUser = this.userRepository.saveAndFlush(user);
                user.setId(googleUser.getId());
            }
            User existingUser = this.userRepository.findByEmail(email).get();
            user.setId(existingUser.getId());

            return this.jwtService.generateToken(user);

        }
        return null;
    }

    public UserDto updateUserInfo(Long id, UpdatedUserInfoDto updatedUserInfoDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        AddressDto addressDto = updatedUserInfoDto.getAddressDto();

        if (updatedUserInfoDto.getFirstName() != null) {
            existingUser.setFirstName(updatedUserInfoDto.getFirstName());
        }

        if (updatedUserInfoDto.getLastName() != null) {
            existingUser.setLastName(updatedUserInfoDto.getLastName());
        }

        if (updatedUserInfoDto.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updatedUserInfoDto.getPhoneNumber());
        }
        if (updatedUserInfoDto.getIban() != null) {
            existingUser.setIban(updatedUserInfoDto.getIban());
        }



        if (addressDto.getCity() != null && !addressDto.getCity().isEmpty() &&
                addressDto.getStreet() != null && !addressDto.getStreet().isEmpty() &&
                addressDto.getStreetNumber() != null && !addressDto.getStreetNumber().isEmpty() &&
                addressDto.getPostCode() != null && !addressDto.getPostCode().isEmpty()) {
            Address address = existingUser.getAddress();

            if (address == null) {
                address = new Address();
            }

            address.setCity(addressDto.getCity());
            address.setPostCode(addressDto.getPostCode());
            address.setStreet(addressDto.getStreet());
            address.setStreetNumber(addressDto.getStreetNumber());
            addressRepository.save(address);
            existingUser.setAddress(address);
        }

        userRepository.save(existingUser);
        return mapUserToUserDto(existingUser);
    }

    private boolean isAnyAddressFieldProvided(AddressDto addressDto) {
        return addressDto.getCity() != null || addressDto.getStreet() != null ||
                addressDto.getPostCode() != null || addressDto.getStreetNumber() != null;
    }


    public UserDto updateProfilePicture(Long userId, MultipartFile file) throws IOException {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        URL pictureUrls = storageService.uploadFile(file);

        existingUser.setProfilePicture(pictureUrls.toString());

        userRepository.save(existingUser);

        return mapUserToUserDto(existingUser);
    }
}

