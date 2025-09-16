package co.com.crediya.usecase.solicitud.dto.response;

import java.math.BigDecimal;

public record SolicitudInfo(
    Long id_solicitud,
    String documento,
    String email,
    BigDecimal monto,
    Integer plazo_meses,
    String nombre_tipo_prestamo,
    String nombre_estado_solicitud,
    String nombre_solicitante,
    Double salario_base,
    BigDecimal tasa_interes
) {}
