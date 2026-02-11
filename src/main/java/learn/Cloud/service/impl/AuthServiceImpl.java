package learn.Cloud.service.impl;

import learn.Cloud.entity.UserEntityInfo;
import learn.Cloud.model.UserRole;
import learn.Cloud.model.responce.AuthenticationRequest;
import learn.Cloud.model.responce.AuthenticationResponce;
import learn.Cloud.model.responce.RegisterRequest;
import learn.Cloud.repository.UserRepository;
import learn.Cloud.security.JwtService;
import learn.Cloud.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Lazy
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    public static final String WRONG_PARAMETERS = "Пароль или логин не верны";
    public static final String USER_EXIST = "Данный пользователь уже существует";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Override
    public AuthenticationResponce registrationNewUser(RegisterRequest request) {



        UserEntityInfo user = new UserEntityInfo();
        user.setFirstName(request.getFirstname());
        user.setLastName(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);

        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        AuthenticationResponce authenticationResponce = new AuthenticationResponce(jwtToken);
        return authenticationResponce;
    }

    @Override
    public String authUser(AuthenticationRequest request) {

//        AuthenticationResponce
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findUserEntitiesByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
//        return AuthenticationResponce.builder()
//                .token(jwtToken)
//                .build();
        return "main-page";
    }

}