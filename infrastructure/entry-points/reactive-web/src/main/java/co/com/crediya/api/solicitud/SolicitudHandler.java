package co.com.crediya.api.solicitud;

import co.com.crediya.api.solicitud.dto.request.RegistroSolicitudRequestDto;
import co.com.crediya.api.solicitud.mapper.SolicitudMapperDto;
import co.com.crediya.api.support.ResponseUtils;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import co.com.crediya.usecase.solicitud.dto.request.SolicitudUseCaseDto;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
    private final SmartValidator validator;



    public Mono<ServerResponse> registrarSolicitud(ServerRequest req){
        return req.principal()
                .cast(JwtAuthenticationToken.class)
                .flatMap(auth -> {
                    String emailFromToken = auth.getToken().getSubject();
                    String documento = (String) auth.getTokenAttributes().get("doc");
                    return req.bodyToMono(RegistroSolicitudRequestDto.class)
                            .map(dto -> mapperDto.toUseCae(dto, emailFromToken, documento))
                            .flatMap(useCase::registrarSolicitud)
                            .map(mapperDto::toResponse)
                            .flatMap(ResponseUtils::createdJson);
                });


    }

    private Mono<RegistroSolicitudRequestDto> validate(RegistroSolicitudRequestDto body) {
        var errors = new BeanPropertyBindingResult(body, "registroSolicitudRequest");
        validator.validate(body, errors);
        if (errors.hasErrors()) {
            String msg = errors.getAllErrors().get(0).getDefaultMessage();
            return Mono.error(new IllegalArgumentException(msg));
        }
        return Mono.just(body);
    }
}
