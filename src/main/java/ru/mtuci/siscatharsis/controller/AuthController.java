package ru.mtuci.siscatharsis.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import ru.mtuci.siscatharsis.dto.auth.*;
import ru.mtuci.siscatharsis.enums.UserRoleEnum;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.JwtUtil;

import ru.mtuci.siscatharsis.utils.ApiMessage;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody UserRegister userRequest) {
        if (userService.findByLogin(userRequest.getLogin()) != null || userService.findByEmail(userRequest.getEmail()) != null) {
            return ApiMessage.BadRequest("User already exists");
        }

        userService.save(
            new User(userRequest.getLogin(), passwordEncoder.encode(userRequest.getPassword()), userRequest.getEmail(), UserRoleEnum.ROLE_USER, null)
        );

        UserDetails userDetails = userService.loadUserByUsername(userRequest.getLogin());
        String token = jwtUtil.generateToken(userDetails);

        return ApiMessage.Success(token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@Valid @RequestBody UserLogin userRequest) {
        User user = userService.findByLogin(userRequest.getLogin());

        if (user == null) {
            return ApiMessage.BadRequest("Invalid credentials");
        }

        if (!passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {
            return ApiMessage.BadRequest("Invalid credentials");
        }

        String token = jwtUtil.generateToken(userService.loadUserByUsername(user.getUsername()));

        return ApiMessage.Success(token);
    }
}
