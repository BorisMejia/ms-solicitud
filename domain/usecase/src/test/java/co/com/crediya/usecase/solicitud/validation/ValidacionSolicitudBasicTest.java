package co.com.crediya.usecase.solicitud.validation;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidacionSolicitudBasicTest {

    private ValidacionSolicitudBasic validator;

    @BeforeEach
    void setUp() {
        validator = new ValidacionSolicitudBasic();
    }

    private Solicitud valid() {
        return Solicitud.builder()
                .documento("CC123")
                .email("user@example.com")
                .monto(new BigDecimal("1000000"))
                .plazo_meses(12)
                .id_tipo_prestamo(1L)
                .build();
    }

    @Test
    void validarBasica_ok() {
        var s = valid();

        StepVerifier.create(validator.validarBasica(s))
                .expectNextMatches(out -> {
                    // devuelve el mismo objeto sin modificar
                    assertThat(out).isSameAs(s);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void validarBasica_error_montoNull() {
        var s = valid().toBuilder().monto(null).build();

        StepVerifier.create(validator.validarBasica(s))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("El monto debe ser mayor que 0");
                })
                .verify();
    }

    @Test
    void validarBasica_error_montoCero() {
        var s = valid().toBuilder().monto(BigDecimal.ZERO).build();

        StepVerifier.create(validator.validarBasica(s))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("El monto debe ser mayor que 0");
                })
                .verify();
    }

    @Test
    void validarBasica_error_plazoNull() {
        var s = valid().toBuilder().plazo_meses(null).build();

        StepVerifier.create(validator.validarBasica(s))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("El plazo en meses debe ser mayor que 0");
                })
                .verify();
    }

    @Test
    void validarBasica_error_plazoNoPositivo() {
        var s = valid().toBuilder().plazo_meses(0).build();

        StepVerifier.create(validator.validarBasica(s))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("El plazo en meses debe ser mayor que 0");
                })
                .verify();
    }

    @Test
    void validarBasica_error_documentoNull() {
        var s = valid().toBuilder().documento(null).build();

        StepVerifier.create(validator.validarBasica(s))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("El documento es requerido");
                })
                .verify();
    }

    @Test
    void validarBasica_error_documentoBlank() {
        var s = valid().toBuilder().documento("   ").build();

        StepVerifier.create(validator.validarBasica(s))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("El documento es requerido");
                })
                .verify();
    }

    @Test
    void validarBasica_error_tipoPrestamoNull() {
        var s = valid().toBuilder().id_tipo_prestamo(null).build();

        StepVerifier.create(validator.validarBasica(s))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("El tipo de prestamo es requerido");
                })
                .verify();
    }

    // ===================== validarContraTipo =====================

    @Test
    void validarContraTipo_ok_sinLimites() {
        var s = valid();

        StepVerifier.create(validator.validarContraTipo(s, null, null))
                .expectNext(s)
                .verifyComplete();
    }

    @Test
    void validarContraTipo_ok_enLimitesIguales() {
        var s = valid().toBuilder().monto(new BigDecimal("500000")).build();

        StepVerifier.create(validator.validarContraTipo(s, new BigDecimal("500000"), new BigDecimal("500000")))
                .expectNext(s)
                .verifyComplete();
    }

    @Test
    void validarContraTipo_error_menorQueMinimo() {
        var s = valid().toBuilder().monto(new BigDecimal("500000")).build();

        StepVerifier.create(validator.validarContraTipo(s, new BigDecimal("600000"), null))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("Monto menor al mínimo permitido");
                })
                .verify();
    }

    @Test
    void validarContraTipo_error_mayorQueMaximo() {
        var s = valid().toBuilder().monto(new BigDecimal("700000")).build();

        StepVerifier.create(validator.validarContraTipo(s, null, new BigDecimal("650000")))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("Monto mayor al máximo permitido");
                })
                .verify();
    }
}
