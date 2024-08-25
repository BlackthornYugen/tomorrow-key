package dev.jskw.tomorrowkey.persistance;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyEntity {
    @Id
    private String id;

    @Lob
    private byte[] encodedPrivateKey;

    @Lob
    private byte[] encodedPublicKey;

    private String keyType;
    private Integer keySize;
    private Instant createdAt;
    private Instant releaseAt;
}