package ru.mtuci.siscatharsis.dto.internal.license.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LicenseUpdateRequest{
    private Long userId;
    private Long productId;
    private Long typeId;

    private Date firstActivationDate;
    private Date endingDate;
    private Boolean isBlocked;
    private Integer deviceCount;
    private Integer duration;
    private String description;
}
