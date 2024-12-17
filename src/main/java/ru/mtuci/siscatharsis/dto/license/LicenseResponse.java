package ru.mtuci.siscatharsis.dto.license;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

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

    public String getBodyForSigning(){
        return "hi";
    }
}
