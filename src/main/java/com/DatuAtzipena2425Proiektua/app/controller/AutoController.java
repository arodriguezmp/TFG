package com.DatuAtzipena2425Proiektua.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.DatuAtzipena2425Proiektua.app.repository.AutoRepository;
import com.DatuAtzipena2425Proiektua.app.repository.MatxuraRepository;
import com.DatuAtzipena2425Proiektua.app.domain.Auto;
import com.DatuAtzipena2425Proiektua.app.domain.Matxura;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

@Controller
public class AutoController {
    
    @Autowired
    private AutoRepository autoRepository;
    
    @Autowired
    private MatxuraRepository matxuraRepository;
    
    @GetMapping("/")
    public String index(Model model) {
        List<Auto> allAutos = autoRepository.findAll();
        List<Auto> carouselAutos = getRandomAutos(allAutos, 3); // 3 autos para el carousel
        List<Auto> cardAutos = getRandomAutos(allAutos, 6); // 6 autos para las cards


        List<Matxura> allMatxuras = matxuraRepository.findAll();
        
        model.addAttribute("carouselAutos", carouselAutos);
        model.addAttribute("cardAutos", cardAutos);
        return "index";
    }

    @GetMapping("/auto/{id}")
    public String mostrarAuto(@PathVariable Long id, Model model) {
        Auto auto = autoRepository.findById(id).orElse(null);
        if (auto != null) {
            model.addAttribute("auto", auto);
            return "auto";
        }
        return "redirect:/";
    }
    

    
    private List<Auto> getRandomAutos(List<Auto> allAutos, int count) {
        List<Auto> result = new ArrayList<>();
        Random random = new Random();
        
        if (allAutos.size() <= count) {
            return allAutos;
        }
        
        while (result.size() < count) {
            int index = random.nextInt(allAutos.size());
            Auto auto = allAutos.get(index);
            if (!result.contains(auto)) {
                result.add(auto);
            }
        }
        
        return result;
    }
}