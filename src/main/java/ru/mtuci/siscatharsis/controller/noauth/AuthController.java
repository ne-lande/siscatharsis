package ru.mtuci.siscatharsis.controller.noauth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.user.RefreshTokenRequest;
import ru.mtuci.siscatharsis.dto.user.LoginRequest;
import ru.mtuci.siscatharsis.dto.user.RegisterRequest;
import ru.mtuci.siscatharsis.dto.user.RefreshTokenResponse;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.user.User;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.services.user.SessionService;
import ru.mtuci.siscatharsis.services.user.UserService;
import ru.mtuci.siscatharsis.utils.ResponseUtils;
import ru.mtuci.siscatharsis.utils.JwtUtil;

import java.util.UUID;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final SessionService sessionService;
    private final DeviceService deviceService;
    private final AuthenticationManager authenticationManager;
    private final ResponseUtils responseUtils;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody RegisterRequest userRequest) {
        String login = userRequest.login();
        String email = userRequest.email();

        if (userService.existsByLoginAndEmail(login, email)) {
            return responseUtils.badRequest("User already exists");
        }

        User user = User.builder()
                .login(login)
                .email(email)
                .role(User.Role.ROLE_USER)
                .build();

        userService.create(user, userRequest.password());

        String macAddress = userRequest.macAddress();

        Device device = Device.builder()
                .user(user)
                .macAddress(macAddress)
                .build();

        deviceService.create(device);

        RefreshTokenResponse response = sessionService.generateTokenPair(user, user.getId(), device.getId());

        return responseUtils.success(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@Valid @RequestBody LoginRequest userRequest) {
        String username = userRequest.login();
        String password = userRequest.password();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if (!authentication.isAuthenticated()) {
            return responseUtils.badRequest("Invalid credentials");
        }

        String macAddress = userRequest.macAddress();
        User user = (User) authentication.getPrincipal();
        Device device = deviceService.requireUserDevice(macAddress, user);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        RefreshTokenResponse response = sessionService.generateTokenPair(userDetails, user.getId(), device.getId());

        return responseUtils.success(response);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest userRequest) {
        String token = userRequest.token();

        String login = jwtUtil.extractLogin(token);
        Long userId = userService.requireByLogin(login).getId();
        Long deviceId = jwtUtil.extractDeviceId(token);
        UUID tokenId = jwtUtil.extractRefreshTokenId(token);

        sessionService.rotateToken(tokenId, userId);

        UserDetails userDetails = userService.loadUserByUsername(login);
        RefreshTokenResponse response = sessionService.generateTokenPair(userDetails, userId, deviceId);

        return responseUtils.success(response);
    }
}
