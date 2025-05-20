package com.DatuAtzipena2425Proiektua.app.repository;

import org.springframework.stereotype.Repository;

import com.DatuAtzipena2425Proiektua.app.domain.Auto;

@Repository
public interface AutoRepository {
    Iterable<Auto> saveAll(Iterable<Auto> entities);
}
