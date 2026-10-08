package com.example.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.security.Principal;
import java.util.Map;
//
//@EnableMultiFactorAuthentication(authorities = {

/// /        FactorGrantedAuthority.PASSWORD_AUTHORITY,
/// /        FactorGrantedAuthority.OTT_AUTHORITY
//})
@SpringBootApplication
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        var u = new JdbcUserDetailsManager(dataSource);
        u.setEnableUpdatePassword(true);
        return u;
    }

    @Bean
    Customizer<HttpSecurity> securityCustomizer() {
//        var amf = AuthorizationManagerFactories
//                .multiFactor()
//                .requireFactors(FactorGrantedAuthority.OTT_AUTHORITY, FactorGrantedAuthority.PASSWORD_AUTHORITY)
////                .requireFactors(bu -> bu.requireFactor(b->b.validDuration(Duration.ofSeconds(10))))
//                .build();
        return http -> http
                .oauth2AuthorizationServer(as -> as.oidc(Customizer.withDefaults()))
//                .authorizeHttpRequests(a -> a
//                        .requestMatchers("/admin").access(amf.hasRole("ADMIN"))
//                )
                .webAuthn(a -> a
                        .rpId("localhost")
                        .allowedOrigins("http://localhost:8080")
                )
                .oneTimeTokenLogin(ott -> ott.tokenGenerationSuccessHandler((_, response, oneTimeToken) -> {

                    response.getWriter().write("you've got console mail!");
                    response.setContentType(MediaType.TEXT_PLAIN_VALUE);

                    IO.println(oneTimeToken.getUsername() + "," +
                            " please go to http://localhost:8080/login/ott?token=" + oneTimeToken.getTokenValue());
                }));
    }

}
