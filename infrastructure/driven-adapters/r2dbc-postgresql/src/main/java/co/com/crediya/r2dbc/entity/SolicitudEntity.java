package co.com.crediya.r2dbc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("solicitud")
@Data
public class SolicitudEntity {

    @Id
    private Long id_solicitud;
    private String documento;
    private String email;
    private BigDecimal monto;
    private Integer plazo_meses;
    private Long id_tipo_prestamo;
    private Long id_estado;
}
