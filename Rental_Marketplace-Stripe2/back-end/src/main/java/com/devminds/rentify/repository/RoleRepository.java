package com.devminds.rentify.repository;

import com.devminds.rentify.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT u FROM Role u WHERE u.id = 2")
    Role findUserRole();

}
