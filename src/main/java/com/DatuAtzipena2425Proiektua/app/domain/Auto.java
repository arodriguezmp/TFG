package com.DatuAtzipena2425Proiektua.app.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String marka;
    private String modeloa;
    private String autoFoto; 

    @OneToMany(mappedBy = "auto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Matxura> matxurak;
}