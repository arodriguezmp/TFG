package com.DatuAtzipena2425Proiektua.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.DatuAtzipena2425Proiektua.app.domain.Auto;

@Repository
public interface AutoRepository extends JpaRepository<Auto, Long> {
    List<Auto> findAll();
    @Query("SELECT a FROM Auto a WHERE LOWER(CONCAT(a.marka, ' ', a.modeloa)) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Auto> findByFullName(String search);
}
