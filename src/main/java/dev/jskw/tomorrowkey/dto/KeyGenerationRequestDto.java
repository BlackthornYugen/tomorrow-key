package dev.jskw.tomorrowkey.dto;

import dev.jskw.tomorrowkey.KeyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KeyGenerationRequestDto {
    @Schema(description = "Number of hours after which the key will be released", example = "24")
    private Long releaseHours;

    @Schema(description = "Type of the key", example = "RSA")
    private KeyType keyType;

    @Schema(description = "Size of the key in bits",
            example = "2048",
            allowableValues = {"1024", "2048", "4096", "256", "384", "521"})
    private Integer keySize;
}
