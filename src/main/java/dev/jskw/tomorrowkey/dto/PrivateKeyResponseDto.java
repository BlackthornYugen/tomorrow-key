package dev.jskw.tomorrowkey.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PrivateKeyResponseDto {
    // Getters and Setters
    private String encoded;
    private String releasedAt;
    private String identifier;
    private String keyType;
    private Integer keySize;

}
