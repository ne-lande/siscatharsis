package ru.mtuci.siscatharsis.configs;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ru.mtuci.siscatharsis.utils.ResponseUtils;
import ru.mtuci.siscatharsis.utils.exceptions.EntityNotFoundException;
import ru.mtuci.siscatharsis.utils.exceptions.LicenseException;

@SuppressWarnings("unused")
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ResponseUtils responseUtils;

    @ExceptionHandler({
            EntityNotFoundException.class,
            RuntimeException.class,
            UsernameNotFoundException.class
    })
    public ResponseEntity<?> handleServerExceptions(Exception exception) {
        return responseUtils.serverError(exception.getMessage());
    }

    @ExceptionHandler({LicenseException.class})
    public ResponseEntity<?> handleBadRequest(Exception exception) {
        return responseUtils.badRequest(exception.getMessage());
    }


    @ExceptionHandler({OptimisticLockException.class})
    public ResponseEntity<?> handleOptimisticLock(OptimisticLockException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Resource was modified concurrently. Please reload and try again.");
    }
}
