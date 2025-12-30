---
title: "API Security"
draft: false
---

# API Security

APIs are the connective tissue of modern systems. Securing them requires multiple layers: strong authentication and authorization, transport security, input/output validation, abuse prevention, and operational visibility. This guide provides production‑oriented patterns, examples, and checklists.

---

## Design Principles

- Prefer standard protocols
  - Users: OAuth 2.1 + OIDC for authentication.
  - Services: mTLS identity plus scoped tokens (JWT/PASETO) where needed.
- Short‑lived credentials
  - Access tokens with minutes‑level TTL; use refresh tokens bound to device/session.
- Least privilege
  - Narrow scopes/claims; per‑endpoint policy; deny‑by‑default.
- Explicit contracts
  - OpenAPI schemas; validate requests and responses; generate both client and server stubs with validation hooks.
- Defense in depth
  - TLS everywhere; input validation; output encoding; rate limits; anomaly detection.
- Replay and idempotency
  - Idempotency keys for mutating endpoints; time‑bounded nonces for signed requests.

Cross‑references
- Transport: see [TLS/SSL Configuration](/docs/security/cryptography/tls-ssl/)
- Credentials: see [Hashing and Password Storage](/docs/security/cryptography/hashing/) and [PKI](/docs/security/cryptography/pki/)

---

## Authentication

### Users: OAuth 2.1 + OpenID Connect

- Flows
  - Authorization Code with PKCE (public/mobile/web SPA).
  - Confidential clients (server web apps) use Authorization Code without PKCE, but PKCE still recommended.
- Tokens
  - Access token (JWT typically): short TTL (5–15 min); audience (aud) restricted to API.
  - Refresh token: rotate on use; bind to device/session with additional context (e.g., session ID, IP hints).
- Claims
  - iss, sub, aud, iat, exp, nbf; custom claims for roles/permissions kept minimal.
- Validations
  - issuer and audience match; signature via JWKS; exp/nbf window strictly enforced; clock skew < 60s.

Example (Node/Express, JWKS):
```js
import jwksRsa from "jwks-rsa";
import jwt from "jsonwebtoken";
import express from "express";

const ISSUER = "https://idp.example.com/";
const AUDIENCE = "api://orders";
const JWKS = jwksRsa({
  jwksUri: `${ISSUER}.well-known/jwks.json`,
  cache: true, cacheMaxEntries: 5, cacheMaxAge: 10 * 60 * 1000
});

async function authenticate(req, res, next) {
  const header = req.headers.authorization || "";
  const [, token] = header.split(" ");
  if (!token) return res.status(401).end();

  const decoded = jwt.decode(token, { complete: true });
  if (!decoded?.header?.kid) return res.status(401).end();

  const key = await JWKS.getSigningKey(decoded.header.kid);
  const pub = key.getPublicKey();
  try {
    const payload = jwt.verify(token, pub, {
      algorithms: ["RS256", "ES256"],
      issuer: ISSUER,
      audience: AUDIENCE
    });
    req.user = payload;
    next();
  } catch {
    return res.status(401).end();
  }
}
```

### Services: mTLS + Scoped Tokens

- Use mTLS for service identity; authorize by SPIFFE ID or SAN.
- Issue short‑lived tokens with least privilege for cross‑domain calls; consider internal token issuers with attestation.
- Prefer token exchange (RFC 8693) instead of passing user tokens downstream (prevent confused deputy).

---

## Authorization

- Centralize policy
  - Externalize from application code (e.g., OPA/Rego, Cedar, Zanzibar‑style ReBAC).
- Models
  - RBAC for coarse permissions; ABAC for context/tenant; ReBAC for shared resources.
- Decisions
  - Deny by default; evaluate per request; log allow/deny with reason and attributes for audit.
- Multi‑tenant controls
  - Tenant isolation checks are mandatory at every data access layer; include tenant_id in indexes and access predicates.

Example (OPA sidecar HTTP API):
```rego
package httpapi.authz

default allow = false

allow {
  input.method == "POST"
  input.path = ["v1", "orders"]
  input.user.scope[_] == "orders:create"
  input.user.tenant == input.body.tenant
}
```

---

## Input and Output Validation

- Request validation
  - Validate against OpenAPI/JSON Schema; reject extraneous fields; canonicalize encodings.
- Response validation
  - Verify you do not leak server internals; scrub sensitive fields.
- Content types
  - Enforce expected content‑type (application/json); size limits to prevent DoS.

Node/Express example (zod):
```js
import { z } from "zod";

const CreateOrder = z.object({
  idempotencyKey: z.string().uuid(),
  tenant: z.string().uuid(),
  items: z.array(z.object({
    sku: z.string().min(1),
    qty: z.number().int().positive().max(1000)
  })).min(1)
});

function validate(schema) {
  return (req, res, next) => {
    const r = schema.safeParse(req.body);
    if (!r.success) return res.status(400).json({ error: "invalid" });
    req.body = r.data;
    next();
  };
}
```

---

## Abuse Prevention and Rate Limiting

- Global and per‑principal limits
  - Per access token, per API key, per user, with fallback to IP.
- Concurrency limits
  - Limit in‑flight requests per principal; apply backpressure.
- Adaptive controls
  - Raise challenges or degrade gracefully under abnormal spikes.
- Idempotency keys
  - Required for POST/PUT that mutate state; store for limited TTL; deduplicate on retries.

Example (idempotency):
```sql
-- schema
CREATE TABLE idempotency (
  tenant UUID, key UUID, method TEXT, path TEXT,
  status INT, response JSONB, created_at TIMESTAMPTZ DEFAULT now(),
  PRIMARY KEY (tenant, key)
);
```

---

## Replay and Signed Requests

- Nonce + timestamp
  - Require X-Request-Timestamp; reject if outside small window (e.g., 5 minutes).
- Signature
  - HMAC over canonicalized request components (method, path, headers, body).
- Use case
  - Third‑party webhooks and partner APIs.

Webhook verification (Node):
```js
import crypto from "crypto";

function verifyWebhook(secret, rawBody, timestamp, signature) {
  const base = `v1:${timestamp}:${rawBody}`;
  const mac = crypto.createHmac("sha256", secret).update(base).digest("hex");
  // constant-time compare
  return crypto.timingSafeEqual(Buffer.from(mac), Buffer.from(signature));
}
```

---

## Transport and Discovery

- Enforce HTTPS with modern TLS; redirect HTTP to HTTPS at edge.
- Mutual TLS internally for zero trust posture.
- API discovery endpoints must not leak secrets; rate limit and protect schema fetch if it reveals sensitive metadata.

---

## CORS and Browser Considerations

- CORS
  - Explicit allow‑list of origins; restrict methods and headers; do not use wildcard with credentials.
- Cookies (for session‑based APIs)
  - Set Secure, HttpOnly, SameSite=Lax/Strict; rotate session IDs on privilege changes.

---

## Versioning and Deprecation

- Semantic versioning of APIs; explicit deprecation windows; headers to warn clients.
- Backward‑compatible changes only in minor versions; breaking changes require new versioned route.

---

## Observability and Incident Readiness

- Structured logs with auth context (subject, tenant, client id), request ID, and decision metadata.
- Audit trails for authorization decisions, scope evaluations, and administrative actions.
- Metrics: rate of 4xx/5xx per endpoint, token verification failures, rate‑limit hits, p99 latency.
- Tracing: propagate correlation IDs; include auth checks as spans.

---

## Example End‑to‑End (Go, net/http)

```go
// Pseudocode: TLS enforced at LB; JWT auth; per-user rate limit; idempotency
func ordersHandler(w http.ResponseWriter, r *http.Request) {
  // 1) AuthN
  claims, err := verifyJWT(r.Header.Get("Authorization"))
  if err != nil { http.Error(w, "unauthorized", 401); return }

  // 2) Rate limit
  if !limit(claims.Sub) { http.Error(w, "too many requests", 429); return }

  // 3) Parse & validate
  var req CreateOrder
  if err := json.NewDecoder(http.MaxBytesReader(w, r.Body, 1<<20)).Decode(&req); err != nil {
    http.Error(w, "invalid", 400); return
  }
  if err := req.Validate(); err != nil { http.Error(w, "invalid", 400); return }

  // 4) Idempotency
  if ok, resp := checkIdempotency(claims.Tenant, req.IdempotencyKey); ok {
    writeJSON(w, 200, resp); return
  }

  // 5) AuthZ
  if !authorize(claims, "orders:create", claims.Tenant == req.Tenant) {
    http.Error(w, "forbidden", 403); return
  }

  // 6) Execute
  resp, err := createOrder(r.Context(), req)
  if err != nil { http.Error(w, "server error", 500); return }

  // 7) Persist idempotency and respond
  saveIdempotency(claims.Tenant, req.IdempotencyKey, 201, resp)
  writeJSON(w, 201, resp)
}
```

---

## API Keys

- Generation: high‑entropy, random; prefix for tracking (e.g., ak_live_xxx).
- Storage: do not store plaintext; store verifier = HMAC_SHA256(key, pepper).
- Scoping: tie to tenant/app; restrict to routes/verbs; set explicit expiration.
- Rotation: support multiple active keys; publish last‑used timestamp.

---

## Common Pitfalls

- Accepting tokens without verifying audience/issuer and signature.
- Over‑broad scopes; using “admin” scope outside dedicated admin APIs.
- Missing idempotency on payment/order APIs.
- Logging Authorization headers or full request bodies with secrets.
- Using wildcard CORS with credentials, enabling CSRF.
- Long‑lived access tokens without refresh rotation.

---

## Checklist

- [ ] HTTPS enforced; modern TLS; mTLS for service‑to‑service.
- [ ] OAuth 2.1/OIDC for users; short‑lived access tokens; rotated refresh tokens.
- [ ] JWTs verified against JWKS; iss/aud/exp/nbf strictly validated.
- [ ] Centralized authorization; deny‑by‑default; auditable decisions.
- [ ] Request/response validation against schema; size and type limits.
- [ ] Per‑principal rate limiting and concurrency limits; abuse detection.
- [ ] Idempotency keys for mutating operations; replay window controls for signed requests.
- [ ] Sensitive data redaction in logs; structured audit trails.
- [ ] API keys scoped, hashed (HMAC), and rotated; last‑used timestamps.
- [ ] Versioning strategy and deprecation policy documented.
