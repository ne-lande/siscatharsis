package ru.mtuci.siscatharsis.dto.signature;

// Little test
public record SignaturePatchRequest(
        String threatName,
        Byte firstBytes,
        String remainderHash,
        Integer remainderLength,
        String fileType,
        Integer offsetStart,
        Integer offsetEnd
) {}
