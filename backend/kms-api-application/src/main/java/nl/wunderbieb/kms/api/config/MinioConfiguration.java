package nl.wunderbieb.kms.api.config;

import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

  @Bean
  @ConditionalOnProperty(prefix = "kms.storage.minio", name = "endpoint")
  MinioClient minioClient(MinioStorageProperties properties) {
    return MinioClient.builder()
        .endpoint(properties.endpoint())
        .credentials(properties.accessKey(), properties.secretKey())
        .build();
  }
}
