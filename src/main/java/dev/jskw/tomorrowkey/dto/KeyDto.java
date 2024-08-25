package dev.jskw.tomorrowkey.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KeyDto {
    // Getters and Setters
    private PrivateKeyResponseDto privateKey;
    private PublicKeyResponseDto publicKey;
}
