
package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.EstadoSolicitud;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.usecase.solicitud.dto.response.SolicitudInfo;
import co.com.crediya.model.solicitud.pagination.PageQuery;
import co.com.crediya.model.solicitud.pagination.PageResult;
import co.com.crediya.usecase.solicitud.dto.request.SolicitudUseCaseDto;
import reactor.core.publisher.Mono;

public interface ISolicitudUseCase {
    Mono<Solicitud> registrarSolicitud(SolicitudUseCaseDto solicitud);
    Mono<PageResult<SolicitudInfo>> ejecutarPagination(PageQuery query);
    Mono<Solicitud> actualizarEstado(Long idSolicitud, EstadoSolicitud nuevoEstado);
}
