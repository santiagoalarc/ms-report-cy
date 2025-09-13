package co.com.bancolombia;

import co.com.bancolombia.config.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MainApplication {
    public static void main(String[] args) {

        EnvLoader.loadConfig();

        SpringApplication.run(MainApplication.class, args);
    }
}
