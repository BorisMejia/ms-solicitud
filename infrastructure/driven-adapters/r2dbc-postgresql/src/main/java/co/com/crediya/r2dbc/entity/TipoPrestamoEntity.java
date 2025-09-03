package co.com.crediya.r2dbc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Table("tipo_prestamo")
public class TipoPrestamoEntity {

    @Id
    private Long id_tipo_prestamo;
    private String nombre;
    private BigDecimal monto_minimo;
    private BigDecimal monto_maximo;
    private BigDecimal tasa_interes;
    private Boolean validacion_automatica;
}
