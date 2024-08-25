package dev.jskw.tomorrowkey.dto;

import dev.jskw.tomorrowkey.KeyType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KeyGenerationResponseDto {
    private String encoded;
    private String createdAt;
    private String releaseAt;
    private String identifier;
    private KeyType keyType;
    private Integer keySize;

}
