package co.com.crediya.model.estado.gateways;

import co.com.crediya.model.estado.Estado;
import reactor.core.publisher.Mono;

public interface EstadoRepository {
    Mono<Estado>findNameById(Long id);
}
