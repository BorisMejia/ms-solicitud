package co.com.crediya.r2dbc.solicitud;

import co.com.crediya.model.estado.EstadoSolicitud;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.solicitud.pagination.PageQuery;
import co.com.crediya.model.solicitud.pagination.PageResult;
import co.com.crediya.r2dbc.entity.SolicitudEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import org.springframework.data.relational.core.query.Query;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import java.util.List;

@Repository
public class SolicitudReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud/* change for domain model */,
        SolicitudEntity/* change for adapter model */,
        Long,
        SolicitudReactiveRepository
> implements SolicitudRepository {
    public SolicitudReactiveRepositoryAdapter(SolicitudReactiveRepository repository, ObjectMapper mapper, R2dbcEntityTemplate template) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> {
            Solicitud.SolicitudBuilder builder = mapper.mapBuilder(d, Solicitud.SolicitudBuilder.class);
            if (d.getId_estado() != null) {
                builder.estado_solicitud(co.com.crediya.model.estado.EstadoSolicitud.fromId(d.getId_estado().intValue()));
            } else {
                builder.estado_solicitud(null);
            }
            return builder.build();
        });

        this.template = template;
    }
    private final R2dbcEntityTemplate template;
    @Transactional
    @Override
    public Mono<Solicitud> saveSolicitud(Solicitud solicitud) {
        SolicitudEntity entity = toData(solicitud);
        if (solicitud.getEstado_solicitud() != null) {
            entity.setId_estado(Long.valueOf(solicitud.getEstado_solicitud().getId()));
        } else {
            entity.setId_estado(null);
        }
        return repository.save(entity)
                .map(this::toEntity);
    }

    @Override
    public Mono<PageResult<Solicitud>> findPage(PageQuery pageQuery) {
        Sort.Direction direction = pageQuery.asc() ? Sort.Direction.ASC : Sort.Direction.DESC;
        Query select = Query.empty()
                .sort(Sort.by(direction, pageQuery.sortBy()))
                .limit(pageQuery.size())
                .offset((long) pageQuery.page() * pageQuery.size());

        Mono<List<Solicitud>> items$ = template.select(select, SolicitudEntity.class)
                .map(this::toEntity)
                .collectList();

        Mono<Long> total$ = template.count(Query.empty(), SolicitudEntity.class);
        return Mono.zip(items$, total$)
                .map(tuple -> PageResult.of(tuple.getT1(), pageQuery.page(), pageQuery.size(), tuple.getT2()))
                ;
    }

    @Override
    public Mono<Solicitud> updateEstadoSolicitud(Long idSolicitud, EstadoSolicitud nuevoEstado) {
        return findById(idSolicitud)
            .switchIfEmpty(Mono.error(new RuntimeException("Solicitud no encontrada")))
            .flatMap(solicitud -> {
                SolicitudEntity entity = toData(solicitud);
                entity.setId_estado(Long.valueOf(nuevoEstado.getId()));
                return repository.save(entity).map(this::toEntity);
            });
    }
}
