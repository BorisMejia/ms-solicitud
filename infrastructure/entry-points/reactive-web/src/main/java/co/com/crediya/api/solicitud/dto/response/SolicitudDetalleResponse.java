package co.com.crediya.api.solicitud.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record SolicitudDetalleResponse(
    Long id_solicitud,
    String documento,
    String email,
    Long monto,
    Integer plazo_meses,
    String nombre_tipo_prestamo,
    String nombre_estado_solicitud,
    @JsonProperty("name") String nombre_solicitante,
    @JsonProperty("baseSalary") Double salario_base,
    BigDecimal tasa_interes
) {}
