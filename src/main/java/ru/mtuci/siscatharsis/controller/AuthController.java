package ru.mtuci.siscatharsis.controller;

import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.auth.*;
import ru.mtuci.siscatharsis.enums.UserRoleEnum;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiMessage;
import ru.mtuci.siscatharsis.utils.EntityNotFoundException;
import ru.mtuci.siscatharsis.utils.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(
        UserService userService,
        PasswordEncoder passwordEncoder,
        JwtUtil jwtUtil
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(
        @Valid @RequestBody UserRegister userRequest
    ) {
        try {
            userService.findByLogin(userRequest.getLogin());
            userService.findByEmail(userRequest.getEmail());
            return ApiMessage.BadRequest("User already exists");
        } catch (UsernameNotFoundException e) {
            assert true;
        } catch (EntityNotFoundException e) {
            assert true;
        }

        userService.save(
            new User(
                userRequest.getLogin(),
                passwordEncoder.encode(userRequest.getPassword()),
                userRequest.getEmail(),
                UserRoleEnum.ROLE_USER,
                null
            )
        );

        UserDetails userDetails = userService.loadUserByUsername(
            userRequest.getLogin()
        );
        String token = jwtUtil.generateToken(userDetails);

        return ApiMessage.Success(token);
        //working as planned
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(
        @Valid @RequestBody UserLogin userRequest
    ) {
        User user = userService.findByLogin(userRequest.getLogin());

        if (
            !passwordEncoder.matches(
                userRequest.getPassword(),
                user.getPassword()
            )
        ) {
            return ApiMessage.BadRequest("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
            userService.loadUserByUsername(user.getUsername())
        );

        return ApiMessage.Success(token);
    }
}
