package co.com.crediya.r2dbc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("estados")
@Data
public class EstadoSolicitudEntity {
    @Id
    private Long id_estado;
    private String nombre;
    private String descripcion;
}
