package com.DatuAtzipena2425Proiektua.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.DatuAtzipena2425Proiektua.app.domain.Matxura;
import com.DatuAtzipena2425Proiektua.app.repository.AutoRepository;
import com.DatuAtzipena2425Proiektua.app.repository.MatxuraRepository;
@Controller
public class MatxuraController {
    
    @Autowired
    private MatxuraRepository matxuraRepository;
    
    @GetMapping("/matxurak")
    public String matxurak(Model model) {
        List<Matxura> allMatxurak = matxuraRepository.findAll();
        model.addAttribute("matxurak", allMatxurak);
        model.addAttribute("activePage", "matxurak");
        return "matxurak";
    }
}
