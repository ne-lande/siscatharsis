package ru.mtuci.siscatharsis.dto.external.auth.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: rewrite all dto to records
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRefresh {
        private String token;
}
