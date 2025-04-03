package ru.mtuci.siscatharsis.dto.internal.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class LicenseHistoryRequest {

    @NotNull
    private Long licenseId;

    @NotNull
    private Long userId;

    @NotBlank
    private String status;

    private Date changeDate;

    @NotBlank
    private String description;
}
