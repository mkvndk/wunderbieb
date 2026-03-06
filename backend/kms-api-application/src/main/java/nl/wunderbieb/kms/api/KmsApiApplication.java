package nl.wunderbieb.kms.api;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ConfigurationPropertiesScan
@EntityScan(basePackages = "nl.wunderbieb.kms")
@EnableJpaRepositories(basePackages = "nl.wunderbieb.kms")
@SpringBootApplication(scanBasePackages = "nl.wunderbieb.kms")
public class KmsApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(KmsApiApplication.class, args);
  }
}
