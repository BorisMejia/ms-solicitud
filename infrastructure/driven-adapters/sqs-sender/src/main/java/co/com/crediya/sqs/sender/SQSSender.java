package co.com.crediya.sqs.sender;

import co.com.crediya.model.notificacionsolicitud.NotificacionSolicitud;
import co.com.crediya.model.notificacionsolicitud.gateways.NotificacionSolicitudRepository;
import co.com.crediya.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements NotificacionSolicitudRepository {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }
    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }
    @Override
    public void notificar(NotificacionSolicitud notificacionSolicitud) {
        try {
            String mensaje = objectMapper.writeValueAsString(notificacionSolicitud);
            client.sendMessage(buildRequest(mensaje))
                    .whenComplete((response, ex) -> {
                        if (ex != null) {
                            log.error("Error enviando mensaje a SQS", ex);
                        } else {
                            log.info("Mensaje enviado a SQS, ID: {}, Body: {}", response.messageId(), mensaje);
                        }
                    });
        } catch (Exception e) {
            log.error("Error serializando mensaje para SQS", e);
        }
    }
}
