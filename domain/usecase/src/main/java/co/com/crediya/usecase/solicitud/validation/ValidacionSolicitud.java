package co.com.crediya.usecase.solicitud.validation;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.usecase.solicitud.dto.request.SolicitudUseCaseDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ValidacionSolicitud {

    Mono<SolicitudUseCaseDto> validarBasica(SolicitudUseCaseDto solicitud);
    Mono<SolicitudUseCaseDto> validarContraTipo(SolicitudUseCaseDto solicitud, BigDecimal min, BigDecimal max);
}
