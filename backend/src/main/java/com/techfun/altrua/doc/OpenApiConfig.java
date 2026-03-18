package com.techfun.altrua.doc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
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
                        .description("### 🤝 Conectando Doadores e ONGs\n" +
                                "API para gestão de doações e recrutamento de voluntários.\n\n" +
                                "**Equipe de Desenvolvimento:**\n" +
                                "* [Gabriel](https://github.com/gabriel-mkv)\n" +
                                "* [Mateus](https://github.com/mateusfg7)")
                        .contact(new Contact()
                                .name("Altrua Team")
                                .url("https://github.com/mateusfg7/altrua-api"))
                        .license(new License()
                                .name("GPL-3.0")
                                .url("https://www.gnu.org/licenses/gpl-3.0.html")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentação Técnica & Guia de Instalação")
                        .url("https://github.com/mateusfg7/altrua-api/blob/main/README.md"));
    }
}
