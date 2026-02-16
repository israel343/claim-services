## DESCRIPCIÓN DEL PROYECTO

Este proyecto modela el proceso de gestión de **siniestros (claims)** de un seguro.  
Un *claim* es una solicitud de indemnización que un cliente registra contra una póliza (por ejemplo: choque, robo, vidrio, incendio, etc.).  
El objetivo del sistema es permitir que un claim pase por un **ciclo de vida controlado**, desde su creación hasta su resolución final (aprobación/pago o rechazo/cancelación), aplicando reglas de negocio y manteniendo trazabilidad de cada paso.

### Flujo a alto nivel (end-to-end)
1. El cliente/operador registra un claim con: **policyNumber**, **claimantId**, **tipo**, **descripción** y **monto solicitado**.
2. El sistema ejecuta pasos del proceso: **submit → validate → decide**.
3. Según reglas de negocio/política (por ejemplo umbral de monto), el claim se **aprueba** o **rechaza**.
4. Si está aprobado y aplica, se ejecuta el **pago** (pay).  
5. Si ocurre un error en el proceso, el orquestador registra el fallo.


# Claims Platform — Core + Orchestrator

Este repositorio contiene **2 APIs** que se prueban **en conjunto**:

- **claims-core-service** (puerto **8071**) → Gestión de *claims/siniestros* (CRUD + ciclo de vida).
- **claims-orchestrator-service** (puerto **8070**) → Orquestación del flujo completo consumiendo el Core.

>  Para probar el flujo end-to-end, **ambos servicios deben estar levantados**.

---

## Prerrequisitos

- **Java 17 (JDK 17)** instalado.
-  Puertos libres:
  - **8071** (Core)
  - **8070** (Orchestrator)
- Consola (CMD / PowerShell / Terminal).
-  (Opcional) **Postman** o `curl` para pruebas.
-  (Recomendado) Tener `JAVA_HOME` configurado y `java` disponible en el PATH.

### Verificar Java
```bash
java -version
```
Debe mostrar Java 17.x.

---

## Artefactos (.jar)

Vas a ejecutar los servicios desde sus `.jar`:

- `claims-core-service-0.0.1-SNAPSHOT.jar`
- `claims-orchestrator-service-0.0.1-SNAPSHOT.jar`

> Si estás usando otros nombres/versiones, ajusta los comandos.

---

## Contratos (.yaml)

Los contratos del api se encuentran en la carpeta /contratos del presente repositorio

---


## Levantar ambos servicios (paso a paso)

### Paso 0 — Preparar directorios
Crea la carpeta local (ejemplo):

- `C:\claims\`

Copia cada `.jar` (en la carpeta /jar de este repositorio) y ubícalo en la carpeta local:

- `C:\claims\claims-core-service-0.0.1-SNAPSHOT.jar`
- `C:\claims\claims-orchestrator-service-0.0.1-SNAPSHOT.jar`

---

## 1) Levantar Claims Core Service (8071)

Entra a la carpeta del Core y ejecuta:

```bash
cd C:\claims
java -jar claims-core-service-0.0.1-SNAPSHOT.jar
```

### Verificar Core
- Swagger UI:  
  `http://localhost:8071/swagger-ui/index.html`

---

## 2) Levantar Claims Orchestrator Service (8070)

> Importante: el Orchestrator depende del Core, así que **Core debe estar arriba primero**.

Entra a la carpeta del Orchestrator y ejecuta:

```bash
cd C:\claims
java -jar claims-orchestrator-service-0.0.1-SNAPSHOT.jar
```

### Verificar Orchestrator
- Swagger UI:
  - `http://localhost:8070/swagger-ui/index.html`

---




## Diagrama de Flujo de estados del Claim

El Diagrama de estados de  un Claim se encuentra en el presente repositorio (states-diagram-claim.jpeg)

---
## Probar el flujo completo (Orchestrator → Core)

> Todas las pruebas del flujo se hacen desde el **Orchestrator**.

> Nota: En el presente repositorio también se encuentra el Collection de Postman (IDM.postman_collection.json) que contiene las pruebas de los endpoints o en su defecto también puede usar el swagger (http://localhost:8071/swagger-ui/index.html, http://localhost:8070/swagger-ui/index.html)

### 1) Iniciar flujo (start)
**POST** `http://localhost:8070/api/v1/flows/claims`

Body ejemplo:
```json
{
  "policyNumber": "POL-2026-0001",
  "claimantId": "DNI-12345678",
  "type": "COLLISION",
  "description": "Choque leve en estacionamiento",
  "amountRequested": 900.00,
  "autoPay": true
}
```

Respuesta ejemplo (200):
```json
{
  "flowId": "65263ac1-3a76-4dd5-9e44-27218a690afa",
  "claimId": "37238551-132c-4441-87a9-faa8cdce7d94",
  "status": "COMPLETED",
  "lastStep": "REJECT",
  "executedSteps": ["CREATE","SUBMIT","VALIDATE","DECIDE","REJECT"],
  "errorMessage": null,
  "createdAt": "2026-02-15T16:37:30.614296900Z",
  "updatedAt": "2026-02-15T16:37:30.849350400Z"
}
```

---

### 2) Consultar estado del flow
**GET** `http://localhost:8070/api/v1/flows/{flowId}`

Ejemplo:
```bash
curl http://localhost:8070/api/v1/flows/65263ac1-3a76-4dd5-9e44-27218a690afa
```

---

### 3) Reintentar un flow (solo FAILED o COMPENSATED)
**POST** `http://localhost:8070/api/v1/flows/{flowId}/retry`

Ejemplo:
```bash
curl -X POST http://localhost:8070/api/v1/flows/65263ac1-3a76-4dd5-9e44-27218a690afa/retry
```

> Si el flow está `COMPLETED`, debe responder 409 (no se permite retry).

---

## Probar Core directamente (opcional)

Si quieres probar endpoints del Core sin orquestador:

- Base: `http://localhost:8071/api/v1/claims`

Ejemplos comunes:
- `POST /api/v1/claims` (crear)
- `GET /api/v1/claims` (listar/buscar)
- `GET /api/v1/claims/{claimId}` (detalle)
- `POST /api/v1/claims/{claimId}/submit`
- `POST /api/v1/claims/{claimId}/validate`
- `POST /api/v1/claims/{claimId}/approve`
- `POST /api/v1/claims/{claimId}/reject`
- `POST /api/v1/claims/{claimId}/pay`
- `POST /api/v1/claims/{claimId}/cancel`

---

## Notas importantes

- Por defecto se usa **H2 in-memory** en ambos servicios:
  - Los datos se reinician al detener el proceso.
- El Orchestrator debe apuntar al Core con:
  - `core.base-url=http://localhost:8071`
- Los puertos por defecto:
  - Core `8071`
  - Orchestrator `8070`

---

## Troubleshooting

### “Port already in use”
Libera el puerto 8070/8071 o cambia:
- `server.port` en cada servicio.

### Orchestrator no conecta al Core
- Asegúrate que Core esté arriba primero.
- Revisa `core.base-url` (debe ser `http://localhost:8071`).

### Swagger UI no abre
- Verifica que exista el YAML:
  - `http://localhost:8070/openapi-orchestrator.yaml`
- Swagger UI:
  - `http://localhost:8070/swagger-ui/index.html`

---

## Estructura del repositorio

```
claims-core-service/
claims-orchestrator-service/
contratos/
jars/
README.md
```

> Ambos proyectos se ejecutan como servicios independientes, pero se prueban juntos para el flujo completo.
