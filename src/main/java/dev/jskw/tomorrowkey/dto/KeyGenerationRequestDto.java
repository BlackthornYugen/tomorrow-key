package dev.jskw.tomorrowkey.dto;

import dev.jskw.tomorrowkey.KeyType;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KeyGenerationRequestDto {
    @Schema(description = "Number of hours after which the key will be released", example = "24")
    private Long releaseHours;

    @Schema(description = "Unique identifier for the key", example = "key-12345")
    private String identifier;

    @Schema(description = "Type of the key", example = "RSA")
    private KeyType keyType;

    @Schema(description = "Size of the key in bits", example = "2048")
    private Integer keySize;
}
