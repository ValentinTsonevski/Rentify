package com.devminds.rentify.service;

import com.devminds.rentify.dto.CreateItemDto;
import com.devminds.rentify.dto.EditItemDto;
import com.devminds.rentify.dto.ItemDto;
import com.devminds.rentify.entity.Address;
import com.devminds.rentify.entity.Category;
import com.devminds.rentify.entity.Item;
import com.devminds.rentify.entity.Picture;
import com.devminds.rentify.entity.User;
import com.devminds.rentify.exception.ItemNotFoundException;
import com.devminds.rentify.repository.AddressRepository;
import com.devminds.rentify.repository.CategoryRepository;
import com.devminds.rentify.repository.ItemRepository;
import com.devminds.rentify.repository.PictureRepository;
import com.stripe.exception.StripeException;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private static final String ITEM_NOT_FOUND_MESSAGE = "Item with %d id not found.";
    private static final boolean IS_ITEM_ACTIVE_VALUE = true;
    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;
    private final StorageService storageService;
    private final PictureRepository pictureRepository;
    private final CategoryRepository categoryRepository;
    private final StripeService stripeService;



    public Item saveItem(CreateItemDto createItemDto,HttpServletRequest httpServletRequest) throws IOException, StripeException {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Address addressToAdd = new Address();
        addressToAdd.setCity(createItemDto.getCity());
        addressToAdd.setPostCode(createItemDto.getPostCode());
        addressToAdd.setStreet(createItemDto.getStreet());
        addressToAdd.setStreetNumber(createItemDto.getStreetNumber());
        this.addressRepository.save(addressToAdd);

        Category categoryToSave = this.categoryRepository.findByName(createItemDto.getCategory()).orElse(null);

        List<MultipartFile> pictureFiles = createItemDto.getPictures();
        List<URL> pictureUrls = storageService.uploadFiles(pictureFiles);

        Item itemToSave = this.modelMapper.map(createItemDto, Item.class);
        itemToSave.setAddress(addressToAdd);
        itemToSave.setUser(principal);
        itemToSave.setPostedDate(LocalDateTime.now());
        itemToSave.setCategory(categoryToSave);
        itemToSave.setIsActive(true);

        if (!pictureUrls.isEmpty()) {
            itemToSave.setThumbnail(pictureUrls.get(0).toString());
        }

        itemToSave.setPictures(new ArrayList<>());
        Item savedItem = this.itemRepository.save(itemToSave);
        List<Picture> picturesToAdd = new ArrayList<>();

        for (int i = 0; i < pictureUrls.size(); i++) {
            Picture picture = new Picture();
            picture.setUrl(pictureUrls.get(i).toString());
            picture.setItem(savedItem);
            picturesToAdd.add(this.pictureRepository.save(picture));
        }
        savedItem.setPictures(picturesToAdd);

        stripeService.createStripeAccount(httpServletRequest,savedItem.getUser().getId());
        stripeService.createProduct(savedItem,pictureUrls);

        return this.itemRepository.getReferenceById(savedItem.getId());
    }

    public Page<ItemDto> getAllActiveItems(Pageable pageable) {
        Page<Item> itemsPage = itemRepository.findByIsActive(IS_ITEM_ACTIVE_VALUE, pageable);
        return itemsPage.map(this::mapItemToItemDto);
    }

    public List<ItemDto> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(this::mapItemToItemDto)
                .toList();
    }

    public Item findById(Long itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        return itemOptional.orElse(null);
    }

    public ItemDto getItemById(Long id) {
        return itemRepository.findById(id)
                .map(this::mapItemToItemDto)
                .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, id)));
    }

    public Page<ItemDto> getItemsByCategoryId(Long id, Pageable pageable) {
        Page<Item> itemsPage = itemRepository.findByCategoryIdAndIsActive(id, IS_ITEM_ACTIVE_VALUE, pageable);
        return itemsPage.map(this::mapItemToItemDto);
    }

    public List<ItemDto> getPublishedItemsByUserId(Long userId) {
        return itemRepository.findByUserId(userId).stream()
                .map(this::mapItemToItemDto)
                .collect(Collectors.toList());
    }

    public Page<ItemDto> getFilteredItems(String categoryId, Float priceFrom, Float priceTo, String cityName,
                                          String searchTerm, Pageable pageable) {
        Specification<Item> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Long idOfCategory;

            try {
                idOfCategory = Long.parseLong(categoryId);

            } catch (NumberFormatException e) {
                idOfCategory = null;
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchTerm.toLowerCase() + "%"));
            }

            if (idOfCategory != null) {
                Optional<Category> optionalCategory = categoryRepository.findById(idOfCategory);
                optionalCategory.ifPresent(category -> predicates.add(cb.equal(root.get("category"), category)));
            }

            if ((priceFrom != null && priceFrom >= 0) || (priceTo != null && priceTo >= 0)) {
                if (priceFrom != null && priceTo != null && priceFrom >= 0 && priceTo >= 0) {
                    predicates.add(cb.between(root.get("price"), priceFrom, priceTo));
                } else if (priceFrom != null && priceFrom >= 0) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("price"), priceFrom));
                } else if (priceTo != null && priceTo >= 0) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("price"), priceTo));
                }
            }

            if (cityName != null && !cityName.isEmpty()) {
                List<Address> addresses = addressRepository.findByCity(cityName);
                if (!addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    predicates.add(cb.equal(root.get("address"), address));
                }
            }

            predicates.add(cb.isTrue(root.get("isActive")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Item> pageResult = itemRepository.findAll(spec, pageable);
        return pageResult.map(this::mapItemToItemDto);
    }

    @Transactional
    public ItemDto updateItem(Long id, EditItemDto editItemDto) throws IOException {
        itemExists(id);

        Address addressToAdd = new Address();
        addressToAdd.setCity(editItemDto.getCity());
        addressToAdd.setPostCode(editItemDto.getPostCode());
        addressToAdd.setStreet(editItemDto.getStreet());
        addressToAdd.setStreetNumber(editItemDto.getStreetNumber());
        this.addressRepository.save(addressToAdd);

        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Category categoryToSave = this.categoryRepository.findByName(editItemDto.getCategory()).orElse(null);

        Item itemToSave = this.modelMapper.map(editItemDto, Item.class);
        itemToSave.setId(id);
        itemToSave.setAddress(addressToAdd);
        itemToSave.setUser(principal);
        itemToSave.setPostedDate(LocalDateTime.now());
        itemToSave.setCategory(categoryToSave);
        itemToSave.setIsActive(true);
        itemToSave.setPictures(new ArrayList<>());
        Item savedItem = this.itemRepository.save(itemToSave);

        deletePictures(editItemDto.getDeletedPicturesOnEdit());

        List<MultipartFile> pictureFiles = new ArrayList<>();
        if (editItemDto.getPictures() != null) {
            pictureFiles = editItemDto.getPictures().stream().filter(Objects::nonNull).toList();
        }
        List<URL> pictureUrls = storageService.uploadFiles(pictureFiles);
        savePictures(id, pictureUrls, savedItem);

        return mapItemToItemDto(itemRepository.getReferenceById(savedItem.getId()));
    }

    public ItemDto changeStatusOfItem(Long id) {
        Item itemToUpdate = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, id)));

        itemToUpdate.setIsActive(!itemToUpdate.getIsActive());
        return mapItemToItemDto(itemRepository.save(itemToUpdate));
    }

    private void itemExists(Long id) {
        if (itemRepository.findById(id).isEmpty()) {
            throw new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, id));
        }
    }

    private ItemDto mapItemToItemDto(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }

    private Item mapItemDtoToItem(ItemDto itemDto) {
        return modelMapper.map(itemDto, Item.class);
    }

    private void deletePictures(String[] picturesToDelete) {
        for (String url : picturesToDelete) {
            pictureRepository.deleteByUrl(url);
        }
    }

    private void savePictures(Long id, List<URL> pictureUrls, Item savedItem) {
        List<Picture> picturesToAdd = new ArrayList<>();

        for (URL pictureUrl : pictureUrls) {
            Picture picture = new Picture();
            picture.setUrl(pictureUrl.toString());
            picture.setItem(savedItem);
            picturesToAdd.add(this.pictureRepository.save(picture));
        }
        savedItem.setPictures(picturesToAdd);

        savedItem.setThumbnail(!pictureRepository.findByItemId(id).isEmpty() ?
                pictureRepository.findByItemId(id).get(0).getUrl() : null);
    }
}
