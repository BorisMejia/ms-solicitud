package co.com.crediya.r2dbc.estado;

import co.com.crediya.r2dbc.entity.EstadoSolicitudEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

// TODO: This file is just an example, you should delete or modify it
public interface EstadoSolicitudReactiveRepository extends ReactiveCrudRepository<EstadoSolicitudEntity, Long>, ReactiveQueryByExampleExecutor<EstadoSolicitudEntity> {
}
