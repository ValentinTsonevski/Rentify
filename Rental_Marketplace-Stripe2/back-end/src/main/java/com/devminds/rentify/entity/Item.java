package com.devminds.rentify.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "item")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

public class Item {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @NotEmpty
    @Size(max = 1024)
    @Column(name = "description")
    private String description;

    @NotNull
    @Positive
    @Column(name = "price")
    private BigDecimal price;

    @NotNull
    @Column(name = "posted_date")
    private LocalDateTime postedDate;

    @Positive
    @NotNull
    @Column(name = "deposit")
    private float deposit;

    @Column(name = "item_stripe_id")
    private String itemStripeId;

    @ManyToOne
    private Category category;

    private String thumbnail;

    private Boolean isActive;

    @ManyToOne
    @JsonIgnore
    private User user;

    @JsonIgnore
    @ManyToOne
    private Address address;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    private List<History> histories;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")

    private List<Picture> pictures;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    private List<LikedItem> likedItems;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    private List<Rent> rents;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    private List<Review> reviews;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + name + '\'' ;
    }
}
