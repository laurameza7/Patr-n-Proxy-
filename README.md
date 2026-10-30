# AI Proxy Platform — Backend (Spring Boot)

Plataforma de consumo de IA con **Proxy de rate limiting y cuotas**. Taller del **Patrón Proxy**, Patrones de Software, Universidad Cooperativa de Colombia, Campus Pasto.

**Autora:** Laura Sofía Meza Reinoso

## Patrón Proxy aplicado

```
Cliente (AIController)
      │  depende solo de la interfaz AIGenerationService
      ▼
RateLimitProxyService   → ¿superó N requests/minuto?  → 429 Too Many Requests + Retry-After
      ▼
QuotaProxyService       → ¿le quedan tokens este mes? → 402 Payment Required
      ▼                   (descuenta los tokens consumidos después de generar)
MockAIGenerationService → objeto real: Thread.sleep(1200 ms) + texto aleatorio predefinido
```

| Rol del patrón | Clase |
|---|---|
| Subject (interfaz común) | `service/AIGenerationService` |
| Real Subject | `service/MockAIGenerationService` |
| Proxy 1 (control de acceso) | `proxy/RateLimitProxyService` |
| Proxy 2 (control de cuota y registro de consumo) | `proxy/QuotaProxyService` |
| Ensamblaje de la cadena | `config/ProxyChainConfig` |
| Cliente | `controller/AIController` |

## Planes

| Plan | Requests / minuto | Tokens / mes |
|---|---|---|
| FREE | 10 | 50.000 |
| PRO | 60 | 500.000 |
| ENTERPRISE | ilimitado | ilimitado |

## Endpoints

El usuario se identifica con el header `X-User-Id`. Si no se envía, se usa `demo-user`.

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/ai/generate` | Pasa por la cadena completa de proxies. Body: `{ "prompt": "...", "maxTokens": 512 }` |
| GET | `/api/quota/status` | Tokens usados, tokens restantes, `resetDate`, plan actual y estado del rate limit |
| GET | `/api/quota/history` | Uso diario de los últimos 7 días |
| POST | `/api/quota/upgrade` | Upgrade de plan (FREE → PRO) con pago simulado. Body opcional: `{ "targetPlan": "PRO" }` |

## Tareas programadas (`scheduler/QuotaResetScheduler`)

- `0 * * * * *`: reinicia el rate limit cada minuto.
- `0 0 0 1 * *`: reinicia la cuota mensual el primer día de cada mes.

## Ejecutar localmente

```bash
mvn spring-boot:run
```

## Despliegue (Railway)

El repo incluye un `Dockerfile`. En Railway: New Project → Deploy from GitHub repo → seleccionar este repo → Settings → Networking → Generate Domain. Variable opcional: `ALLOWED_ORIGINS=https://<tu-frontend>.vercel.app`.
