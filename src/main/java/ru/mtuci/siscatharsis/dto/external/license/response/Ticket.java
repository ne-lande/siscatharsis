package ru.mtuci.siscatharsis.dto.external.license.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.mtuci.siscatharsis.utils.SignatureInstance;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    private Date currentDate;
    private int lifetime;
    private Date activationDate;
    private Date expirationDate;
    private Long userId;
    private Long deviceId;
    private Boolean isBlocked;

    private SignatureInstance signature;

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
