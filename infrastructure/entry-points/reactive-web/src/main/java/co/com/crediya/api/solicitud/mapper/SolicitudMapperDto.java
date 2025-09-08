package co.com.crediya.api.solicitud.mapper;

import co.com.crediya.api.solicitud.dto.request.RegistroSolicitudRequestDto;
import co.com.crediya.api.solicitud.dto.response.RegistrarSolicitudResponse;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.usecase.solicitud.dto.request.SolicitudUseCaseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SolicitudMapperDto {

    @Mappings({
            @Mapping(target = "email", expression = "java(emailFromToken)"),
               @Mapping(target = "documento", expression = "java(documentoFromToken)")
    })
    SolicitudUseCaseDto toUseCae(RegistroSolicitudRequestDto registroSolicitud, String emailFromToken, String documentoFromToken);


    RegistrarSolicitudResponse toResponse(Solicitud solicitud);
}
