package co.com.crediya.api.solicitud;

import co.com.crediya.api.solicitud.dto.request.RegistroSolicitudRequestDto;
import co.com.crediya.api.solicitud.mapper.SolicitudMapperDto;
import co.com.crediya.api.solicitud.validation.ValidarSolicitud;
import co.com.crediya.api.support.ResponseUtils;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
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
        return req.principal()
                .cast(JwtAuthenticationToken.class)
                .flatMap(auth -> {
                    String emailFromToken = auth.getToken().getSubject();
                    String documento = (String) auth.getTokenAttributes().get("doc");
                    return req.bodyToMono(RegistroSolicitudRequestDto.class)
                            .flatMap(validarSolicitud::validate)
                            .map(dto -> mapperDto.toUseCae(dto, emailFromToken, documento))
                            .flatMap(useCase::registrarSolicitud)
                            .map(mapperDto::toResponse)
                            .flatMap(ResponseUtils::createdJson);
                });

    }
}
