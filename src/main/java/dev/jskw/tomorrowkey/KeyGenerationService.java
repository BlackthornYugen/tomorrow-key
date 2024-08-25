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
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
public class KeyGenerationService {
    final KeyRepository repository;
    final DateTimeFormatter formatter;

    public KeyGenerationService(KeyRepository repository) {
        this.repository = repository;
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    }

    public KeyGenerationResponseDto generateKey(KeyType keyType, Integer keySize, String identifier, Long releaseHours) throws NoSuchAlgorithmException {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyType.name());
        keySize = keySize != null ? keySize : 2048;
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        LocalDateTime now = LocalDateTime.now();
        if (releaseHours == null) {
            releaseHours = 24L;
        }
        if (identifier == null) {
            identifier = "%s-%d_hours".formatted(now.format(formatter), releaseHours);
        }
        var key = KeyEntity.builder()
                .id(identifier)
                .createdAt(LocalDateTime.now().toInstant(ZoneOffset.UTC))
                .releaseAt(LocalDateTime.now().plusHours(releaseHours).toInstant(ZoneOffset.UTC))
                .keyType(keyType.name())
                .keySize(keySize)
                .encodedPrivateKey(keyPair.getPrivate().getEncoded())
                .encodedPublicKey(keyPair.getPublic().getEncoded())
                .build();
        KeyGenerationResponseDto response = new KeyGenerationResponseDto();
        response.setEncoded(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        response.setReleaseAt(key.getReleaseAt().atZone(ZoneOffset.UTC).format(formatter));
        response.setCreatedAt(key.getCreatedAt().atZone(ZoneOffset.UTC).format(formatter));
        response.setIdentifier(key.getId());
        response.setKeyType(keyType);
        response.setKeySize(keySize);
        repository.save(key);

        return response;
    }

    public KeyDto getById(String id) {
        var keyEntity = repository.findById(id).orElse(null);
        if (keyEntity == null) {
            return null;
        }
        var builder = KeyDto.builder()
                .id(keyEntity.getId())
                .keySize(keyEntity.getKeySize())
                .keyType(KeyType.valueOf(keyEntity.getKeyType()))
                .publicKey(Base64.getEncoder().encodeToString(keyEntity.getEncodedPublicKey()));
        // if released set the private key
        if (keyEntity.getReleaseAt().isBefore(LocalDateTime.now().toInstant(ZoneOffset.UTC))) {
            builder.privateKey(Base64.getEncoder().encodeToString(keyEntity.getEncodedPrivateKey()));
        }
        return builder.build();
    }
}