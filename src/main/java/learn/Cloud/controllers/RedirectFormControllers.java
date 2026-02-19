package learn.Cloud.controllers;//package com.weather.web.project.weather.controllers;

import learn.Cloud.model.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class RedirectFormControllers {

    @GetMapping("/")
    public String index() {
        log.info("enter into the programm");
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        log.info("enter reg form");
        return "registration";

    }

    @GetMapping("/autentificate")
    public String showAuthForm() {
        log.info("enter auth form");
        return "auth";

    }
    @GetMapping("/main-page")
    public String mainPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        log.info("=== ЗАГРУЗКА MAIN-PAGE ===");

        // ✅ СОЗДАЕМ ТЕСТОВОГО ПОЛЬЗОВАТЕЛЯ
        UserDto testUser = new UserDto();
        testUser.setFirstName("string");
        testUser.setLastName("string");
        testUser.setEmail("string");

        // ✅ ДОБАВЛЯЕМ В МОДЕЛЬ
        model.addAttribute("userDto", testUser);
        model.addAttribute("locationCount", 0);
        return "main-page";

    }
}
