package ru.mtuci.siscatharsis.configs;

import lombok.RequiredArgsConstructor;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.services.UserService;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import java.security.SecureRandom;
//TODO: 1. Как временное решение пойдёт, но лучше тогда использовать систему миграции и скрипт, который не попадёт в гитхаб

@Component
@RequiredArgsConstructor
public class InitalizeAdmin implements CommandLineRunner {
    private final UserService userService;
    private final DeviceService deviceService;

    @Value("${admin.username}") private String username;
    @Value("${admin.password}") private String password;

    private void initializePassword() {
        if (!(password == null || password.isEmpty())) {
            return;
        }

        password = generateRandomPassword();
        System.out.println("===================");
        System.out.println("Generated admin password: " + password);
        System.out.println("===================");
    }

    private String generateRandomPassword() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder passwordBuilder = new StringBuilder(16);

        for (int i = 0; i < 16; i++) {
            passwordBuilder.append(chars.charAt(random.nextInt(chars.length())));
        }

        return passwordBuilder.toString();
    }

    @Override
    public void run(String... args) {
        String email = "admin@siscatharsis.ru";
        if (userService.existsByLoginAndEmail(username, email)) {
            return;
        }

        initializePassword();

        User user = User.builder()
                .login(username)
                .email("admin@siscatharsis.ru")
                .role(User.Role.ROLE_ADMIN)
                .build();

        userService.create(user, password);

        Device device = Device.builder()
                .name("admin")
                .macAddress("admin")
                .user(user)
                .build();

        deviceService.create(device);
    }
}
