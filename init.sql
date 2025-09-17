-- Tabla tipo_prestamo
CREATE TABLE IF NOT EXISTS tipo_prestamo (
    id_tipo_prestamo INTEGER PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    monto_minimo NUMERIC NOT NULL,
    monto_maximo NUMERIC NOT NULL,
    tasa_interes NUMERIC NOT NULL,
    validacion_automatica BOOLEAN NOT NULL
);

INSERT INTO tipo_prestamo (id_tipo_prestamo, nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica) VALUES
    (2, 'Educativo', 500000, 20000000, 14.5, TRUE),
    (1, 'Libre inversión', 1000000, 50000000, 23.9, TRUE),
    (3, 'Vehículo', 5000000, 120000000, 17.75, TRUE)
ON CONFLICT (id_tipo_prestamo) DO NOTHING;

-- Tabla estados
CREATE TABLE IF NOT EXISTS estados (
    id_estado INTEGER PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(100)
);

INSERT INTO estados (id_estado, nombre, descripcion) VALUES
    (1, 'PENDIENTE_REVISION', 'Pendiente de revisión'),
    (2, 'REVISION_MANUAL', 'Requiere revisión manual'),
    (3, 'RECHAZADO', 'Rechazado'),
    (4, 'APROBADO', 'Aprobado')
ON CONFLICT (id_estado) DO NOTHING;

-- Tabla solicitud
CREATE TABLE IF NOT EXISTS solicitud (
    id_solicitud SERIAL PRIMARY KEY,
    documento VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    monto NUMERIC NOT NULL,
    plazo_meses INTEGER NOT NULL,
    id_tipo_prestamo INTEGER REFERENCES tipo_prestamo(id_tipo_prestamo),
    id_estado INTEGER REFERENCES estados(id_estado)
);
