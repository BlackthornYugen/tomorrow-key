package dev.jskw.tomorrowkey;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/keys")
public class KeyManagementController {

    @PostMapping("/generate")
    public ResponseEntity<KeyGenerationResponseDto> generateKey(@RequestBody KeyGenerationResponseDto request) {
        // Business logic for key generation would go here

        // Stub response for now
        KeyGenerationResponseDto response = new KeyGenerationResponseDto();
        response.setPublicKey("BASE64_ENCODED_PUBLIC_KEY");
        response.setPrivateKeyReleaseAt("2024-08-25T15:00:00Z");
        response.setIdentifier("20240825T120000Z-24_hours");
        response.setKeyType(request.getKeyType());
        response.setKeySize(request.getKeySize() != null ? request.getKeySize() : 2048);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{identifier}/private")
    public ResponseEntity<?> getPrivateKey(
            @PathVariable String identifier,
            @RequestHeader("Accept") String acceptHeader) {

        // Business logic for retrieving the private key would go here

        // Stub response for different formats
        switch (acceptHeader) {
            case MediaType.APPLICATION_JSON_VALUE -> {
                PrivateKeyResponseDto response = new PrivateKeyResponseDto();
                response.setPrivateKey("BASE64_ENCODED_PRIVATE_KEY");
                response.setReleasedAt("2024-08-25T15:00:00Z");
                response.setDownloadedAt("2024-08-25T15:01:00Z");
                response.setIdentifier(identifier);
                response.setKeyType("rsa");
                response.setKeySize(2048);

                return ResponseEntity.ok(response);
            }
            case "application/pkix-key" -> {
                // DER format, base64 encoded, not JSON-wrapped
                return ResponseEntity.ok("BASE64_ENCODED_PRIVATE_KEY");
            }
            case "application/x-pem-file" -> {
                // PEM format, not JSON-wrapped
                return ResponseEntity.ok("-----BEGIN PRIVATE KEY-----\nBASE64_ENCODED_PRIVATE_KEY\n-----END PRIVATE KEY-----");
            }
        }

        // Default to returning DER in JSON if no specific header is matched
        PrivateKeyResponseDto response = new PrivateKeyResponseDto();
        response.setPrivateKey("BASE64_ENCODED_PRIVATE_KEY");
        response.setReleasedAt("2024-08-25T15:00:00Z");
        response.setDownloadedAt("2024-08-25T15:01:00Z");
        response.setIdentifier(identifier);
        response.setKeyType("rsa");
        response.setKeySize(2048);

        return ResponseEntity.ok(response);
    }
}
