package com.techfun.altrua.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
				.components(new Components()
						.addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
				.info(new Info()
						.title("Altrua API")
						.version("1.0.0")
						.description(
								"""
										## Conectando Propósitos, Transformando Vidas

										O **Altrua** é uma plataforma de gestão estratégica de causas sociais que conecta \
										doadores e organizações sem fins lucrativos (ONGs), promovendo transparência e \
										impacto social mensurável.

										---

										### Principais Módulos

										| Módulo | Descrição |
										|--------|-----------|
										| 🏢 **Gestão de ONGs** | Registro, geolocalização e visibilidade institucional |
										| 🤝 **Voluntariado** | Recrutamento e gestão de vagas para causas sociais |

										---

										### Autenticação

										Esta API utiliza **JWT (JSON Web Token)** para autenticação. Para acessar endpoints protegidos:

										1. Obtenha seu token via `/auth/login` ou `/auth/signup`
										2. Insira o token no campo **Bearer Token** no painel lateral direito
										---

										Desenvolvido por [Gabriel](https://github.com/gabriel-mkv) & [Mateus](https://github.com/mateusfg7)
										""")
						.license(new License()
								.name("GPL-3.0")
								.url("https://www.gnu.org/licenses/gpl-3.0.html")))
				.externalDocs(new ExternalDocumentation()
						.description("Documentação Técnica & Guia de Instalação")
						.url("https://github.com/mateusfg7/altrua/blob/main/docs/README.md"));
	}

	private SecurityScheme createAPIKeyScheme() {
		return new SecurityScheme().type(SecurityScheme.Type.HTTP)
				.bearerFormat("JWT")
				.scheme("bearer");
	}
}
