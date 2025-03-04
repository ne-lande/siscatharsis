package ru.mtuci.siscatharsis.controller.external;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.external.auth.request.TokenRefresh;
import ru.mtuci.siscatharsis.dto.external.auth.request.UserLogin;
import ru.mtuci.siscatharsis.dto.external.auth.request.UserRegister;
import ru.mtuci.siscatharsis.dto.external.auth.response.UserTokenResponse;
import ru.mtuci.siscatharsis.enums.UserRoleEnum;
import ru.mtuci.siscatharsis.model.RefreshToken;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.JwtService;
import ru.mtuci.siscatharsis.services.RefreshTokenService;
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
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, JwtService jwtService, RefreshTokenService refreshTokenService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody UserRegister userRequest) {
        // ЭТО УМНЕЕ СДЕЛАТЬ
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
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@Valid @RequestBody UserLogin userRequest) {
        String username = userRequest.getLogin();
        String password = userRequest.getPassword();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // early return my beloved
        if (!authentication.isAuthenticated()) {
            return ApiMessage.BadRequest("Invalid credentials");
        }

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);

        UserTokenResponse response = UserTokenResponse.builder()
                .accessToken(jwtService.GenerateToken(username))
                .token(refreshToken.getToken())
                .build();

        return ApiMessage.Success(response);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefresh userRequest) {
        String token = userRequest.getToken();

        UserTokenResponse response = refreshTokenService.findByToken(token)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.GenerateToken(user.getUsername());
                    return UserTokenResponse.builder()
                            .accessToken(accessToken)
                            .token(token)
                            .build();
                }).orElseThrow(() -> new RuntimeException("No refresh Token found"));

        return ApiMessage.Success(response);
    }
}
