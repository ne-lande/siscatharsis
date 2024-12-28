package ru.mtuci.siscatharsis.utils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignatureInstance {
    String signature;
    String public_key;
}
