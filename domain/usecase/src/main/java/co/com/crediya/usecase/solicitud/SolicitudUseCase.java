package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.EstadoSolicitud;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.usecase.solicitud.dto.response.SolicitudInfo;
import co.com.crediya.model.solicitud.exception.NotFoundException;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.solicitud.pagination.PageQuery;
import co.com.crediya.model.solicitud.pagination.PageResult;
import co.com.crediya.usecase.solicitud.dto.request.SolicitudUseCaseDto;
import co.com.crediya.usecase.solicitud.validation.ValidacionSolicitud;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class SolicitudUseCase implements ISolicitudUseCase{

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final ValidacionSolicitud validacionSolicitud;
    private final EstadoRepository estadoRepository;

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



    public Mono<PageResult<SolicitudInfo>> ejecutarPagination(PageQuery query){
        return solicitudRepository.findPage(query)
            .flatMap(pageResult -> Flux.fromIterable(pageResult.items())
                .flatMap(solicitud -> {
                    EstadoSolicitud estado = solicitud.getEstado_solicitud() != null
                        ? solicitud.getEstado_solicitud()
                        : EstadoSolicitud.PENDIENTE_REVISION;
                    return tipoPrestamoRepository.findById(solicitud.getId_tipo_prestamo())
                        .flatMap(tipoPrestamo -> estadoRepository.findNameById(Long.valueOf(estado.ordinal() + 1))
                            .map(estadoObj -> new SolicitudInfo(
                                solicitud.getId_solicitud(),
                                solicitud.getDocumento(),
                                solicitud.getEmail(),
                                solicitud.getMonto(),
                                solicitud.getPlazo_meses(),
                                tipoPrestamo.getNombre_tipo_prestamo(),
                                estadoObj.getNombre_estado()
                            ))
                        );
                })
                .collectList()
                .map(items -> PageResult.of(items, pageResult.page(), pageResult.size(), pageResult.totalItems()))
            );
    }

}
