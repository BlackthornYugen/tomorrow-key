package dev.jskw.tomorrowkey;

import dev.jskw.tomorrowkey.dto.KeyDto;
import dev.jskw.tomorrowkey.dto.KeyGenerationResponseDto;
import dev.jskw.tomorrowkey.persistance.KeyEntity;
import dev.jskw.tomorrowkey.persistance.KeyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.FileAlreadyExistsException;
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
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'");
    }

    public KeyGenerationResponseDto generateKey(KeyType keyType, Integer keySize, String identifier, Long releaseHours)
            throws NoSuchAlgorithmException, FileAlreadyExistsException {
        var keyPairGenerator = KeyPairGenerator.getInstance(keyType.name());
        keySize = keySize != null ? keySize : 2048;
        keyPairGenerator.initialize(keySize);
        var keyPair = keyPairGenerator.generateKeyPair();
        var now = LocalDateTime.now(ZoneOffset.UTC).withSecond(0).withNano(0);
        if (releaseHours == null) {
            releaseHours = 24L;
        }
        if (identifier == null) {
            identifier = "%s-%d_hours".formatted(now.format(formatter), releaseHours);
        }
        var existingKey = getById(identifier);
        if (existingKey != null) {
            throw new FileAlreadyExistsException("Key with identifier %s already exists".formatted(identifier));
        }

        var key = KeyEntity.builder()
                .id(identifier)
                .createdAt(now.toInstant(ZoneOffset.UTC))
                .releaseAt(now.plusHours(releaseHours).toInstant(ZoneOffset.UTC))
                .keyType(keyType.name())
                .keySize(keySize)
                .encodedPrivateKey(keyPair.getPrivate().getEncoded())
                .encodedPublicKey(keyPair.getPublic().getEncoded())
                .build();
        var response = new KeyGenerationResponseDto();
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
        return getKeyDto(keyEntity);
    }

    public Page<KeyDto> getKeys(Pageable pageable) {
        return repository.findAll(pageable).map(this::getKeyDto);
    }

    private KeyDto getKeyDto(KeyEntity keyEntity) {
        var builder = KeyDto.builder()
                .id(keyEntity.getId())
                .keySize(keyEntity.getKeySize())
                .releaseAt(keyEntity.getReleaseAt().atZone(ZoneOffset.UTC).format(formatter))
                .createdAt(keyEntity.getCreatedAt().atZone(ZoneOffset.UTC).format(formatter))
                .keyType(KeyType.valueOf(keyEntity.getKeyType()))
                .publicKey(Base64.getEncoder().encodeToString(keyEntity.getEncodedPublicKey()));
        if (keyEntity.getReleaseAt() != null && keyEntity.getReleaseAt().isBefore(LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC))) {
            builder.privateKey(Base64.getEncoder().encodeToString(keyEntity.getEncodedPrivateKey()));
        }
        return builder.build();
    }
}