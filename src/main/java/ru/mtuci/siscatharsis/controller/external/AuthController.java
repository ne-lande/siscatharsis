package ru.mtuci.siscatharsis.controller.external;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final DeviceService deviceService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody UserRegister userRequest) {
        String login = userRequest.getLogin();
        String email = userRequest.getEmail();

        if (userService.existsByLoginAndEmail(login, email)) {
            return ApiMessage.BadRequest("User already exists");
        }

        String passwordHash = passwordEncoder.encode(userRequest.getPassword());

        User user = User.builder()
                .login(login)
                .email(email)
                .passwordHash(passwordHash)
                .role(User.Role.ROLE_USER)
                .licenses(null)
                .build();

        userService.create(user);

        String macAddress = userRequest.getMacAddress();
        Device device = Device.builder()
                .user(user)
                .macAddress(macAddress)
                .build();

        deviceService.create(device, user);

        UserTokenResponse response = sessionService.generateTokenPair((UserDetails) user, user.getId(), device.getId());

        return ApiMessage.Success(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@Valid @RequestBody UserLogin userRequest) {
        String username = userRequest.login();
        String password = userRequest.password();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if (!authentication.isAuthenticated()) {
            return ApiMessage.BadRequest("Invalid credentials");
        }

        String macAddress = userRequest.macAddress();
        User user = (User) authentication.getPrincipal();
        Device device = deviceService.requireUserDevice(macAddress, user);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserTokenResponse response = sessionService.generateTokenPair(userDetails, user.getId(), device.getId());

        return ApiMessage.Success(response);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefresh userRequest) {
        String token = userRequest.getToken();

        String login = jwtUtil.extractLogin(token);
        Long userId = userService.requireByLogin(login).getId();
        Long deviceId = jwtUtil.extractDeviceId(token);
        UUID tokenId = jwtUtil.extractRefreshTokenId(token);

        sessionService.rotateToken(tokenId, userId);

        UserDetails userDetails = userService.loadUserByUsername(login);
        UserTokenResponse response = sessionService.generateTokenPair(userDetails, userId, deviceId);

        return ApiMessage.Success(response);
    }
}
