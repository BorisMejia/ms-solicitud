package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.EstadoSolicitud;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.exception.NotFoundException;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.usecase.solicitud.dto.request.SolicitudUseCaseDto;
import co.com.crediya.usecase.solicitud.validation.ValidacionSolicitud;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class SolicitudUseCase implements ISolicitudUseCase{

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final ValidacionSolicitud validacionSolicitud;

    @Override
    public Mono<Solicitud> registrarSolicitud(SolicitudUseCaseDto solicitud) {
        return validacionSolicitud.validarBasica(solicitud)
                .then(tipoPrestamoRepository.findById(solicitud.id_tipo_prestamo())
                        .switchIfEmpty(Mono.error(new NotFoundException("El tipo de prestamo no existe"))))
                .flatMap(validacion -> validacionSolicitud.validarContraTipo(solicitud, validacion.getMonto_minimo(), validacion.getMonto_maximo()))
                .then(Mono.fromSupplier(() ->
                        Solicitud.builder()
                                .documento(solicitud.documento())
                                .email(solicitud.email())
                                .monto(solicitud.monto())
                                .plazo_meses(solicitud.plazo_meses())
                                .id_tipo_prestamo(solicitud.id_tipo_prestamo())
                                .estado_solicitud(EstadoSolicitud.PENDIENTE_REVISION)
                                .build()))
                .flatMap(solicitudRepository::saveSolicitud);
    }
}
