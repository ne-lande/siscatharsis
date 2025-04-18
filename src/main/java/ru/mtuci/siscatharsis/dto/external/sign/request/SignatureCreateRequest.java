package ru.mtuci.siscatharsis.dto.external.sign.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignatureCreateRequest {
        String threatName;
        Byte firstBytes;
        String remainderHash;
        int remainderLength;
        String fileType;
        int offsetStart;
        int offsetEnd;
}
