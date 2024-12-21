package ru.mtuci.siscatharsis.configs;

import org.springframework.beans.factory.annotation.Autowired;
import ru.mtuci.siscatharsis.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.enums.UserRoleEnum;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Component
public class InitalizeAdmin implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            userService.findByLogin("admin");
        } catch (UsernameNotFoundException e) {
            userService.save(
                new User("admin", passwordEncoder.encode("megapassword123"), "admin@siscatharsis.ru", UserRoleEnum.ROLE_ADMIN, null)
            );
        }
    }
}
