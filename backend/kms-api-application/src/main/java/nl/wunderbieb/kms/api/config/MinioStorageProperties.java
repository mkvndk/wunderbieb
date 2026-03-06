package nl.wunderbieb.kms.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kms.storage.minio")
public record MinioStorageProperties(
    String endpoint,
    String accessKey,
    String secretKey,
    String bucket
) {
}
