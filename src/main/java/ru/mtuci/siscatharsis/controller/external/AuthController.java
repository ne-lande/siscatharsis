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
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.RefreshToken;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.services.JwtService;
import ru.mtuci.siscatharsis.services.RefreshTokenService;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiMessage;
import ru.mtuci.siscatharsis.utils.EntityNotFoundException;
import ru.mtuci.siscatharsis.utils.JwtUtil;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final DeviceService deviceService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, JwtService jwtService, DeviceService deviceService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.deviceService = deviceService;
        this.authenticationManager = authenticationManager;
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

        userService.save(
            new User(
                login,
                passwordEncoder.encode(userRequest.getPassword()),
                email,
                UserRoleEnum.ROLE_USER,
                null
            )
        );

        User user =  userService.findByLogin(login);
        deviceService.save(
                Device.builder()
                        .user(user)
                        .name(deviceName)
                        .macAddress(macAddress)
                        .build()
        );

        Device device = deviceService.findByMacAddressAndUser(macAddress, user);
        UserDetails userDetails = userService.loadUserByUsername(
            userRequest.getLogin()
        );

        UserTokenResponse response = jwtService.generateTokenPair(userDetails, device.getId());

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

        UserDetails userDetails = userService.loadUserByUsername(username);
        UserTokenResponse response = jwtService.generateTokenPair(userDetails, deviceId);

        return ApiMessage.Success(response);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefresh userRequest) {
        String token = userRequest.getToken();

        String login = jwtService.extractLogin(token);
        Long deviceId = jwtService.extractDeviceId(token);
        UUID tokenId = jwtService.extractRefreshTokenId(token);
        UserDetails userDetails = userService.loadUserByUsername(login);

        UserTokenResponse response = jwtService.rotateToken(tokenId, login, deviceId);

        return ApiMessage.Success(response);
    }
}
