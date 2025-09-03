package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ISolicitudUseCase {
    Mono<Solicitud> registrarSolicitud(Solicitud solicitud);
}
