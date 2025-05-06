package ru.mtuci.siscatharsis.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductCreateUpdateRequest(

    @NotBlank(message = "Name is empty")
    String name,

    boolean isBlocked
) {}
