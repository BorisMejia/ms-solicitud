package co.com.crediya.usecase.solicitud.dto.request;

import java.math.BigDecimal;

public record SolicitudUseCase (
        String documento,
        String email,
        BigDecimal monto,
        Integer plazoMeses,

        Long idTipoPrestamo
){
}
