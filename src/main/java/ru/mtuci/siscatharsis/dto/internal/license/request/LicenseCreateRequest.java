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
public class LicenseCreateRequest{
    private Long ownerId;
    private Long productId;
    private Long typeId;

    private Date endingDate;
    private String description;
}
