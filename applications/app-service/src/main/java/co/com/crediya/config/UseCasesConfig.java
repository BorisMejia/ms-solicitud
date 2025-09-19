package co.com.crediya.config;

import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.notificacionsolicitud.gateways.NotificacionSolicitudRepository;
import co.com.crediya.model.solicitante.gateways.SolicitanteInfoRepository;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.usecase.solicitud.validation.ValidacionSolicitud;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.crediya.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

        @Bean
        public SolicitudUseCase solicitudUseCase(
                SolicitudRepository solicitudRepository,
                TipoPrestamoRepository tipoPrestamoRepository,
                ValidacionSolicitud validacionSolicitud,
                EstadoRepository estadoRepository,
                SolicitanteInfoRepository solicitanteInfoRepository,
                NotificacionSolicitudRepository notificacionSolicitudRepository
        ){
                return new SolicitudUseCase(solicitudRepository, tipoPrestamoRepository, validacionSolicitud, estadoRepository, solicitanteInfoRepository, notificacionSolicitudRepository);
        }

}
