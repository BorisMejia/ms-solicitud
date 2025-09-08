package co.com.crediya.model.tipoprestamo;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TipoPrestamo {

    private Long id_tipo_prestamo;
    private String nombre_tipo_prestamo;
    private BigDecimal monto_minimo;
    private BigDecimal monto_maximo;
    private BigDecimal tasa_interes;
    private Boolean validacion_automatica;
}
