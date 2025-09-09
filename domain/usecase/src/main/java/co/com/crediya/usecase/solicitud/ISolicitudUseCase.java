package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.usecase.solicitud.dto.request.SolicitudUseCaseDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ISolicitudUseCase {
    Mono<Solicitud> registrarSolicitud(SolicitudUseCaseDto solicitud);
}
