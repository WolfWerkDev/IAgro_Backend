-- =========================================================
-- V1__init.sql
-- Inicialización del esquema de base de datos para IAgro
-- =========================================================

-- ==============================
-- TABLA: planes
-- ==============================
CREATE TABLE planes (
    id_plan BIGINT PRIMARY KEY,
    tipo_plan VARCHAR(50) NOT NULL,
    descripcion TEXT NOT NULL,
    precio_mensual FLOAT NOT NULL,
    max_fincas INT,
    max_agrolinks_por_finca INT,
    max_mensajes_chat INT,
    variables_disponibles TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    es_activo BOOLEAN DEFAULT TRUE
);

-- ==============================
-- TABLA: usuarios
-- ==============================
CREATE TABLE usuarios (
    id_usuario BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    fecha_nacimiento DATE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    telefono VARCHAR(20),
    correo VARCHAR(255) UNIQUE NOT NULL,
    contraseña VARCHAR(255) NOT NULL,
    foto_perfil VARCHAR(255) DEFAULT 'fotoPerfilDefault.png',
    fecha_inicio_plan TIMESTAMP,
    fecha_fin_plan TIMESTAMP,
    es_activo BOOLEAN DEFAULT TRUE,
    es_admin BOOLEAN DEFAULT FALSE,
    id_plan BIGINT REFERENCES planes(id_plan)
);

-- ==============================
-- TABLA: superusuarios
-- ==============================
CREATE TABLE superusuarios (
    id_super_usuario BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    codigo VARCHAR(100)
);

-- ==============================
-- TABLA: fincas
-- ==============================
CREATE TABLE fincas (
    id_finca BIGSERIAL PRIMARY KEY,
    nombre_finca VARCHAR(255) DEFAULT 'Mi finca',
    ubicacion TEXT,
    tipo_de_cultivo VARCHAR(100),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    foto_finca VARCHAR(255) DEFAULT 'FotoFincaDefault.png',
    id_usuario BIGINT REFERENCES usuarios(id_usuario)
);

-- ==============================
-- TABLA: agrolinks
-- ==============================
CREATE TABLE agrolinks (
    codigo VARCHAR(50) PRIMARY KEY,
    nombre_agrolink VARCHAR(255) DEFAULT 'Mi Agrolink',
    es_vinculado BOOLEAN DEFAULT FALSE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_vinculacion TIMESTAMP,
    id_finca BIGINT REFERENCES fincas(id_finca)
);

-- ==============================
-- TABLA: reportes
-- ==============================
CREATE TABLE reportes (
    id_reporte BIGSERIAL PRIMARY KEY,
    codigo_agrolink VARCHAR(50) REFERENCES agrolinks(codigo),
    temperatura_ambiente FLOAT,
    temperatura_suelo FLOAT,
    humedad_suelo FLOAT,
    ph_suelo FLOAT,
    conductividad_suelo FLOAT,
    fecha_reporte TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==============================
-- TABLA: chats
-- ==============================
CREATE TABLE chats (
    id_chat BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT REFERENCES usuarios(id_usuario),
    nombre_chat VARCHAR(255),
    fehca_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==============================
-- TABLA: mensajes
-- ==============================
CREATE TABLE mensajes (
    id_mensaje BIGSERIAL PRIMARY KEY,
    id_chat BIGINT REFERENCES chats(id_chat),
    contenido TEXT NOT NULL,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    emisor_mensaje VARCHAR(50)
);

-- =========================================================
-- DATOS INICIALES - PLANES
-- =========================================================
INSERT INTO planes (
    id_plan, tipo_plan, descripcion, precio_mensual, max_fincas,
    max_agrolinks_por_finca, max_mensajes_chat, variables_disponibles,
    fecha_creacion, es_activo
) VALUES
-- 🟩 FREE
(1, 'FREE', 'Plan gratuito con acceso limitado al chatbot de IAgro.',
 0, 0, 0, 10, NULL, CURRENT_TIMESTAMP, TRUE),

-- 🟨 BASICO
(2, 'BASICO', 'Plan básico con capacidad para una finca y hasta dos Agrolinks.',
 30000, 1, 2, 50, 'Temperatura del suelo, humedad del suelo', CURRENT_TIMESTAMP, TRUE),

-- 🟦 PREMIUM
(3, 'PREMIUM', 'Plan premium con acceso completo y sin límites de uso.',
 70000, NULL, NULL, NULL,
 'Temperatura ambiente, temperatura del suelo, humedad del suelo, pH del suelo, conductividad del suelo',
 CURRENT_TIMESTAMP, TRUE);
