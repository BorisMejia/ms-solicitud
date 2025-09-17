package co.com.crediya.model.estado;

public enum EstadoSolicitud {
    PENDIENTE_REVISION(1),
    REVISION_MANUAL(2),
    RECHAZADO(3),
    APROBADO(4);

    private final int id;

    EstadoSolicitud(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static EstadoSolicitud fromId(int id) {
        for (EstadoSolicitud estado : values()) {
            if (estado.id == id) return estado;
        }
        throw new IllegalArgumentException("Id de estado inválido: " + id);
    }
}
