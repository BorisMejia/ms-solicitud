package co.com.crediya.r2dbc.tipoPrestamo;

import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.r2dbc.entity.TipoPrestamoEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class TipoPrestamoReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        TipoPrestamo
        /* change for domain model */,
        TipoPrestamoEntity/* change for adapter model */,
    Long,
        TipoPrestamoReactiveRepository
> implements TipoPrestamoRepository {
    public TipoPrestamoReactiveRepositoryAdapter(TipoPrestamoReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.mapBuilder(d,TipoPrestamo.TipoPrestamoBuilder.class).build());
    }

    @Override
    public Mono<TipoPrestamo> findById(Long id) {
        return repository.findById(id)
                .map(entity -> TipoPrestamo.builder()
                        .id_tipo_prestamo(entity.getId_tipo_prestamo())
                        .nombre_tipo_prestamo(entity.getNombre())
                        .monto_minimo(entity.getMonto_minimo())
                        .monto_maximo(entity.getMonto_maximo())
                        .tasa_interes(entity.getTasa_interes())
                        .validacion_automatica(entity.getValidacion_automatica())
                        .build()
                );
    }
}
