package ru.mtuci.siscatharsis.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ru.mtuci.siscatharsis.utils.ApiConstructor;
import ru.mtuci.siscatharsis.utils.EntityNotFoundException;
import ru.mtuci.siscatharsis.utils.LicenseException;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ApiConstructor apiConstructor;

    @ExceptionHandler({
            EntityNotFoundException.class,
            RuntimeException.class,
            UsernameNotFoundException.class
    })
    public ResponseEntity<?> handleServerExceptions(Exception exception) {
        return apiConstructor.serverError(exception.getMessage());
    }

    @ExceptionHandler({LicenseException.class})
    public ResponseEntity<?> handleBadRequest(Exception exception) {
        return apiConstructor.badRequest(exception.getMessage());
    }
}
