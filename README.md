# MessageBoard — Message App

Aplicación full-stack de mensajería personal (notas/mensajes) con autenticación JWT.

## Stack

| Capa | Tecnología |
|---|---|
| Frontend | Vue 3 + TypeScript + Vite + Pinia + Vue Router + Tailwind CSS |
| Backend | Spring Boot 3.5 + Java 25 + Maven |
| Base de datos | MongoDB (Spring Data) |
| Autenticación | Spring Security + JWT (JJWT) |
| API docs | SpringDoc OpenAPI / Swagger UI |
| Frontend testing | Vitest + Vue Test Utils |
| Backend testing | JUnit 5 + Mockito + AssertJ + MockMvc |

## Estructura

```
backend/   ← Spring Boot REST API
frontend/  ← Aplicación Vue + TypeScript
```

### Backend (`backend/`)

Arquitectura en capas:

```
Controller → Service → Repository → MongoDB
     ↑
Security (JwtAuthenticationFilter → SecurityContext)
```

Paquetes:
- `config` — Seguridad, CORS, OpenAPI
- `controller` — Endpoints REST (`AuthController`, `MessageController`)
- `dto` — Objetos de entrada/salida de la API (`MessageResponse`, `AuthResponse`, ...)
- `exception(.handler)` — Excepciones de aplicación y manejo global
- `model` — Entidades (`User`, `Message`)
- `repository` — Acceso a datos
- `security` — JWT, filtros, `UserDetailsService`, usuario autenticado
- `service` — Lógica de negocio (`AuthService`, `MessageService`)

## API REST

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| POST | `/api/auth/register` | — | Registrar usuario y obtener JWT |
| POST | `/api/auth/login` | — | Iniciar sesión y obtener JWT |
| GET | `/api/messages` | JWT | Listar mensajes del usuario |
| GET | `/api/messages/{id}` | JWT | Obtener un mensaje propio |
| POST | `/api/messages` | JWT | Crear mensaje (201) |
| PUT | `/api/messages/{id}` | JWT | Actualizar mensaje propio |
| DELETE | `/api/messages/{id}` | JWT | Borrar mensaje propio (204) |

Todas las rutas `/api/messages` requieren `Authorization: Bearer <JWT>`.

La autorización se aplica en el backend: un usuario solo puede leer/editar/borrar sus propios mensajes.

Swagger UI disponible en `http://localhost:8080/swagger-ui.html`.

## Configuración y entorno

El backend se configura mediante variables de entorno (no se versionan secretos):

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `JWT_SECRET` | Secreto JWT (obligatorio, base64 ≥ 256 bits) | — |
| `JWT_EXPIRATION` | Expiración JWT en ms | `3600000` |
| `MONGO_URI` | URI de conexión a MongoDB | `mongodb://localhost:27017/messages` |
| `MONGO_AUTH_DB` | Base de datos de autenticación MongoDB | `admin` |
| `SPRING_PROFILES_ACTIVE` | Perfil activo | `local` |

Generar un secreto: `openssl rand -base64 64`

Ejemplo de variables de entorno para arrancar el backend localmente:

```bash
export JWT_SECRET=<secreto-generado>
export MONGO_URI=mongodb://localhost:27017/messages
```

Existe un plantilla de configuración en
`backend/src/main/resources/application-local.example.properties`.

## Puesta en marcha

Requisitos: Java 25, Maven, Node.js, y una instancia MongoDB accesible.

```bash
# 1. Configurar variables de entorno (ver tabla anterior)
# 2. Arrancar la base de datos MongoDB

# Backend (puerto 8080)
cd backend && mvn spring-boot:run

# Frontend dev (puerto 5173; hace proxy a /api → :8080)
cd frontend && npm run dev
```

O bien usar el script `start.sh` para lanzar ambos procesos.

## Test

```bash
# Backend (JUnit + Mockito + JaCoCo)
cd backend && mvn test

# Frontend (Vitest)
cd frontend && npm test

# Build de producción del frontend
cd frontend && npm run build
```

Resultados actuales:
- Backend: 55 tests, 0 fallos; cobertura ~91% instrucciones / ~96% ramas.
- Frontend: 34 tests, 0 fallos.

## Notas de arquitectura

- El backend es la fuente de verdad para autenticación, autorización, validación y reglas de negocio; el frontend nunca es un límite de seguridad.
- Las entidades de persistencia no se exponen directamente por la API (se usan DTOs como `MessageResponse`).
- Los controladores permanecen delgados; la lógica de negocio vive en los servicios.
- El frontend centraliza el acceso HTTP en `src/services/` (`api.ts`, `authService.ts`, `messageService.ts`), consumido por los stores Pinia.
- La sesión del frontend se persiste en `localStorage`; ante una respuesta `401`, se limpia la sesión y se redirige a `/login`.
