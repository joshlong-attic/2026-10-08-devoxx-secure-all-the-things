package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    RouterFunction<ServerResponse> uiRoute() {
        return route()
                .GET("/**", http())
                .before(BeforeFilterFunctions.uri("http://localhost:8020"))
                .build();
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    RouterFunction<ServerResponse> apiRoute() {
        return route()
                .GET("/api/**", http())
                .before(BeforeFilterFunctions.uri("http://localhost:8081"))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .before(BeforeFilterFunctions.rewritePath("/api", "/"))
                .build();

    }
}


// :8080/index.html -> :8020/index.html
// :8080/api/message -> :8081/message