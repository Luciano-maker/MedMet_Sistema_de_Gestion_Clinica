-- ========================================================
-- SISTEMA DE GESTIÓN CLÍNICA: MedMet
-- ========================================================

-- Creacion Base de datos 
CREATE DATABASE IF NOT EXISTS SistemaMedMetDB;
USE SistemaMedMetDB;

-- Creacion Tabla Persona
CREATE TABLE persona (
    dni VARCHAR(10) PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    apellido VARCHAR(60) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(80),
    direccion VARCHAR(120)
);

-- Creacion Tabla Usuario
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(30) NOT NULL UNIQUE,
    contrasenia VARCHAR(50) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

-- Creacion Tabla Especialidad
CREATE TABLE especialidad (
    id_especialidad INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    activo BOOLEAN DEFAULT TRUE
);

-- Creacion Tabla Recepcionista
CREATE TABLE recepcionista (
    id_recepcionista INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(10) NOT NULL UNIQUE,
    fecha_ingreso DATE NOT NULL,
    id_usuario INT NOT NULL UNIQUE,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (dni) REFERENCES persona(dni),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- Creacion Tabla Medico
CREATE TABLE medico (
    id_medico INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(10) NOT NULL UNIQUE,
    matricula_profesional VARCHAR(20) NOT NULL UNIQUE,
    id_especialidad INT NOT NULL,
    id_usuario INT NOT NULL UNIQUE,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (dni) REFERENCES persona(dni),
    FOREIGN KEY (id_especialidad) REFERENCES especialidad(id_especialidad),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- Creacion Tabla Paciente
CREATE TABLE paciente (
    id_paciente INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(10) NOT NULL UNIQUE,
    fecha_nacimiento DATE NOT NULL,
    obra_social VARCHAR(60) DEFAULT 'Particular',
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (dni) REFERENCES persona(dni)
);

-- Creacion Tabla Turno
CREATE TABLE turno (
    id_turno INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente INT NOT NULL,
    id_medico INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    estado VARCHAR(20) DEFAULT 'Pendiente',
    estado_pago VARCHAR(20) DEFAULT 'Pendiente',
    FOREIGN KEY (id_paciente) REFERENCES paciente(id_paciente),
    FOREIGN KEY (id_medico) REFERENCES medico(id_medico)
);

-- Creacion Tabla Pago
CREATE TABLE pago (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_turno INT NOT NULL UNIQUE,
    fecha_hora DATETIME,
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(30) NOT NULL,
    FOREIGN KEY (id_turno) REFERENCES turno(id_turno)
);

-- Creacion Tabla Historia Clinica
CREATE TABLE historia_clinica (
    id_historia_clinica INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente INT NOT NULL UNIQUE,
    fecha_apertura DATE NOT NULL,
    observaciones_iniciales TEXT,
    FOREIGN KEY (id_paciente) REFERENCES paciente(id_paciente)
);

-- Creacion Tabla Evolucion medica
CREATE TABLE evolucion_medica (
    id_evolucion INT AUTO_INCREMENT PRIMARY KEY,
    id_historia_clinica INT NOT NULL,
    id_medico INT NOT NULL,
    fecha_hora DATETIME,
    motivo_consulta VARCHAR(255) NOT NULL,
    diagnostico TEXT NOT NULL,
    tratamiento TEXT,
    FOREIGN KEY (id_historia_clinica) REFERENCES historia_clinica(id_historia_clinica),
    FOREIGN KEY (id_medico) REFERENCES medico(id_medico)
);

-- ===================
-- INSERCION DE DATOS
-- ===================
USE sistemamedmetdb;

-- Inserción en PERSONA (1 Recepcionista, 3 Médicos, 3 Pacientes)
INSERT INTO persona (dni, nombre, apellido, telefono, email, direccion) VALUES
-- Recepcionista
('32111222', 'Laura', 'Gómez', '3876421001', 'laura.gomez@medmet.com', 'Av. 25 de Mayo 340'),
-- Médicos
('28444555', 'Marcos', 'Fernández', '3876422002', 'm.fernandez@medmet.com', 'Belgrano 120'),
('30777888', 'Valeria', 'Pérez', '3876423003', 'v.perez@medmet.com', 'Mitre 550'),
('26123987', 'Andrea', 'Ruiz', '3876424004', 'a.ruiz@medmet.com', 'San Martín 820'),
-- Pacientes
('43686992', 'Luciano', 'Ruiz Jiménez', '3876425005', 'luciano.ruiz@gmail.com', 'Güemes 150'),
('35123456', 'Javier', 'Gómez', '3876426006', 'javier.gomez@hotmail.com', 'Alberdi 410'),
('28990112', 'Marina', 'Sosa', '3876427007', 'marina.sosa@yahoo.com', 'Rivadavia 930');

-- Inserción en USUARIO (1 Recepcionista, 3 Médicos)
INSERT INTO usuario (nombre_usuario, contrasenia, rol, activo) VALUES
('recepcion_central', 'Admin2026*', 'RECEPCIONISTA', TRUE),
('dr_fernandez', 'DocCardio26', 'MEDICO', TRUE),
('dra_perez', 'DocGen2026', 'MEDICO', TRUE),
('dra_ruiz', 'DocOdonto26', 'MEDICO', TRUE);

-- Inserción en ESPECIALIDAD (3 registros)
INSERT INTO especialidad (nombre, descripcion, activo) VALUES
('Cardiología', 'Evaluación preventiva, diagnóstico y control cardiovascular', TRUE),
('Medicina General', 'Atención médica primaria, clínica general y derivaciones', TRUE),
('Odontología', 'Salud bucal integral, limpiezas y tratamientos odontológicos', TRUE);

-- Inserción en RECEPCIONISTA (1 registro solicitado)
INSERT INTO recepcionista (dni, fecha_ingreso, id_usuario, activo) VALUES
('32111222', '2024-03-01', 1, TRUE);

-- Inserción en MEDICO (3 registros)
INSERT INTO medico (dni, matricula_profesional, id_especialidad, id_usuario, activo) VALUES
('28444555', 'MP-5842', 1, 2, TRUE), -- Dr. Marcos Fernández en Cardiología
('30777888', 'MP-6120', 2, 3, TRUE), -- Dra. Valeria Pérez en Medicina General
('26123987', 'MP-4399', 3, 4, TRUE); -- Dra. Andrea Ruiz en Odontología

-- Inserción en PACIENTE (3 registros)
INSERT INTO paciente (dni, fecha_nacimiento, obra_social, activo) VALUES
('43686992', '2002-02-22', 'Particular', TRUE),
('35123456', '1990-06-15', 'OSDE', TRUE),
('28990112', '1981-11-04', 'INSSJP (PAMI)', TRUE);

-- Inserción en TURNO (3 registros)
INSERT INTO turno (id_paciente, id_medico, fecha, hora, estado, estado_pago) VALUES
(1, 1, '2026-09-29', '08:30:00', 'Atendido', 'PAGADO'),
(2, 2, '2026-09-29', '09:00:00', 'En Espera', 'PAGADO'),
(3, 3, '2026-09-29', '09:30:00', 'Pendiente', 'PENDIENTE');

-- Inserción en PAGO (3 registros)
INSERT INTO pago (id_turno, fecha_hora, monto, metodo_pago) VALUES
(1, '2026-09-29 08:20:00', 8500.00, 'Efectivo'),
(2, '2026-09-29 08:50:00', 12000.00, 'Transferencia'),
(3, '2026-09-29 09:15:00', 9500.00, 'Debito');

-- Inserción en HISTORIA_CLINICA (3 registros)
INSERT INTO historia_clinica (id_paciente, fecha_apertura, observaciones_iniciales) VALUES
(1, '2026-09-28', 'Paciente sin antecedentes patológicos relevantes declarados.'),
(2, '2026-09-28', 'Paciente con antecedentes de hipertensión arterial leve.'),
(3, '2026-09-28', 'Paciente en seguimiento odontológico periódico.');

-- Inserción en EVOLUCION_MEDICA (3 registros)
INSERT INTO evolucion_medica (id_historia_clinica, id_medico, fecha_hora, motivo_consulta, diagnostico, tratamiento) VALUES
(1, 1, '2026-09-29 08:45:00', 'Control cardiovascular de rutina', 'Ritmo regular sin soplos.', 'Mantener control preventivo anual.'),
(2, 2, '2026-09-29 09:20:00', 'Chequeo clínico general', 'Examen físico.', 'Solicitud de laboratorio de sangre de rutina en ayunas.'),
(3, 3, '2026-09-29 09:50:00', 'Molestia en pieza dental molar', 'Gingivitis localizada.', 'Limpieza dental profunda.');


-- Consultar agenda de turnos
SELECT 
    t.id_turno,
    t.fecha,
    t.hora,
    CONCAT(pp.apellido, ', ', pp.nombre) AS paciente,
    pp.dni AS dni_paciente,
    CONCAT(pm.apellido, ', ', pm.nombre) AS medico,
    e.nombre AS especialidad,
    t.estado AS estado_turno,
    t.estado_pago
FROM turno t
INNER JOIN paciente pac ON t.id_paciente = pac.id_paciente
INNER JOIN persona pp ON pac.dni = pp.dni
INNER JOIN medico m ON t.id_medico = m.id_medico
INNER JOIN persona pm ON m.dni = pm.dni
INNER JOIN especialidad e ON m.id_especialidad = e.id_especialidad;

-- Consultar pagos realizados
SELECT 
    p.id_pago,
    p.id_turno,
    p.fecha_hora,
    p.monto,
    p.metodo_pago,
    CONCAT(per.apellido, ', ', per.nombre) AS paciente
FROM pago p
INNER JOIN turno t ON p.id_turno = t.id_turno
INNER JOIN paciente pac ON t.id_paciente = pac.id_paciente
INNER JOIN persona per ON pac.dni = per.dni;

-- Consultar historias clínicas y evoluciones médicas
SELECT 
    hc.id_historia_clinica,
    CONCAT(per.apellido, ', ', per.nombre) AS paciente,
    em.fecha_hora AS fecha_atencion,
    em.motivo_consulta,
    em.diagnostico,
    em.tratamiento,
    CONCAT(med_per.apellido, ', ', med_per.nombre) AS medico_actuante
FROM historia_clinica hc
INNER JOIN paciente pac ON hc.id_paciente = pac.id_paciente
INNER JOIN persona per ON pac.dni = per.dni
INNER JOIN evolucion_medica em ON hc.id_historia_clinica = em.id_historia_clinica
INNER JOIN medico med ON em.id_medico = med.id_medico
INNER JOIN persona med_per ON med.dni = med_per.dni;

-- === Borrar Registro de Especialidad ===
-- Consultar tabla Especialidad
SELECT id_especialidad, nombre, activo FROM especialidad WHERE activo = TRUE;
-- Dar de baja lógica a una Especialidad
UPDATE especialidad 
SET activo = FALSE 
WHERE id_especialidad = 3;
-- Verficar baja
SELECT id_especialidad, nombre, activo FROM especialidad WHERE id_especialidad = 3;