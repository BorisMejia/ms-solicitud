package co.com.crediya.api.webclient;

import co.com.crediya.model.solicitante.SolicitanteInfo;
import co.com.crediya.model.solicitante.gateways.SolicitanteInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Component
@RequiredArgsConstructor
public class SolicitanteInfoAdapter implements SolicitanteInfoRepository {

    private final WebClient webClient;
    private static record SolicitanteInfoInfra(String name, Double baseSalary) {}

    @Override
    public Mono<SolicitanteInfo> obtenerPorDocumento(String documento) {
        return ReactiveSecurityContextHolder.getContext()
            .map(securityContext -> {
                Authentication auth = securityContext.getAuthentication();
                if (auth instanceof JwtAuthenticationToken jwtAuth)
                    return jwtAuth.getToken().getTokenValue();
                throw new IllegalStateException("No JWT token found in security context");
            })
            .flatMap(token -> webClient.get()
                .uri("/api/v1/user/info/{document}", documento)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(SolicitanteInfoInfra.class)
                .map(dto -> SolicitanteInfo.fromInfra(documento, dto.name(), dto.baseSalary()))
                .onErrorResume(e -> Mono.just(SolicitanteInfo.fromInfra(documento, "", 0.0))));
    }
}
