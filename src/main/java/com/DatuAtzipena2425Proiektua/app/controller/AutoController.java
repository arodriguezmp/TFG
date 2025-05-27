package com.DatuAtzipena2425Proiektua.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.DatuAtzipena2425Proiektua.app.repository.AutoRepository;
import com.DatuAtzipena2425Proiektua.app.repository.MatxuraRepository;
import com.DatuAtzipena2425Proiektua.app.domain.Auto;
import com.DatuAtzipena2425Proiektua.app.domain.Matxura;

import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

@Controller
public class AutoController {

    private static final String AUTO_UPLOAD_DIR = "src/main/resources/static/images/autoak";

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
        model.addAttribute("activePage", "home");
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

    @GetMapping("/auto/edit/{id}")
    public String editAuto(@PathVariable Long id, Model model) {
        Auto auto = autoRepository.findById(id).orElse(null);
        if (auto != null) {
            model.addAttribute("auto", auto);
            return "autoEdit";
        }
        return "redirect:/autoak";
    }

    @PostMapping("/auto/update/{id}")
    public String updateAuto(@PathVariable Long id,
            @ModelAttribute Auto auto,
            @RequestParam("photoFile") MultipartFile photoFile) {
        Auto existingAuto = autoRepository.findById(id).orElse(null);
        if (existingAuto != null) {
            existingAuto.setMarka(auto.getMarka());
            existingAuto.setModeloa(auto.getModeloa());

            if (!photoFile.isEmpty()) {
                deleteExistingPhoto(existingAuto.getAutoFoto());
                String newPhotoPath = savePhoto(photoFile);
                existingAuto.setAutoFoto(newPhotoPath);
            }

            autoRepository.save(existingAuto);
        }
        return "redirect:/autoak?t=" + System.currentTimeMillis();
    }

    @GetMapping("/autoak")
    public String autos(Model model, @RequestParam(required = false) String search) {
        List<Auto> autos;
        if (search != null && !search.isEmpty()) {
            autos = autoRepository.findByFullName(search);
        } else {
            autos = autoRepository.findAll();
        }
        model.addAttribute("autos", autos);
        model.addAttribute("search", search);
        model.addAttribute("activePage", "autoak");
        return "autos";
    }

    @PostMapping("/auto/delete/{id}")
    public String deleteAuto(@PathVariable Long id) {
        Auto auto = autoRepository.findById(id).orElse(null);
        if (auto != null) {
            // Eliminar la foto si existe
            if (auto.getAutoFoto() != null) {
                deleteExistingPhoto(auto.getAutoFoto());
            }
            // Las matxuras asociadas se eliminarán automáticamente por la relación cascade
            autoRepository.deleteById(id);
        }
        return "redirect:/autoak";
    }

    @GetMapping("/auto/new")
    public String newAutoForm(Model model) {
        model.addAttribute("auto", new Auto());
        return "autoNew";
    }

    @PostMapping("/auto/new")
    public String createAuto(@ModelAttribute Auto auto,
            @RequestParam("photoFile") MultipartFile photoFile) {
        if (!photoFile.isEmpty()) {
            String newPhotoPath = savePhoto(photoFile);
            auto.setAutoFoto(newPhotoPath);
        }

        autoRepository.save(auto);
        return "redirect:/autoak?t=" + System.currentTimeMillis();
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

    private void deleteExistingPhoto(String photoPath) {
        if (photoPath != null && !photoPath.isEmpty()) {
            try {
                Path path = Paths.get("src/main/resources/static" + photoPath);
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String savePhoto(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path uploadDir = Paths.get(AUTO_UPLOAD_DIR);
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                Path filePath = uploadDir.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                return "/images/autoak/" + fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}