package co.com.crediya.usecase.solicitud.validation;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.usecase.solicitud.dto.request.SolicitudUseCaseDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ValidacionSolicitud {

    Mono<Void> validarBasica(SolicitudUseCaseDto solicitud);
    Mono<Void> validarContraTipo(SolicitudUseCaseDto solicitud, BigDecimal min, BigDecimal max);
}
