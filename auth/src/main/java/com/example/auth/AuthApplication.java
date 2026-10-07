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

//@EnableMultiFactorAuthentication(authorities = {})
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
    Customizer<HttpSecurity> httpSecurityCustomizer() {
//        var amf = AuthorizationManagerFactories.multiFactor()
//                .requireFactor(b -> b.validDuration(Duration.ofSeconds(10)).passwordAuthority())
//                .requireFactor(RequiredFactor.Builder::ottAuthority)
//                .build();
        return security -> security
                .oauth2AuthorizationServer(a -> a.oidc(Customizer.withDefaults()))
//                .authorizeHttpRequests(a -> a
//                        .requestMatchers("/me").authenticated()
//                        .requestMatchers("/admin").access(amf.hasRole("ADMIN"))
//                )
                .webAuthn(a -> a
                        .allowedOrigins("http://localhost:8080")
                        .rpName("bootiful") // todo does this work without it?
                        .rpId("localhost")
                )
                .oneTimeTokenLogin(ott -> ott
                        .tokenGenerationSuccessHandler((_, response, oneTimeToken) -> {

                                    response.getWriter().write("you've got console mail!");
                                    response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                                    IO.println(oneTimeToken.getUsername() +
                                            ", please go http://localhost:8080/login/ott?token=" +
                                            oneTimeToken.getTokenValue());
                                }
                        ));
    }

//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity security) {
//        return security
//                .formLogin(Customizer.withDefaults())
//                .httpBasic(Customizer.withDefaults())
//                .authorizeHttpRequests(a -> a.anyRequest().authenticated())
//                .build();
//    }

    /*
    @Bean
    InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder passwordEncoder) {
        var builder = User.builder().roles("USER");
        var pw1 = passwordEncoder.encode("pw");
        var pw2 = passwordEncoder.encode("pw");
        IO.println(pw1 + System.lineSeparator() + pw2);
        return new InMemoryUserDetailsManager(
                builder.username("daniel").password(pw1).build(),
                builder.username("josh").password(pw2).build()
        );
    }
     */
}
