package co.com.crediya.api.solicitud.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record RegistroSolicitudRequestDto(

        @NotNull @DecimalMin(value = "0.01")
        BigDecimal monto,
        @NotNull @Min(1)
        Integer plazo_meses,
        @NotNull @Schema(example = "1: Libre inversion, 2: Educativo, 3: Vehiculo")
        Long id_tipo_prestamo
) {
}
