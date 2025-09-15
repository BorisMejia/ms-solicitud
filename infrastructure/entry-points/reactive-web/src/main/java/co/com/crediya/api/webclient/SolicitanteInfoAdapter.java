package co.com.crediya.api.webclient;

import co.com.crediya.model.solicitante.SolicitanteInfo;
import co.com.crediya.model.solicitante.gateways.SolicitanteInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SolicitanteInfoAdapter implements SolicitanteInfoRepository {

    private final WebClient webClient;

    private static record SolicitanteInfoInfra(String name, Double baseSalary) {}

    @Override
    public Mono<SolicitanteInfo> obtenerPorDocumento(String documento) {
        return webClient.get()
                .uri("/api/v1/user/info/{document}", documento)
                .retrieve()
                .bodyToMono(SolicitanteInfoInfra.class)
                .map(dto -> SolicitanteInfo.fromInfra(documento, dto.name(), dto.baseSalary()))
                .onErrorResume(e -> Mono.just(SolicitanteInfo.fromInfra(documento, "", 0.0)));
    }
}
