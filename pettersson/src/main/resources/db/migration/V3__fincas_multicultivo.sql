-- ============================================
-- MIGRACIÓN V3: Tabla de Fincas con múltiples cultivos
-- ============================================

-- 1. Crear tabla intermedia para tipos de cultivo
CREATE TABLE IF NOT EXISTS finca_tipos_de_cultivo (
    id SERIAL PRIMARY KEY,
    finca_id BIGINT NOT NULL REFERENCES fincas(id_finca) ON DELETE CASCADE,
    tipo_de_cultivo VARCHAR(100) NOT NULL
);

-- 2. Opcional: migrar datos existentes si tienes tipos únicos en fincas.tipo_de_cultivo
-- Esto copia los valores actuales a la nueva tabla intermedia
INSERT INTO finca_tipos_de_cultivo (finca_id, tipo_de_cultivo)
SELECT id_finca, tipo_de_cultivo
FROM fincas
WHERE tipo_de_cultivo IS NOT NULL;

-- 3. Ya que ahora los cultivos están en la tabla intermedia, podemos eliminar la columna anterior
ALTER TABLE fincas
DROP COLUMN tipo_de_cultivo;

-- 4. Verificación
-- Para ver los cultivos de cada finca
SELECT f.id_finca, f.nombre_finca, ft.tipo_de_cultivo
FROM fincas f
LEFT JOIN finca_tipos_de_cultivo ft ON f.id_finca = ft.finca_id
ORDER BY f.id_finca;
