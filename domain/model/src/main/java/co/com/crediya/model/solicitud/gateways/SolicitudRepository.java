package co.com.crediya.model.solicitud.gateways;

import co.com.crediya.model.estado.EstadoSolicitud;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.pagination.PageQuery;
import co.com.crediya.model.solicitud.pagination.PageResult;
import reactor.core.publisher.Mono;

public interface SolicitudRepository {
    Mono<Solicitud> saveSolicitud(Solicitud solicitud);
    Mono<PageResult<Solicitud>> findPage(PageQuery query);
    Mono<Solicitud> updateEstadoSolicitud(Long idSolicitud, EstadoSolicitud nuevoEstado);
    Mono<Solicitud> findById(Long id);
}
