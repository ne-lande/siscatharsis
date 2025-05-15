package ru.mtuci.siscatharsis.dto.signature;

public record SignatureCreateRequest (
        String threatName,
        byte[] firstBytes,
        byte[] remainderHash,
        int remainderLength,
        String fileType,
        int offsetStart,
        int offsetEnd
) {}