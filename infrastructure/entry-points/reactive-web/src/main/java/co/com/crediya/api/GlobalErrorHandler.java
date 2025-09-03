package co.com.crediya.api;

import co.com.crediya.model.solicitud.exception.DomainException;
import co.com.crediya.model.solicitud.exception.NotFoundException;
import co.com.crediya.model.solicitud.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ServerCodecConfigurer codecs;


    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        var status = httpStatus(ex);
        var body = Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", ex.getMessage()
        );

        var response = ServerResponse.status(status).body(BodyInserters.fromValue(body));
        var request = ServerRequest.create(exchange, codecs.getReaders());

        return response.flatMap(res -> res.writeTo(exchange, new HandlerStrategiesResponseContext(
                HandlerStrategies.builder().codecs(c -> c.defaultCodecs()).build()
        )));
    }

    private HttpStatus httpStatus(Throwable ex) {
        if (ex instanceof ValidationException) return HttpStatus.BAD_REQUEST;
        if (ex instanceof NotFoundException) return HttpStatus.NOT_FOUND;
        if (ex instanceof DomainException || ex instanceof IllegalArgumentException) return HttpStatus.BAD_REQUEST;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private record HandlerStrategiesResponseContext(HandlerStrategies strategies) implements ServerResponse.Context {
        @Override public List<HttpMessageWriter<?>> messageWriters() { return strategies.messageWriters(); }
        @Override public List<ViewResolver> viewResolvers() { return strategies.viewResolvers(); }
    }
}
