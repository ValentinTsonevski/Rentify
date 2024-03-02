package com.devminds.rentify.service;

import com.devminds.rentify.dto.AddressDto;
import com.devminds.rentify.entity.Address;
import com.devminds.rentify.exception.AddressNotFoundException;
import com.devminds.rentify.repository.AddressRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    private static final String ADDRESS_NOT_FOUND_MESSAGE = "Category with %d id not found.";
    private final AddressRepository addressRepository;

    private final ModelMapper modelMapper;


    @Autowired
    public AddressService(AddressRepository addressRepository, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    public List<AddressDto> getAllAddresses() {
        return addressRepository.findAll()
                .stream()
                .map(this::mapAddressToAddressDto)
                .toList();
    }

    public Address getAddressById(long id) {
        return addressRepository.findById(id).orElseThrow(() -> new AddressNotFoundException(String.format(ADDRESS_NOT_FOUND_MESSAGE, id)));
    }

    public List<Address> getAddressesByCity(String city) {
        return addressRepository.findByCity(city);
    }

    public List<Address> getAddressesByPostCode(String postalCode) {
        return addressRepository.findByPostCode(postalCode);
    }

    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }

    public void deleteAddressById(long id) {
        addressRepository.deleteById(id);
    }

    private AddressDto mapAddressToAddressDto(Address address) {
        return modelMapper.map(address, AddressDto.class);
    }
}
