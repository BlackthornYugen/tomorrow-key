package dev.jskw.tomorrowkey;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PrivateKeyResponseDto {
    // Getters and Setters
    private String privateKey;
    private String releasedAt;
    private String downloadedAt;
    private String identifier;
    private String keyType;
    private Integer keySize;

}
