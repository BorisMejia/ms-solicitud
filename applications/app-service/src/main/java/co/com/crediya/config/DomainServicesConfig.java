package co.com.crediya.config;

import co.com.crediya.usecase.solicitud.validation.ValidacionSolicitud;
import co.com.crediya.usecase.solicitud.validation.ValidacionSolicitudBasic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServicesConfig {

    @Bean
    public ValidacionSolicitud validacionSolicitud(){
        return new ValidacionSolicitudBasic();
    }
}
