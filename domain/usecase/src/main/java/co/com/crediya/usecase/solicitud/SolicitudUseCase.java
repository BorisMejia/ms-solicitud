package co.com.crediya.usecase.solicitud;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.EstadoSolicitud;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.notificacionsolicitud.NotificacionSolicitud;
import co.com.crediya.model.notificacionsolicitud.gateways.NotificacionSolicitudRepository;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitante.gateways.SolicitanteInfoRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
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
    private final SolicitanteInfoRepository solicitanteInfoRepository;
    private final NotificacionSolicitudRepository notificacionSolicitudRepository;

    @Override
    public Mono<Solicitud> registrarSolicitud(SolicitudUseCaseDto solicitud) {
    return validacionSolicitud.validarBasica(solicitud)
        .flatMap(dto -> tipoPrestamoRepository.findById(solicitud.id_tipo_prestamo())
        .switchIfEmpty(Mono.error(new NotFoundException("El tipo de prestamo no existe")))
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
        .flatMap(solicitudRepository::saveSolicitud)
        );
    }

    public Mono<PageResult<SolicitudInfo>> ejecutarPagination(PageQuery query) {
        return solicitudRepository.findPage(query)
                .flatMap(pageResult -> {
                    var pendientesPorUsuario = pageResult.items().stream()
                            .filter(solicitud -> solicitud.getEstado_solicitud() == null ||
                                    (solicitud.getEstado_solicitud() != EstadoSolicitud.RECHAZADO && solicitud.getEstado_solicitud() != EstadoSolicitud.APROBADO))
                            .collect(Collectors.groupingBy(
                                    Solicitud::getDocumento,
                                    Collectors.mapping(Solicitud::getMonto, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                            ));

                    return Flux.fromIterable(pageResult.items())
                            .flatMap(solicitud -> buildSolicitudInfo(solicitud, pendientesPorUsuario))
                            .collectList()
                            .map(items -> PageResult.of(items, pageResult.page(), pageResult.size(), pageResult.totalItems()));
                });
    }

    private Mono<SolicitudInfo> buildSolicitudInfo(Solicitud solicitud, Map<String, BigDecimal> pendientesPorUsuario) {
        EstadoSolicitud estado = solicitud.getEstado_solicitud() != null
                ? solicitud.getEstado_solicitud()
                : EstadoSolicitud.PENDIENTE_REVISION;
        Mono<TipoPrestamo> tipoPrestamo$ = tipoPrestamoRepository.findById(solicitud.getId_tipo_prestamo());
        Mono<String> nombreEstado$ = estadoRepository.findNameById(Long.valueOf(estado.ordinal() + 1))
                .map(Estado::getNombre_estado)
                .defaultIfEmpty("");
        Mono<String> nombreSolicitante$ = solicitanteInfoRepository.obtenerPorDocumento(solicitud.getDocumento())
                .map(s -> s != null && s.nombre() != null ? s.nombre() : "")
                .defaultIfEmpty("");
        Mono<Double> salarioBase$ = solicitanteInfoRepository.obtenerPorDocumento(solicitud.getDocumento())
                .map(s -> s != null && s.salarioBase() != null ? s.salarioBase() : 0.0)
                .defaultIfEmpty(0.0);

        BigDecimal montoMensualSolicitud = pendientesPorUsuario.getOrDefault(solicitud.getDocumento(), BigDecimal.ZERO);

        return Mono.zip(tipoPrestamo$, nombreEstado$, nombreSolicitante$, salarioBase$)
                .map(tuple -> {
                    TipoPrestamo tipoPrestamo = tuple.getT1();
                    String nombreTipoPrestamo = tipoPrestamo != null && tipoPrestamo.getNombre_tipo_prestamo() != null ? tipoPrestamo.getNombre_tipo_prestamo() : "";
                    BigDecimal tasaInteres = tipoPrestamo != null ? tipoPrestamo.getTasa_interes() : null;
                    return new SolicitudInfo(
                            solicitud.getId_solicitud(),
                            solicitud.getDocumento(),
                            solicitud.getEmail(),
                            solicitud.getMonto(),
                            solicitud.getPlazo_meses(),
                            nombreTipoPrestamo,
                            tuple.getT2() != null ? tuple.getT2() : "",
                            tuple.getT3() != null ? tuple.getT3() : "",
                            tuple.getT4() != null ? tuple.getT4() : 0.0,
                            tasaInteres,
                            montoMensualSolicitud
                    );
                });
    }

    public Mono<Solicitud> actualizarEstado(Long idSolicitud, EstadoSolicitud nuevoEstado) {
        return solicitudRepository.findById(idSolicitud)
                .switchIfEmpty(Mono.error(new NotFoundException("Solicitud no encontrada")))
                .flatMap(solicitud -> {
                    Solicitud actualizada = solicitud.toBuilder()
                            .estado_solicitud(nuevoEstado)
                            .build();
                    return solicitudRepository.saveSolicitud(actualizada)
                            .doOnSuccess(saved -> {
                                NotificacionSolicitud notificacion = new NotificacionSolicitud(
                                        saved.getId_solicitud(),
                                        saved.getEmail(),
                                        nuevoEstado.name()
                                );
                                notificacionSolicitudRepository.notificar(notificacion);
                            });
                });
    }
}