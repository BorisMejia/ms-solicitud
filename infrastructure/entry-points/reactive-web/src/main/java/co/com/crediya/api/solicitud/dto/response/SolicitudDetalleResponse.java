package co.com.crediya.api.solicitud.dto.response;

public record SolicitudDetalleResponse(
    Long id_solicitud,
    String documento,
    String email,
    Long monto,
    Integer plazo_meses,
    String nombre_tipo_prestamo,
    String nombre_estado_solicitud
) {}
