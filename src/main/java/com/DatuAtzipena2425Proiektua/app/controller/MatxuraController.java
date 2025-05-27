package com.DatuAtzipena2425Proiektua.app.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.DatuAtzipena2425Proiektua.app.domain.Auto;
import com.DatuAtzipena2425Proiektua.app.domain.Matxura;
import com.DatuAtzipena2425Proiektua.app.repository.AutoRepository;
import com.DatuAtzipena2425Proiektua.app.repository.MatxuraRepository;

@Controller
public class MatxuraController {

    private static final String MATXURA_UPLOAD_DIR = "src/main/resources/static/images/matxurak";

    @Autowired
    private MatxuraRepository matxuraRepository;

    @Autowired
    private AutoRepository autoRepository;

    @GetMapping("/matxurak")
    public String matxurak(Model model) {
        List<Matxura> allMatxurak = matxuraRepository.findAll();
        model.addAttribute("matxurak", allMatxurak);
        model.addAttribute("activePage", "matxurak");
        return "matxurak";
    }

    @GetMapping("/matxura/new")
    public String newMatxuraForm(Model model) {
        model.addAttribute("matxura", new Matxura());
        model.addAttribute("autos", autoRepository.findAll());
        return "matxuraNew";
    }

    @GetMapping("/matxura/{id}")
    public String mostrarMatxura(@PathVariable Long id, Model model) {
        Matxura matxura = matxuraRepository.findById(id).orElse(null);
        if (matxura != null) {
            model.addAttribute("matxura", matxura);
            return "matxuraDetail";
        }
        return "redirect:/matxurak";
    }

    @GetMapping("/matxura/edit/{id}")
    public String editMatxura(@PathVariable Long id, Model model) {
        Matxura matxura = matxuraRepository.findById(id).orElse(null);
        if (matxura != null) {
            model.addAttribute("matxura", matxura);
            return "matxuraEdit";
        }
        return "redirect:/matxurak";
    }

    @PostMapping("/matxura/new")
    public String createMatxura(@ModelAttribute Matxura matxura,
            @RequestParam("photoFile") MultipartFile photoFile,
            @RequestParam("autoId") Long autoId) {
        // Autoa esleitu
        Auto auto = autoRepository.findById(autoId).orElse(null);
        if (auto != null) {
            matxura.setAuto(auto);

            // Argazkia kudeatu da ematen bada
            if (!photoFile.isEmpty()) {
                String newPhotoPath = savePhoto(photoFile, MATXURA_UPLOAD_DIR, "/images/matxurak");
                matxura.setFotoRuta(newPhotoPath);
            }

            // Bideoa kudeatu ematen bada
            if (matxura.getVideoRuta() != null && !matxura.getVideoRuta().isEmpty()) {
                String videoId = extractYoutubeId(matxura.getVideoRuta());
                if (videoId != null && !videoId.isEmpty()) {
                    matxura.setVideoRuta(videoId);
                }
            }

            matxuraRepository.save(matxura);
        }
        return "redirect:/matxurak?t=" + System.currentTimeMillis();
    }

    @PostMapping("/matxura/update/{id}")
    public String updateMatxura(@PathVariable Long id,
            @ModelAttribute Matxura matxura,
            @RequestParam("photoFile") MultipartFile photoFile) {
        Matxura existingMatxura = matxuraRepository.findById(id).orElse(null);
        if (existingMatxura != null) {
            existingMatxura.setDeskribapena(matxura.getDeskribapena());

            if (!photoFile.isEmpty()) {
                deleteExistingPhoto(existingMatxura.getFotoRuta(), MATXURA_UPLOAD_DIR);
                String newPhotoPath = savePhoto(photoFile, MATXURA_UPLOAD_DIR, "/images/matxurak");
                existingMatxura.setFotoRuta(newPhotoPath);
            }

            if (matxura.getVideoRuta() != null && !matxura.getVideoRuta().isEmpty()) {
                String videoId = extractYoutubeId(matxura.getVideoRuta());
                if (videoId != null && !videoId.isEmpty()) {
                    existingMatxura.setVideoRuta(videoId);
                }
            }

            matxuraRepository.save(existingMatxura);
        }
        return "redirect:/matxurak";
    }

    @PostMapping("/matxura/delete/{id}")
    public String deleteMatxura(@PathVariable Long id) {
        Matxura matxura = matxuraRepository.findById(id).orElse(null);
        if (matxura != null) {
            // Argazkia ezabatu existitzen bada
            if (matxura.getFotoRuta() != null) {
                deleteExistingPhoto(matxura.getFotoRuta(), MATXURA_UPLOAD_DIR);
            }
            matxuraRepository.deleteById(id);
        }
        return "redirect:/matxurak";
    }

    private void deleteExistingPhoto(String photoPath, String baseDir) {
        if (photoPath != null && !photoPath.isEmpty()) {
            try {
                Path path = Paths.get("src/main/resources/static" + photoPath);
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String savePhoto(MultipartFile file, String uploadDir, String urlPath) {
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path uploadDirectory = Paths.get(uploadDir);
                if (!Files.exists(uploadDirectory)) {
                    Files.createDirectories(uploadDirectory);
                }
                Path filePath = uploadDirectory.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                return urlPath + "/" + fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String extractYoutubeId(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }

        String videoId = "";
        String cleanUrl = url.trim();

        if (cleanUrl.contains("youtu.be/")) {
            videoId = cleanUrl.substring(cleanUrl.lastIndexOf("/") + 1);
        } else if (cleanUrl.contains("watch?v=")) {
            videoId = cleanUrl.split("watch\\?v=")[1];
            int ampersandPosition = videoId.indexOf('&');
            if (ampersandPosition != -1) {
                videoId = videoId.substring(0, ampersandPosition);
            }
        }

        return videoId;
    }

}
