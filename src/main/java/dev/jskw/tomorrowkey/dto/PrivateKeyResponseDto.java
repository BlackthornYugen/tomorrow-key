package dev.jskw.tomorrowkey.dto;

import dev.jskw.tomorrowkey.KeyType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PrivateKeyResponseDto {
    private String encoded;
    private String releasedAt;
    private String identifier;
    private KeyType keyType;
    private Integer keySize;
}