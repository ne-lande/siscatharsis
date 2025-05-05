package ru.mtuci.siscatharsis.dto.external.auth.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserTokenResponse {
    private String accessToken;

    private String token;
}
