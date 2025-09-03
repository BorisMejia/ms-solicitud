package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.EstadoSolicitud;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.exception.NotFoundException;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.usecase.solicitud.validation.ValidacionSolicitud;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class SolicitudUseCase implements ISolicitudUseCase{

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final ValidacionSolicitud validacionSolicitud;


    public Mono<Solicitud> registrarSolicitud(Solicitud solicitud) {
        return validacionSolicitud.validarBasica(solicitud)
                .then(tipoPrestamoRepository.findById(solicitud.getId_tipo_prestamo())
                        .switchIfEmpty(Mono.error(new NotFoundException("El tipo de prestamo no existe"))))
                .flatMap(tipoPrestamo -> validacionSolicitud.validarContraTipo(solicitud, tipoPrestamo.getMonto_minimo(), tipoPrestamo.getMonto_maximo()))
                .map(validarSolicitud -> validarSolicitud.toBuilder()
                        .estado_solicitud(EstadoSolicitud.PENDIENTE_REVISION)
                        .build())
                .flatMap(solicitudRepository::saveSolicitud);
    }


}
