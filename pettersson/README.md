# 🌱 IAgro Backend – Sistema Inteligente de Monitoreo Agrícola

**IAgro** es una plataforma web inteligente que integra **IoT, análisis de datos e inteligencia artificial** para optimizar procesos agrícolas como el **riego y la fumigación**, mejorando la toma de decisiones en tiempo real.

---

## 🚀 Tecnologías principales

* **Java 17**
* **Spring Boot 3.5.6**
* **Spring Data JPA**
* **Spring Security**
* **PostgreSQL**
* **Flyway**
* **Maven**
* **Lombok**

---

## 🧩️ Arquitectura general

El sistema sigue una arquitectura **modular y escalable**, compuesta por:

* **Backend (IAgro API):** desarrollado con Spring Boot.
* **Frontend:** (Angular, en desarrollo).
* **Dispositivo IoT Agrolink:** basado en **ESP32 + Arduino**, encargado de medir variables ambientales.
* **Base de datos:** PostgreSQL.
* **Contenedores:** Docker (implementación futura).

---

## 🗄️ Estructura del proyecto

```
src/
 ├── main/
 │   ├── java/com/iagro/pettersson/
 │   │   ├── controller/
 │   │   ├── service/
 │   │   ├── repository/
 │   │   ├── entity/
 │   │   └── dto/
 │   └── resources/
 │       ├── db/migration/
 │       │   └── V1__create_tables.sql
 │       └── application.properties
 └── test/
```

---

## ⚙️ Configuración local

### 1️⃣ Variables de entorno

Asegúrate de tener configuradas las siguientes variables en tu entorno o archivo `.env`:

```
DB_HOST=localhost:5432
DB_NAME_IAGRO=iagro_db
DB_USER=postgres
DB_PASSWORD=tu_contraseña
```

### 2️⃣ Ejecución

```bash
mvn spring-boot:run
```

El backend se ejecutará por defecto en:
🔗 `http://localhost:8080`

---

## 🧠 Migraciones con Flyway

Para aplicar las migraciones iniciales:

```bash
mvn clean flyway:migrate
```

Los scripts están en `src/main/resources/db/migration`.

---

## 📡 Endpoints iniciales

| Método | Endpoint      | Descripción                    |
| :----: | ------------- | ------------------------------ |
|   GET  | `/api/planes` | Lista todos los planes activos |
|  POST  | `/api/planes` | Crea un nuevo plan             |

---

## 🤝 Contribución

1. Haz un **fork** del proyecto.
2. Crea una nueva rama con tu funcionalidad: `git checkout -b feature/nueva-funcionalidad`.
3. Realiza un **commit** con cambios claros.
4. Envía un **pull request**.

---

## 📜 Licencia

Proyecto de investigación aplicada desarrollado en el **SENA - SENNOVA**, bajo fines académicos y tecnológicos.
© 2025 - Pettersson Pulido.

---

## 🌾 Estado actual

✅ Migraciones listas
✅ Conexión a PostgreSQL establecida
✅ Modelos y repositorio en GitHub
🗷️ En desarrollo: controladores y endpoints
