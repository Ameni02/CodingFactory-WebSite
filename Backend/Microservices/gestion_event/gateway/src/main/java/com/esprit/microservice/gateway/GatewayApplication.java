package com.esprit.microservice.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
/*
	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder){

		return builder.routes()
				.route("Candidat", r->r.path("/candidats/**")
						.uri("http://localhost:8080"))

				.route("job-s", r->r.path("/jobs/**")
						.uri("http://localhost:8082"))
				.build();
	}
	*/
}
