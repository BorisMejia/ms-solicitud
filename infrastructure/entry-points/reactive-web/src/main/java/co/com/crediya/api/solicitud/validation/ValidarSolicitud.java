package co.com.crediya.api.solicitud.validation;

import co.com.crediya.api.solicitud.dto.request.RegistroSolicitudRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class ValidarSolicitud {

    private final SmartValidator validator;

    public Mono<RegistroSolicitudRequestDto> validate(RegistroSolicitudRequestDto body) {
        var errors = new BeanPropertyBindingResult(body, "registroSolicitudRequest");
        validator.validate(body, errors);
        if (errors.hasErrors()) {
            String msg = errors.getAllErrors().get(0).getDefaultMessage();
            return Mono.error(new IllegalArgumentException(msg));
        }
        return Mono.just(body);
    }
}
