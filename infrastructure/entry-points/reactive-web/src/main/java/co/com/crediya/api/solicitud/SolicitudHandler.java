package co.com.crediya.api.solicitud;

import co.com.crediya.api.solicitud.dto.request.RegistroSolicitudRequestDto;
import co.com.crediya.api.solicitud.mapper.SolicitudMapperDto;
import co.com.crediya.api.solicitud.validation.ValidarSolicitud;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class SolicitudHandler {

    private final SolicitudUseCase useCase;
    private final SolicitudMapperDto mapperDto;
    private final ValidarSolicitud validarSolicitud;



    public Mono<ServerResponse> registrarSolicitud(ServerRequest req){
        return req.bodyToMono(RegistroSolicitudRequestDto.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El cuerpo de la petición es requerido")))
                .flatMap(validarSolicitud::validate)
                .map(mapperDto::toDomain)
                .flatMap(useCase::registrarSolicitud)
                .map(mapperDto::toResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }
}
