package co.com.crediya.usecase.solicitud.validation;

import co.com.crediya.model.estado.EstadoSolicitud;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.exception.ValidationException;
import co.com.crediya.usecase.solicitud.dto.request.SolicitudUseCaseDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;


public class ValidacionSolicitudBasic implements ValidacionSolicitud{

    public Mono<SolicitudUseCaseDto> validarBasica(SolicitudUseCaseDto solicitud) {
        if (solicitud.monto() == null || solicitud.monto().compareTo(BigDecimal.ZERO) <= 0)
            return Mono.error(new ValidationException("El monto debe ser mayor que 0"));
        if (solicitud.plazo_meses() == null || solicitud.plazo_meses() <= 0)
            return Mono.error(new ValidationException("El plazo en meses debe ser mayor que 0"));
        if (solicitud.documento() == null || solicitud.documento().isBlank())
            return Mono.error(new ValidationException("El documento es requerido"));
        if (solicitud.id_tipo_prestamo() == null)
            return Mono.error(new ValidationException("El tipo de prestamo es requerido"));

        return Mono.just(solicitud);
    }

    public Mono<SolicitudUseCaseDto> validarContraTipo(SolicitudUseCaseDto solicitud, BigDecimal min, BigDecimal max) {
        if (min != null && solicitud.monto().compareTo(min) < 0)
            return Mono.error(new ValidationException("Monto menor al mínimo permitido"));
        if (max != null && solicitud.monto().compareTo(max) > 0)
            return Mono.error(new ValidationException("Monto mayor al máximo permitido"));

        return Mono.just(solicitud);
    }

}
