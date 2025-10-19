package ch.insurance.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI insuranceApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Insurance API")
                .description("API for managing insurance clients and contracts")
                .version("1.0.1")
                .contact(new Contact().name("Insurance API Support").email("support@insurance.ch"))
                .license(new License().name("Proprietary"))
                .termsOfService("https://insurance.ch/terms"));
  }
}
