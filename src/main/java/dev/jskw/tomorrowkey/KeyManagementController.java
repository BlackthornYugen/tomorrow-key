package dev.jskw.tomorrowkey;

import dev.jskw.tomorrowkey.dto.KeyDto;
import dev.jskw.tomorrowkey.dto.KeyGenerationRequestDto;
import dev.jskw.tomorrowkey.dto.KeyGenerationResponseDto;
import dev.jskw.tomorrowkey.dto.PrivateKeyResponseDto;
import dev.jskw.tomorrowkey.dto.PublicKeyResponseDto;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.FileAlreadyExistsException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/keys")
@Slf4j
public class KeyManagementController {
    private final KeyGenerationService keyGenerationService;

    public KeyManagementController(KeyGenerationService keyGenerationService) {
        this.keyGenerationService = keyGenerationService;
    }


    @PutMapping("/{identifier}")
    public ResponseEntity<KeyGenerationResponseDto> generateKey(
            @PathVariable @Pattern(regexp = "[A-Za-z][A-Za-z0-9-]{3,127}") String identifier,
            @RequestBody KeyGenerationRequestDto request) {
        try {
            KeyGenerationResponseDto response = keyGenerationService.generateKey(
                    request.getKeyType(),
                    request.getKeySize(),
                    identifier,
                    request.getReleaseHours()
            );
            log.info("Key with identifier {} generated", identifier);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            log.info("Algorithm not found", noSuchAlgorithmException);
            log.info(noSuchAlgorithmException.getMessage(), noSuchAlgorithmException);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidParameterException invalidParameterException) {
            log.info("Invalid parameter", invalidParameterException);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (FileAlreadyExistsException fileAlreadyExistsException) {
            log.info("Key with identifier {} already exists", identifier, fileAlreadyExistsException);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<KeyDto> getKeyJson(@PathVariable String identifier) {
        var keyDto = keyGenerationService.getById(identifier);
        if (keyDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(keyDto);
    }

    @GetMapping(value = "/{identifier}/private", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PrivateKeyResponseDto> getPrivateKeyJson(@PathVariable String identifier) {
        var keyDto = keyGenerationService.getById(identifier);
        if (keyDto == null) {
            return ResponseEntity.notFound().build();
        }

        PrivateKeyResponseDto response = new PrivateKeyResponseDto();
        response.setEncoded(keyDto.getPrivateKey());
        response.setIdentifier(keyDto.getId());
        response.setKeyType(keyDto.getKeyType());
        response.setKeySize(keyDto.getKeySize());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{identifier}/private", produces = "application/pkix-key")
    public ResponseEntity<String> getPrivateKeyDer(@PathVariable String identifier) {
        var keyDto = keyGenerationService.getById(identifier);
        if (keyDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(keyDto.getPrivateKey());
    }

    @GetMapping(value = "/{identifier}/private", produces = "application/x-pem-file")
    public ResponseEntity<String> getPrivateKeyPem(@PathVariable String identifier) {
        var keyDto = keyGenerationService.getById(identifier);
        if (keyDto == null || keyDto.getPrivateKey() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(getPem(keyDto, false));
    }

    @GetMapping(value = "/{identifier}/public", produces = "application/json")
    public ResponseEntity<PublicKeyResponseDto> getPublicKeyJson(@PathVariable String identifier) {
        var keyDto = keyGenerationService.getById(identifier);
        if (keyDto == null) {
            return ResponseEntity.notFound().build();
        }

        var response = new PublicKeyResponseDto();
        response.setEncoded(keyDto.getPublicKey());
        response.setIdentifier(keyDto.getId());
        response.setKeyType(keyDto.getKeyType());
        response.setKeySize(keyDto.getKeySize());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{identifier}/public", produces = "application/pkix-key")
    public ResponseEntity<String> getPublicKeyDer(@PathVariable String identifier) {
        var keyDto = keyGenerationService.getById(identifier);
        if (keyDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(keyDto.getPublicKey());
    }

    @GetMapping(value = "/{identifier}/public", produces = "application/x-pem-file")
    public ResponseEntity<String> getPublicKeyPem(@PathVariable String identifier) {
        KeyDto keyDto = keyGenerationService.getById(identifier);
        return ResponseEntity.ok(getPem(keyDto, true));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<KeyDto>> getKeys(
            @PageableDefault(size = 40, sort = "releaseAt", direction = Sort.Direction.ASC) @ParameterObject Pageable pageable) {
        Page<KeyDto> keys = keyGenerationService.getKeys(pageable);
        return ResponseEntity.ok(keys);
    }

    private String wrapBase64String(String base64, int lineLength) {
        StringBuilder wrapped = new StringBuilder();
        int index = 0;
        while (index < base64.length()) {
            wrapped.append(base64, index, Math.min(index + lineLength, base64.length()));
            wrapped.append("\n");
            index += lineLength;
        }
        return wrapped.toString();
    }

    private String getPem(KeyDto keyDto, boolean isPublic) {
        final String label;
        final String keyTypeString;
        final String key;
        if (isPublic) {
            key = keyDto.getPublicKey();
            keyTypeString = "";
            label = "PUBLIC KEY";
        } else {
            key = keyDto.getPrivateKey();
            keyTypeString = keyDto.getKeyType().equals(KeyType.EC) ? "EC " : "";
            label = "PRIVATE KEY";
        }
        return String.format("-----BEGIN %s%s-----\n%s-----END %s%s-----\n",
                keyTypeString, label,
                wrapBase64String(key, 64),
                keyTypeString, label);
    }
}
