package com.DatuAtzipena2425Proiektua.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.DatuAtzipena2425Proiektua.app.domain.Matxura;

@Repository
public interface MatxuraRepository extends JpaRepository<Matxura, Long>{
    List<Matxura> findAll();
}
