package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.EstadoSolicitud;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.notificacionsolicitud.NotificacionSolicitud;
import co.com.crediya.model.notificacionsolicitud.gateways.NotificacionSolicitudRepository;
import co.com.crediya.model.solicitante.gateways.SolicitanteInfoRepository;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.exception.NotFoundException;
import co.com.crediya.model.solicitud.exception.ValidationException;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.usecase.solicitud.dto.request.SolicitudUseCaseDto;
import co.com.crediya.usecase.solicitud.validation.ValidacionSolicitud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class SolicitudUseCaseTest {

    @Mock
    SolicitudRepository solicitudRepository;
    @Mock
    TipoPrestamoRepository tipoPrestamoRepository;
    @Mock
    ValidacionSolicitud validacionSolicitud;
    @Mock
    EstadoRepository estadoRepository;
    @Mock
    SolicitanteInfoRepository solicitanteInfoRepository;
    @Mock
    NotificacionSolicitudRepository notificacionSolicitudRepository;
    private SolicitudUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SolicitudUseCase(solicitudRepository, tipoPrestamoRepository, validacionSolicitud, estadoRepository, solicitanteInfoRepository, notificacionSolicitudRepository );
    }

    private Solicitud validSolicitud() {
        return Solicitud.builder()
                .documento("CC123")
                .email("user@example.com")
                .monto(new BigDecimal("1000000"))
                .plazo_meses(12)
                .id_tipo_prestamo(1L)
                .estado_solicitud(EstadoSolicitud.PENDIENTE_REVISION)
                .build();
    }

    private TipoPrestamo tipo() {
        return TipoPrestamo.builder()
                .id_tipo_prestamo(1L)
                .nombre_tipo_prestamo("Libre Inversión")
                .monto_minimo(new BigDecimal("500000"))
                .monto_maximo(new BigDecimal("2000000"))
                .tasa_interes(new BigDecimal("1.27"))
                .build();
    }


    @Test
    void registrarSolicitud_ok() {
        var s = validSolicitud();
        var t = tipo();
        var dto = new SolicitudUseCaseDto(s.getDocumento(), s.getEmail(), s.getMonto(),
                s.getPlazo_meses(), s.getId_tipo_prestamo());

        when(validacionSolicitud.validarBasica(dto)).thenReturn(Mono.just(dto));
        when(tipoPrestamoRepository.findById(1L)).thenReturn(Mono.just(t));
        when(validacionSolicitud.validarContraTipo(dto, t.getMonto_minimo(), t.getMonto_maximo()))
                .thenReturn(Mono.just(dto));
        // deja que el adapter devuelva lo que recibe (para poder capturarlo)
        when(solicitudRepository.saveSolicitud(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.registrarSolicitud(dto))
                .assertNext(res -> {
                    assertThat(res.getEstado_solicitud()).isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
                    assertThat(res.getMonto()).isEqualByComparingTo(s.getMonto());
                    assertThat(res.getEmail()).isEqualTo(s.getEmail());
                })
                .verifyComplete();

        // interacciones
        verify(validacionSolicitud).validarBasica(dto);
        verify(tipoPrestamoRepository).findById(1L);
        verify(validacionSolicitud).validarContraTipo(dto, t.getMonto_minimo(), t.getMonto_maximo());

        // se guarda con estado forzado a PENDIENTE_REVISION
        ArgumentCaptor<Solicitud> cap = ArgumentCaptor.forClass(Solicitud.class);
        verify(solicitudRepository).saveSolicitud(cap.capture());
        assertThat(cap.getValue().getEstado_solicitud()).isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);

        verifyNoMoreInteractions(solicitudRepository, tipoPrestamoRepository, validacionSolicitud);
    }

    @Test
    void registrarSolicitud_error_enValidacionBasica() {
        var s = validSolicitud();
        var dto = new SolicitudUseCaseDto(s.getDocumento(), s.getEmail(), s.getMonto(),
                s.getPlazo_meses(), s.getId_tipo_prestamo());
        when(validacionSolicitud.validarBasica(dto))
                .thenReturn(Mono.error(new ValidationException("El monto debe ser mayor que 0")));

        StepVerifier.create(useCase.registrarSolicitud(dto))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("El monto debe ser mayor que 0");
                })
                .verify();

        verify(validacionSolicitud).validarBasica(dto);
        verifyNoInteractions(tipoPrestamoRepository);
        verifyNoInteractions(solicitudRepository);
    }

    @Test
    void registrarSolicitud_error_tipoPrestamoNoExiste() {
        var s = validSolicitud();
        var dto = new SolicitudUseCaseDto(s.getDocumento(), s.getEmail(), s.getMonto(),
                s.getPlazo_meses(), s.getId_tipo_prestamo());
        when(validacionSolicitud.validarBasica(dto)).thenReturn(Mono.just(dto));
        when(tipoPrestamoRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.registrarSolicitud(dto))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(NotFoundException.class);
                    assertThat(ex.getMessage()).isEqualTo("El tipo de prestamo no existe");
                })
                .verify();

        verify(validacionSolicitud).validarBasica(dto);
        verify(tipoPrestamoRepository).findById(1L);
        verifyNoInteractions(solicitudRepository);
    }

    @Test
    void registrarSolicitud_error_validacionContraTipo() {
        var s = validSolicitud();
        var t = tipo();
        var dto = new SolicitudUseCaseDto(s.getDocumento(), s.getEmail(), s.getMonto(),
                s.getPlazo_meses(), s.getId_tipo_prestamo());

        when(validacionSolicitud.validarBasica(dto)).thenReturn(Mono.just(dto));
        when(tipoPrestamoRepository.findById(1L)).thenReturn(Mono.just(t));
        when(validacionSolicitud.validarContraTipo(dto, t.getMonto_minimo(), t.getMonto_maximo()))
                .thenReturn(Mono.error(new ValidationException("Monto mayor al máximo permitido")));

        StepVerifier.create(useCase.registrarSolicitud(dto))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("Monto mayor al máximo permitido");
                })
                .verify();

        verify(validacionSolicitud).validarBasica(dto);
        verify(tipoPrestamoRepository).findById(1L);
        verify(validacionSolicitud).validarContraTipo(dto, t.getMonto_minimo(), t.getMonto_maximo());
        verifyNoInteractions(solicitudRepository);
    }
}
