package ru.mtuci.siscatharsis.configs;

import org.springframework.beans.factory.annotation.Autowired;
import ru.mtuci.siscatharsis.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.enums.UserRoleEnum;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import java.security.SecureRandom;
//TODO: 1. Как временное решение пойдёт, но лучше тогда использовать систему миграции и скрипт, который не попадёт в гитхаб

@Component
public class InitalizeAdmin implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String username;

    @Value("${admin.password}")
    private String password;

    private void initializePassword() {
        if (password == null || password.isEmpty()) {
            password = generateRandomPassword(16);
            System.out.println("===================");
            System.out.println("Generated admin password: " + password);
            System.out.println("===================");
        }
    }

    private String generateRandomPassword(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        SecureRandom random = new SecureRandom();
        StringBuilder passwordBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            passwordBuilder.append(chars.charAt(random.nextInt(chars.length())));
        }

        return passwordBuilder.toString();
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            userService.findByLogin(this.username);
        } catch (UsernameNotFoundException e) {
            initializePassword();
            userService.save(
                new User(this.username,
                passwordEncoder.encode(getPassword()),
                "admin@siscatharsis.ru",
                UserRoleEnum.ROLE_ADMIN, null)
            );
        }
    }
}
