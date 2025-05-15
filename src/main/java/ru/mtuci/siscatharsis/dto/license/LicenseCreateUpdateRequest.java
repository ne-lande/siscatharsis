package ru.mtuci.siscatharsis.dto.license;

import java.util.Date;

public record LicenseCreateUpdateRequest(
    Long ownerId,
    Long productId,
    Long typeId,
    Date endingDate
) {}
