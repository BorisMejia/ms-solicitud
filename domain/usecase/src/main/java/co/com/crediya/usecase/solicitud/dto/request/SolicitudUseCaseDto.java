package co.com.crediya.usecase.solicitud.dto.request;

import java.math.BigDecimal;

public record SolicitudUseCaseDto(
        String documento,
        String email,
        BigDecimal monto,
        Integer plazo_meses,
        Long id_tipo_prestamo
){
}
