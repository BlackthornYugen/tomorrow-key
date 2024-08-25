package dev.jskw.tomorrowkey.dto;

import dev.jskw.tomorrowkey.KeyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyDto {
    private String id;
    private KeyType keyType;
    private Integer keySize;
    private Instant createdAt;
    private Instant releaseAt;
    private String privateKey;
    private String publicKey;
}
