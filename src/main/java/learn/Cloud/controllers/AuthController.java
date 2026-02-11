package learn.Cloud.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import learn.Cloud.model.responce.AuthenticationRequest;
import learn.Cloud.model.responce.RegisterRequest;
import learn.Cloud.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@Tag(name = "Контролер для аутентификации")
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Аутентификация пользователя")
    @PostMapping("/authenticate")
    public String authenticate(
            @RequestParam String username,
            @RequestParam String password) {

        try {
            authService.authUser(username,password);
            return "redirect:/main-page";
        } catch (Exception e) {
            log.error("Ошибка аутентификации: {}", e.getMessage());
            return "exception";
        }
    }

    @Operation(summary = "Регистрация нового пользователя")
    @PostMapping("/register")
    public String register(
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String email,
            @RequestParam String password) {

        try {
            RegisterRequest registerRequest = new RegisterRequest(firstname, lastname, email, password);
            authService.registrationNewUser(registerRequest);
            return "redirect:/autentificate?registered=true";
        } catch (Exception e) {
            log.error("Ошибка регистрации: {}", e.getMessage());
            return "exception";
        }
    }
}