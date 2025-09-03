package co.com.crediya.model.solicitud;
import co.com.crediya.model.estado.EstadoSolicitud;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {

    private Long id_solicitud;
    private String documento;
    private String email;
    private BigDecimal monto;
    private Integer plazo_meses;
    private Long id_tipo_prestamo;
    private EstadoSolicitud estado_solicitud;
}
