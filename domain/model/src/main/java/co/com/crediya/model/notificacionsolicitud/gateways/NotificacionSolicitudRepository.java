package co.com.crediya.model.notificacionsolicitud.gateways;

import co.com.crediya.model.notificacionsolicitud.NotificacionSolicitud;

public interface NotificacionSolicitudRepository {
    void notificar(NotificacionSolicitud notificacionSolicitud);
}
