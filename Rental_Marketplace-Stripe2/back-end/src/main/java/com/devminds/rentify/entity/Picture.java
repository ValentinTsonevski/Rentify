package com.devminds.rentify.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "picture")
public class Picture {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;


    @NotEmpty
    @Size(max = 512)
    @Column(name = "url")
    private String url;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + url + '\'' ;
    }

}
