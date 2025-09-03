package co.com.crediya.usecase.solicitud.validation;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.exception.ValidationException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;


public class ValidacionSolicitudBasic implements ValidacionSolicitud{

    public Mono<Solicitud> validarBasica(Solicitud solicitud) {
        if (solicitud.getMonto() == null || solicitud.getMonto().compareTo(BigDecimal.ZERO) <= 0)
            return Mono.error(new ValidationException("El monto debe ser mayor que 0"));
        if (solicitud.getPlazo_meses() == null || solicitud.getPlazo_meses() <= 0)
            return Mono.error(new ValidationException("El plazo en meses debe ser mayor que 0"));
        if (solicitud.getDocumento() == null || solicitud.getDocumento().isBlank())
            return Mono.error(new ValidationException("El documento es requerido"));
        if (solicitud.getId_tipo_prestamo() == null)
            return Mono.error(new ValidationException("El tipo de prestamo es requerido"));
        return Mono.just(solicitud);
    }

    public Mono<Solicitud> validarContraTipo(Solicitud solicitud, BigDecimal min, BigDecimal max) {
        if (min != null && solicitud.getMonto().compareTo(min) < 0)
            return Mono.error(new ValidationException("Monto menor al mínimo permitido"));
        if (max != null && solicitud.getMonto().compareTo(max) > 0)
            return Mono.error(new ValidationException("Monto mayor al máximo permitido"));
        return Mono.just(solicitud);
    }

}
