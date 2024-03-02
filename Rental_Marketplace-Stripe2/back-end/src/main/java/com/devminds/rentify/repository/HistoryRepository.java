package com.devminds.rentify.repository;

import com.devminds.rentify.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByUserId(Long id);

    List<History> findByItemId(Long id);

    List<History> findByUserEmail(String email);
}
