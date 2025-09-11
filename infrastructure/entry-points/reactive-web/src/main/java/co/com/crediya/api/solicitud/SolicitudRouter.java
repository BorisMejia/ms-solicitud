package co.com.crediya.api.solicitud;

import co.com.crediya.api.solicitud.dto.request.RegistroSolicitudRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class SolicitudRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "registrarSolicitud",
                    operation = @Operation(
                            operationId = "registrarSolicitud",
                            summary = "Registrar solicitud",
                            security = { @SecurityRequirement(name = "bearerAuth") },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = RegistroSolicitudRequestDto.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Registrada",
                                            content = @Content(schema = @Schema(implementation = RegistroSolicitudRequestDto.class))),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                                    @ApiResponse(responseCode = "404", description = "No encontrado")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routes(SolicitudHandler solicitudHandler) {
        return RouterFunctions
                .route(POST("/api/v1/solicitud"), solicitudHandler::registrarSolicitud)

                ;


    }
}
