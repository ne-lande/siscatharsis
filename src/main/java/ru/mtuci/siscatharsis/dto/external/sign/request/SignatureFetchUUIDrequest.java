package ru.mtuci.siscatharsis.dto.external.sign.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignatureFetchUUIDrequest {
        List<UUID> uuidList;
}
