package com.kksg.blog.config;

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
	public OpenAPI openApi() {
		
		String schemeName = "bearerScheme";
		
		return new OpenAPI()
				.addSecurityItem(new SecurityRequirement()
						.addList(schemeName))
				.components(new Components()
						.addSecuritySchemes(schemeName, new SecurityScheme()
								.name(schemeName)
								.type(SecurityScheme.Type.HTTP)
								.bearerFormat("JWT")
								.scheme("bearer")
								)
						)
				.info(new Info()
						.title("Blogging Application APIs")
						.description("This is an blog Apllication Developed By Krishan Kant Singh Gautam")
						.version("1.0")
						.contact(new Contact()
								.name("Krishan Kant SIngh Gautam")
								.email("kksg1999@gmail.com")
								.url("www.krishankant.me"))
						.license(new License().name("Blog App 1.0 Licence").url("http://krishankant.me")))
				.externalDocs(new ExternalDocumentation()
						.description("Github documentation of the Application")
						.url("github.com/codewithkrishan2"))
				;
	}
	
	/*
	 @Bean
	    public Docket docket() {
	        Docket docket = new Docket(DocumentationType.SWAGGER_2);
	        docket.apiInfo(getApiInfo());
	        docket.securityContexts(Arrays.asList(getSecurityContext()));
	        docket.securitySchemes(Arrays.asList(getSchemes()));
	        ApiSelectorBuilder select = docket.select();
	        select.apis(RequestHandlerSelectors.any());
	        select.paths(PathSelectors.any());
	        Docket build = select.build();
	        return build;
	    }


	    private SecurityContext getSecurityContext() {

	        SecurityContext context = SecurityContext
	                .builder()
	                .securityReferences(getSecurityReferences())
	                .build();
	        return context;
	    }

	    private List<SecurityReference> getSecurityReferences() {
	        AuthorizationScope[] scopes = {new AuthorizationScope("Global", "Access Every Thing")};
	        return Arrays.asList(new SecurityReference("JWT", scopes));

	    }

	    private ApiKey getSchemes() {
	        return new ApiKey("JWT", "Authorization", "header");
	    }


	    private ApiInfo getApiInfo() {

	        ApiInfo apiInfo = new ApiInfo(
	                "Product Service Backend : APIS ",
	                "This is backend project created by jalaj",
	                "1.0.0V",
	                "",
	                new Contact("jalaj", "", "jalajgupta97@gmail.com"),
	                "License of APIS",
	                "",
	                new ArrayDeque<>()
	        );

	        return apiInfo;

	    }
	    @Bean
	    public InternalResourceViewResolver defaultViewResolver() {
	        return new InternalResourceViewResolver();
	    }
	
	*/
}
