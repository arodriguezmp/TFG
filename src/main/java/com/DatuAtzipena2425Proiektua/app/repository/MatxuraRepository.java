package com.DatuAtzipena2425Proiektua.app.repository;

import org.springframework.stereotype.Repository;

import com.DatuAtzipena2425Proiektua.app.domain.Matxura;

@Repository
public interface MatxuraRepository {
    Iterable<Matxura> saveAll(Iterable<Matxura> entities);
}
