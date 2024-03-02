package com.devminds.rentify.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "review")
public class Review {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;


    @Positive
    @Column(name = "rating")
    private int rating;


    @Size(max = 255)
    @Column(name = "comment")
    private String comments;

  @ManyToOne(fetch = FetchType.EAGER)
    private Item item;


    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
}
