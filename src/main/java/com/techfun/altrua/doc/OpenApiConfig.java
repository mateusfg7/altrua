package com.techfun.altrua.doc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Altrua API")
                        .version("1.0.0")
                        .description("Altrua API documentation")
                        .contact(new Contact()
                                .name("gabriel-mkv")
                                .email("gabrielmkv.dev@gmail.com"))
                        .contact(new Contact()
                                .name("mateusfg7"))
                        .license(new License()
                                .name("GPL-3.0")));
    }
}
