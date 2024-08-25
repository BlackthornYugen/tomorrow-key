package dev.jskw.tomorrowkey.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PublicKeyResponseDto {
    // Getters and Setters
    private String encoded;
    private String createdAt;
    private String identifier;
    private String keyType;
    private Integer keySize;
}
