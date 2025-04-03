package ru.mtuci.siscatharsis.controller.external;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.external.auth.request.TokenRefresh;
import ru.mtuci.siscatharsis.dto.external.auth.request.UserLogin;
import ru.mtuci.siscatharsis.dto.external.auth.request.UserRegister;
import ru.mtuci.siscatharsis.dto.external.auth.response.UserTokenResponse;
import ru.mtuci.siscatharsis.enums.UserRoleEnum;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.services.SessionService;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiMessage;
import ru.mtuci.siscatharsis.utils.JwtUtil;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final DeviceService deviceService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, SessionService sessionService, DeviceService deviceService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, JwtUtil jwtUtil1) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.sessionService = sessionService;
        this.deviceService = deviceService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil1;
    }

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody UserRegister userRequest) {
        String login = userRequest.getLogin();
        String email = userRequest.getEmail();

        String deviceName = userRequest.getDeviceName();
        String macAddress = userRequest.getMacAddress();

        if (userService.existsByLoginAndEmail(login, email)) {
            return ApiMessage.BadRequest("User already exists");
        }

        String passwordHash = passwordEncoder.encode(userRequest.getPassword());

        userService.save(
                User.builder()
                        .login(login)
                        .email(email)
                        .passwordHash(passwordHash)
                        .role(UserRoleEnum.ROLE_USER)
                        .licenses(null)
                        .build()
        );

        User user = userService.findByLogin(login);
        deviceService.save(
                Device.builder()
                        .user(user)
                        .name(deviceName)
                        .macAddress(macAddress)
                        .build()
        );

        Device device = deviceService.findByMacAddressAndUser(macAddress, user);
        UserDetails userDetails = userService.loadUserByUsername(login);
        UserTokenResponse response = sessionService.generateTokenPair(userDetails, user.getId(), device.getId());

        return ApiMessage.Success(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@Valid @RequestBody UserLogin userRequest) {
        String username = userRequest.getLogin();
        String password = userRequest.getPassword();
        Long deviceId = userRequest.getDeviceId();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // early return my beloved
        if (!authentication.isAuthenticated()) {
            return ApiMessage.BadRequest("Invalid credentials");
        }

        Long userId = userService.findByLogin(username).getId();
        UserDetails userDetails = userService.loadUserByUsername(username);
        UserTokenResponse response = sessionService.generateTokenPair(userDetails, userId, deviceId);

        return ApiMessage.Success(response);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefresh userRequest) {
        String token = userRequest.getToken();

        String login = jwtUtil.extractLogin(token);
        Long userId = userService.findByLogin(login).getId();
        Long deviceId = jwtUtil.extractDeviceId(token);
        UUID tokenId = jwtUtil.extractRefreshTokenId(token);

        sessionService.rotateToken(tokenId, userId);

        UserDetails userDetails = userService.loadUserByUsername(login);
        UserTokenResponse response = sessionService.generateTokenPair(userDetails, userId, deviceId);

        return ApiMessage.Success(response);
    }
}
