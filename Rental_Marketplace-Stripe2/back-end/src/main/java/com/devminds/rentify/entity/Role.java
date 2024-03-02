package com.devminds.rentify.entity;

import com.devminds.rentify.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@Entity
@Table(name = "user_role")

public class Role {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name")
    private UserRole role;


    @NotEmpty
    @Size(max = 255)
    @Column(name = "role_description")
    private String description;


    public Role() {
        this.role = UserRole.USER;

    }
}
