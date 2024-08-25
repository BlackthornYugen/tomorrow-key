// src/main/java/dev/jskw/tomorrowkey/KeyGenerationService.java
package dev.jskw.tomorrowkey;

import dev.jskw.tomorrowkey.dto.KeyGenerationResponseDto;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
public class KeyGenerationService {

    public KeyGenerationResponseDto generateKey(KeyType keyType, Integer keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyType.name());
        keyPairGenerator.initialize(keySize != null ? keySize : 2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String releaseAt = now.plusHours(24).format(formatter);
        String identifier = now.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")) + "-24_hours";

        KeyGenerationResponseDto response = new KeyGenerationResponseDto();
        response.setEncoded(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        response.setReleaseAt(releaseAt);
        response.setIdentifier(identifier);
        response.setKeyType(keyType.name().toLowerCase());
        response.setKeySize(keySize != null ? keySize : 2048);

        return response;
    }
}