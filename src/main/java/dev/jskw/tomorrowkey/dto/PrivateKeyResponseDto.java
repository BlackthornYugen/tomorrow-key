package dev.jskw.tomorrowkey.dto;

import dev.jskw.tomorrowkey.KeyType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PrivateKeyResponseDto {
    private String encoded;
    private String identifier;
    private String createdAt;
    private String releaseAt;
    private KeyType keyType;
    private Integer keySize;
}
