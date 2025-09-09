package co.com.crediya.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class JwtDecoderConfig {

    @Bean
    public ReactiveJwtDecoder jwtDecoder(
            @Value("${security.oauth2.resource-server.jwt.jwk-set-uri:}") String jwkSetUri,
            @Value("${security.jwt.issuer:}") String issuer
    ) {
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();

        if (issuer != null && !issuer.isBlank()) {
            OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
            decoder.setJwtValidator(withIssuer);
        }
        return decoder;
    }

    @Bean
    ApplicationRunner jwksProbe(@Value("${security.oauth2.resource-server.jwt.jwk-set-uri:}") String uri) {
        return args -> WebClient.create().get().uri(uri)
                .retrieve().bodyToMono(String.class)
                .doOnNext(b -> log.info("JWKS ok: {}", b.substring(0, Math.min(b.length(), 80))+"..."))
                .doOnError(e -> log.error("JWKS ERROR {}", e.toString()))
                .subscribe();
    }
}