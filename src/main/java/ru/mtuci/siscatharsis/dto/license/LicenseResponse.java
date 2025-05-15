package ru.mtuci.siscatharsis.dto.license;

import java.util.Date;

public record LicenseResponse (
    Date currentDate,
    int lifetime,
    Date activationDate,
    Date expirationDate,
    Long userId,
    Long deviceId,
    Boolean isBlocked
) {
    @Override
    public String toString() {
        return String.format(
                "%s%d%s%s%d%d%b",
                currentDate, lifetime,
                activationDate, expirationDate,
                userId, deviceId, isBlocked
        );
    }
}

