package co.com.crediya.api.security.securityHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SecurityHandlers {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, ex) -> writeJson(exchange, HttpStatus.UNAUTHORIZED,
                Map.of(
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "error", HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        "message", "No autenticado: inicia sesión"
                ));
    }

    public static ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, ex) -> writeJson(exchange, HttpStatus.FORBIDDEN,
                Map.of(
                        "status", HttpStatus.FORBIDDEN.value(),
                        "error", HttpStatus.FORBIDDEN.getReasonPhrase(),
                        "message", "Acceso denegado"
                ));
    }

    private static Mono<Void> writeJson(ServerWebExchange exchange, HttpStatus status, Map<String, Object> body) {
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().setAcceptCharset(java.util.List.of(StandardCharsets.UTF_8));
        try {
            byte[] bytes = MAPPER.writeValueAsBytes(body);
            var buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return response.setComplete();
        }
    }
}
