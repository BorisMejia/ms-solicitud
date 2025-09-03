package co.com.crediya.usecase.solicitud.validation;

import co.com.crediya.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ValidacionSolicitud {

    Mono<Solicitud> validarBasica(Solicitud s);
    Mono<Solicitud> validarContraTipo(Solicitud s, BigDecimal min, BigDecimal max);
}
