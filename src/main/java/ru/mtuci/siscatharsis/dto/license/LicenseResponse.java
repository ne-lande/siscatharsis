package ru.mtuci.siscatharsis.dto.license;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LicenseResponse {

    private Date currentDate;
    private int lifetime;
    private Date activationDate;
    private Date expirationDate;
    private Long userId;
    private Long deviceId;
    private Boolean isBlocked;
    private String signature;

    public String getBodyForSigning() {
        StringBuilder sb = new StringBuilder();

        sb.append(currentDate != null ? currentDate.toString() : "");
        sb.append("|");
        sb.append(lifetime);
        sb.append("|");
        sb.append(activationDate != null ? activationDate.toString() : "");
        sb.append("|");
        sb.append(expirationDate != null ? expirationDate.toString() : "");
        sb.append("|");
        sb.append(userId != null ? userId.toString() : "");
        sb.append("|");
        sb.append(deviceId != null ? deviceId.toString() : "");
        sb.append("|");
        sb.append(isBlocked != null ? isBlocked.toString() : "");
        sb.append("|");
        sb.append(signature != null ? signature : "");

        return sb.toString().trim();
    }
}
