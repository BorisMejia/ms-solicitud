package co.com.crediya.api.solicitud;

import co.com.crediya.api.solicitud.dto.request.RegistroSolicitudRequestDto;
import co.com.crediya.api.solicitud.mapper.SolicitudMapperDto;
import co.com.crediya.api.solicitud.validation.ValidarSolicitud;
import co.com.crediya.api.support.ResponseUtils;
import co.com.crediya.model.solicitud.pagination.PageQuery;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    public Mono<ServerResponse> listar(ServerRequest request){
        int page = parseInt(request.queryParam("page").orElse("0"), 0);
        int size = parseInt(request.queryParam("size").orElse("20"), 20);
        String[] sortParts = request.queryParam("sort").orElse("id_solicitud,desc").split(",");
        String sortBy = sortParts[0];
        boolean asc = sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1]);

        PageQuery q = PageQuery.of(page, size, sortBy, asc);

        return useCase.ejecutarPagination(q)
                .flatMap(pr -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(pr));
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
