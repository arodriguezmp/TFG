package com.DatuAtzipena2425Proiektua.app.domain;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Matxura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "auto_id")
    private Auto auto;


    private String deskribapena;
    private String fotoRuta;
    private String videoRuta;
}