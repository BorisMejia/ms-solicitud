package co.com.crediya.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@Configuration
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
}