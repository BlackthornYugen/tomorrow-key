package dev.jskw.tomorrowkey;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KeyGenerationResponseDto {
    // Getters and Setters
    private String publicKey;
    private String privateKeyReleaseAt;
    private String identifier;
    private String keyType;
    private Integer keySize;

}
