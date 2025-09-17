package co.com.crediya.r2dbc.estado;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.r2dbc.entity.EstadoSolicitudEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class EstadoSolicitudReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Estado/* change for domain model */,
        EstadoSolicitudEntity/* change for adapter model */,
    Long,
        EstadoSolicitudReactiveRepository
> implements EstadoRepository {
    public EstadoSolicitudReactiveRepositoryAdapter(EstadoSolicitudReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.mapBuilder(d,Estado.EstadoBuilder.class).build());
    }

    @Override
    public Mono<Estado> findNameById(Long id) {
        return repository.findById(id)
            .map(entity -> Estado.builder()
                .id_estado(entity.getId_estado())
                .nombre_estado(entity.getNombre())
                .descripcion_estado(entity.getDescripcion())
                .build()
            );
    }
}
