package springproject.iam.v1.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfigurer {
  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("public")
        .packagesToScan("springproject.boot.v1.controller")
        .build();
  }

  @Bean
  public OpenAPI myOpenAPI() {
    Server dev = new Server().url("http://localhost:8080").description("Dev");
    Contact contact = new Contact().email("hoalx17@gmail.com").name("Lê Xuân Hòa");
    License mitLicense =
        new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");
    Info info =
        new Info()
            .title("Swagger Open API 3")
            .version("1.0")
            .contact(contact)
            .description("Spring Boot IAM Service")
            .termsOfService("https://www.spring.com/terms")
            .license(mitLicense);
    return new OpenAPI().info(info).servers(List.of(dev));
  }
}
