package dev.jskw.tomorrowkey;

import dev.jskw.tomorrowkey.dto.KeyDto;
import dev.jskw.tomorrowkey.dto.KeyGenerationResponseDto;
import dev.jskw.tomorrowkey.persistance.KeyEntity;
import dev.jskw.tomorrowkey.persistance.KeyRepository;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
public class KeyGenerationService {
    final KeyRepository repository;

    public KeyGenerationService(KeyRepository repository) {
        this.repository = repository;
    }

    public KeyGenerationResponseDto generateKey(KeyType keyType, Integer keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyType.name());
        keySize = keySize != null ? keySize : 2048;
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String releaseAt = now.plusHours(24).format(formatter);
        String identifier = now.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")) + "-24_hours";

        KeyGenerationResponseDto response = new KeyGenerationResponseDto();
        response.setEncoded(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        response.setReleaseAt(releaseAt);
        response.setIdentifier(identifier);
        response.setKeyType(keyType);
        response.setKeySize(keySize);
        repository.save(KeyEntity.builder()
                .id(identifier)
                .keyType(keyType.name())
                .keySize(keySize)
                .encodedPrivateKey(keyPair.getPrivate().getEncoded())
                .encodedPublicKey(keyPair.getPublic().getEncoded())
                .build()
        );

        return response;
    }

    public KeyDto getById(String id) {
        var keyEntity = repository.findById(id).orElse(null);
        if (keyEntity == null) {
            return null;
        }

        return KeyDto.builder()
                .id(keyEntity.getId())
                .keySize(keyEntity.getKeySize())
                .keyType(KeyType.valueOf(keyEntity.getKeyType()))
                .privateKey(Base64.getEncoder().encodeToString(keyEntity.getEncodedPrivateKey()))
                .publicKey(Base64.getEncoder().encodeToString(keyEntity.getEncodedPublicKey()))
                .build();
    }
}