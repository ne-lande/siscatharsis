package ru.mtuci.siscatharsis.dto.signature;

public record SignatureCreateRequest (
        String threatName,
        Byte firstBytes,
        String remainderHash,
        int remainderLength,
        String fileType,
        int offsetStart,
        int offsetEnd
) {}