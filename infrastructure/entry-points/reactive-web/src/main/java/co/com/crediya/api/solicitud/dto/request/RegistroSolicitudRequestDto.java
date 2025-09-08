package co.com.crediya.api.solicitud.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record RegistroSolicitudRequestDto(

        @NotNull @DecimalMin(value = "0.01")
        BigDecimal monto,
        @NotNull @Min(1)
        Integer plazo_meses,
        @NotNull
        Long id_tipo_prestamo
) {
}
