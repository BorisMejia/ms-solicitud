package co.com.crediya.api.support;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public final class ResponseUtils {
    private ResponseUtils(){}

    public static Mono<ServerResponse> okJson(Object body) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
    }

    public static Mono<ServerResponse> createdJson(Object body) {
        return ServerResponse.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
    }
}

