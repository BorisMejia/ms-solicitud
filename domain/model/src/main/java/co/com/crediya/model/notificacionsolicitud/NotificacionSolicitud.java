package co.com.crediya.model.notificacionsolicitud;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NotificacionSolicitud {
    private Long idSolicitud;
    private String email;
    private String estado;
}
