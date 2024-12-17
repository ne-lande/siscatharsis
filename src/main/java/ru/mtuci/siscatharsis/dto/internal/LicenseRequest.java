package ru.mtuci.siscatharsis.dto.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LicenseRequest {
    private String code;
    private Long userId;
    private Long productId;
    private Long typeId;
    private Date firstActivationDate;
    private Date endingDate;
    private Boolean isBlocked;
    private Integer deviceCount;
    private Long ownerId;
    private Integer duration;
    private String description;
}
