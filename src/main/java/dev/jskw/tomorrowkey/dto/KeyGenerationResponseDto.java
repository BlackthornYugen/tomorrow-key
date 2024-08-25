package dev.jskw.tomorrowkey.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KeyGenerationResponseDto {
    // Getters and Setters
    private String encoded;
    private String releaseAt;
    private String identifier;
    private String keyType;
    private Integer keySize;

}
