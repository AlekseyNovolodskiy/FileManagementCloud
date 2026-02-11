package learn.Cloud.controllers;//package com.weather.web.project.weather.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
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
    public String mainPage() {
        log.info("enter main page");
        return "main-page";

    }
}
