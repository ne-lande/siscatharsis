package ru.mtuci.siscatharsis.dto.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "Name is empty")
    private String name;

    private boolean isBlocked;

}
