package co.com.crediya.model.solicitante.gateways;

import co.com.crediya.model.solicitante.SolicitanteInfo;
import reactor.core.publisher.Mono;

public interface SolicitanteInfoRepository {
    Mono<SolicitanteInfo> obtenerPorDocumento(String documento);
}
