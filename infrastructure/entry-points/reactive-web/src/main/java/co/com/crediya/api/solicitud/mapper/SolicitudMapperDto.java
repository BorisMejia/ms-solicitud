package co.com.crediya.api.solicitud.mapper;

import co.com.crediya.api.solicitud.dto.request.RegistroSolicitudRequestDto;
import co.com.crediya.api.solicitud.dto.response.RegistrarSolicitudResponse;
import co.com.crediya.model.solicitud.Solicitud;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SolicitudMapperDto {


    Solicitud toDomain(RegistroSolicitudRequestDto registroSolicitud);


    RegistrarSolicitudResponse toResponse(Solicitud solicitud);
}
