package com.example.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId;
import org.springframework.security.oauth2.client.web.client.support.OAuth2RestClientHttpServiceGroupConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.registry.ImportHttpServices;

@ImportHttpServices(MessageClient.class)
@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    OAuth2RestClientHttpServiceGroupConfigurer oauth2RestClientHttpServiceGroupConfigurer(
            OAuth2AuthorizedClientManager auth2AuthorizedClientManager) {
        return OAuth2RestClientHttpServiceGroupConfigurer.from(auth2AuthorizedClientManager);
    }

}

@ClientRegistrationId("spring")
interface MessageClient {

    @GetExchange("http://localhost:8081/message")
    Message get();
}

/* @Component
class MessageClient {

    private final RestClient http;

    MessageClient(RestClient.Builder http, OAuth2AuthorizedClientManager auth2AuthorizedClientManager) {
        var requestInterceptor = new OAuth2ClientHttpRequestInterceptor(auth2AuthorizedClientManager);
        requestInterceptor.setClientRegistrationIdResolver(request -> "spring");
        this.http = http
                .requestInterceptor(requestInterceptor)
                .build();
    }

    Message get() {
        return this.http
                .get()
                .uri("http://localhost:8081/message")
//                .headers(h -> h.setBearerAuth(at))
                .retrieve()
                .body(Message.class);
    }

}
*/

record Message(String message) {
}


@Controller
@ResponseBody
class MeController {

    private final MessageClient messageClient;

    MeController(MessageClient messageClient) {
        this.messageClient = messageClient;
    }

    @GetMapping("/")
    Message user(
//            @RegisteredOAuth2AuthorizedClient("spring") OAuth2AuthorizedClient auth2AuthorizedClient
    ) {

        return this.messageClient.get();
    }
}