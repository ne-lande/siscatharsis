package ru.mtuci.siscatharsis.dto.signature;

import java.util.List;
import java.util.UUID;

public record SignatureFetchUUIDrequest (
        List<UUID> uuidList
) {}