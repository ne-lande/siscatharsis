package ru.mtuci.siscatharsis.dto.external.sign.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignatureFetchDiff {
        Instant dateTime;
}
