package com.techfun.altrua.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
				.components(new Components()
						.addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
				.info(new Info()
						.title("Altrua API")
						.version("1.0.0")
						.description("## 🤝 Conectando Propósitos, Transformando Vidas\n\n" +
								"O **Altrua** é uma plataforma para a gestão estratégica de causas sociais, " +
								"facilitando a conexão entre doadores e organizações sem fins lucrativos (ONGs).\n\n" +
								"### 🚀 Principais Funcionalidades\n" +
								"* **Gestão de ONGs:** Registro, geolocalização e visibilidade institucional.\n" +
								"* **Voluntariado:** Recrutamento e gestão de vagas para causas sociais.\n\n" +
								"### 🔒 Segurança e Acesso\n" +
								"Esta API utiliza **JWT (JSON Web Token)** para autenticação. Para testar os endpoints protegidos, "
								+
								"obtenha o token via endpoint de login (ou signup) e utilize o botão **Authorize** abaixo.\n\n"
								+
								"--- \n" +
								"**Desenvolvido por:**\n" +
								"- [Gabriel](https://github.com/gabriel-mkv)\n" +
								"- [Mateus](https://github.com/mateusfg7)\n")
						.contact(new Contact()
								.name("Altrua Team Support")
								.url("https://github.com/mateusfg7/altrua"))
						.license(new License()
								.name("GPL-3.0")
								.url("https://www.gnu.org/licenses/gpl-3.0.html")))
				.externalDocs(new ExternalDocumentation()
						.description("Documentação Técnica & Guia de Instalação")
						.url("https://github.com/mateusfg7/altrua/blob/main/README.md"));
	}

	private SecurityScheme createAPIKeyScheme() {
		return new SecurityScheme().type(SecurityScheme.Type.HTTP)
				.bearerFormat("JWT")
				.scheme("bearer");
	}
}
