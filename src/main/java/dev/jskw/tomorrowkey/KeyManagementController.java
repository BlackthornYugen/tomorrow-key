package dev.jskw.tomorrowkey;

import dev.jskw.tomorrowkey.dto.KeyDto;
import dev.jskw.tomorrowkey.dto.KeyGenerationResponseDto;
import dev.jskw.tomorrowkey.dto.PrivateKeyResponseDto;
import dev.jskw.tomorrowkey.dto.PublicKeyResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/keys")
public class KeyManagementController {
    private final KeyGenerationService keyGenerationService;

    public KeyManagementController(KeyGenerationService keyGenerationService) {
        this.keyGenerationService = keyGenerationService;
    }

    @PostMapping("/generate")
    public ResponseEntity<KeyGenerationResponseDto> generateKey(@RequestBody KeyGenerationResponseDto request) {
        try {
            KeyGenerationResponseDto response = keyGenerationService.generateKey(KeyType.valueOf(request.getKeyType().toUpperCase()), request.getKeySize());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<KeyDto> getKeyJson(@PathVariable String identifier) {
        PrivateKeyResponseDto privateKeyResponseDto = new PrivateKeyResponseDto();
        privateKeyResponseDto.setEncoded("BASE64_ENCODED_PRIVATE_KEY");
        privateKeyResponseDto.setReleasedAt("2024-08-25T15:00:00Z");
        privateKeyResponseDto.setIdentifier(identifier);
        privateKeyResponseDto.setKeyType("rsa");
        privateKeyResponseDto.setKeySize(2048);

        PublicKeyResponseDto publicKeyResponseDto = new PublicKeyResponseDto();
        publicKeyResponseDto.setEncoded("BASE64_ENCODED_PUBLIC_KEY");
        publicKeyResponseDto.setCreatedAt("2024-08-25T12:00:00Z");
        publicKeyResponseDto.setIdentifier(identifier);
        publicKeyResponseDto.setKeyType("rsa");
        publicKeyResponseDto.setKeySize(2048);

        KeyDto keyDto = new KeyDto();
        keyDto.setPrivateKey(privateKeyResponseDto);
        keyDto.setPublicKey(publicKeyResponseDto);

        return ResponseEntity.ok(keyDto);
    }

    @GetMapping(value = "/{identifier}/private", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PrivateKeyResponseDto> getPrivateKeyJson(@PathVariable String identifier) {
        PrivateKeyResponseDto response = new PrivateKeyResponseDto();
        response.setEncoded("BASE64_ENCODED_PRIVATE_KEY");
        response.setReleasedAt("2024-08-25T15:00:00Z");
        response.setIdentifier(identifier);
        response.setKeyType("rsa");
        response.setKeySize(2048);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{identifier}/private", produces = "application/pkix-key")
    public ResponseEntity<String> getPrivateKeyDer(@PathVariable String identifier) {
        return ResponseEntity.ok("BASE64_ENCODED_PRIVATE_KEY");
    }

    @GetMapping(value = "/{identifier}/private", produces = "application/x-pem-file")
    public ResponseEntity<String> getPrivateKeyPem(@PathVariable String identifier) {
        return ResponseEntity.ok("-----BEGIN PRIVATE KEY-----\nBASE64_ENCODED_PRIVATE_KEY\n-----END PRIVATE KEY-----");
    }

    @GetMapping(value = "/{identifier}/public", produces = "application/pkix-key")
    public ResponseEntity<String> getPublicKeyDer(@PathVariable String identifier) {
        return ResponseEntity.ok("BASE64_ENCODED_PUBLIC_KEY");
    }

    @GetMapping(value = "/{identifier}/public", produces = "application/x-pem-file")
    public ResponseEntity<String> getPublicKeyPem(@PathVariable String identifier) {
        return ResponseEntity.ok("-----BEGIN PUBLIC KEY-----\nBASE64_ENCODED_PUBLIC_KEY\n-----END PRIVATE KEY-----");
    }
}
