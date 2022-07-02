package com.stl.bsky;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;


@SpringBootApplication
@OpenAPIDefinition(info= @Info(title="BSKY", version="2.0", description="Spring Boot | Documenting Using OpenAPI 3.0 specification | Spring Security | JWT Token | Actuator"))
@SecurityScheme(
	    name = "bearerAuth",
	    type = SecuritySchemeType.HTTP,
	    bearerFormat = "JWT",
	    scheme = "Bearer"
	)


public class BSKYApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(BSKYApplication.class, args);
	}


	@Bean
	public OpenApiCustomiser buildV1OpenAPI() {
		final String securitySchemeName = "Access Token";
		return openApi -> {
			openApi.addSecurityItem(new SecurityRequirement().addList(securitySchemeName));

			openApi.getComponents().addSecuritySchemes(securitySchemeName, new io.swagger.v3.oas.models.security.SecurityScheme()
					.type(io.swagger.v3.oas.models.security.SecurityScheme.Type.APIKEY)
					.in(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER)
					.name(HttpHeaders.AUTHORIZATION));
		};
	}

}
