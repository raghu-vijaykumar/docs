---
title: "Broken Authentication & Authorization"
draft: false
---

# Broken Authentication & Authorization

Broken authN/Z spans two root failures:
- Authentication weaknesses allow impersonation (credential stuffing, weak factors, session fixation, JWT validation gaps).
- Authorization weaknesses allow users to act outside intended privileges (IDOR, missing object/function-level checks, mass assignment).

This guide provides concrete patterns to harden authentication and implement reliable, centralized authorization.

---

## Authentication Hardening

Threats
- Credential stuffing/reuse; brute force; password spraying.
- Session fixation/hijacking; insecure remember‑me; weak cookie attributes.
- Token validation gaps (iss/aud/alg confusions); long‑lived tokens.

Controls
- MFA (phishing‑resistant) for privileged actions. See: [/docs/security/iam/mfa.md](/docs/security/iam/mfa.md)
- Rate limit and IP/identity throttling on login, reset, and signup.
- Breach checks (HIBP‑style); block known compromised passwords.
- Password policy: length ≥ 12–14; avoid complexity theater; encourage passphrases; lockout/backoff.
- Session management
  - Cookies: Secure, HttpOnly, SameSite=Lax/Strict; rotate session ID at auth and privilege changes.
  - CSRF protection for cookie‑backed APIs; BFF pattern for SPAs; short TTLs for admin consoles.
- JWT/OIDC
  - Verify signature against JWKS; enforce iss/aud/exp/nbf; prevent “none”/weak algs; short access token TTL (5–15 min), refresh with rotation and revocation on anomaly.
- Account lifecycle
  - Email verification; secure password reset with single‑use tokens; bot defense on signup; device/session management UI.

Minimal Node example (login throttling)
```js
import rateLimit from "express-rate-limit";
const loginLimiter = rateLimit({ windowMs: 15*60_000, max: 20, standardHeaders: true });
app.post("/login", loginLimiter, loginHandler);
```

---

## Authorization Fundamentals

Common flaws
- IDOR/BOLA: accessing resource by guessing ID without tenant/ownership checks.
- Missing function‑level authZ: endpoint callable by any authenticated user.
- Mass assignment: binding client fields directly to privileged properties (e.g., role="admin").
- Contextless decisions: ignoring tenant, device posture, or environment.

Principles
- Deny by default; allow explicitly.
- Enforce at every boundary: API gateway, service, and data access layer.
- Include tenant and ownership checks in every data access path.
- Externalize policy where possible; centralize decisions for consistency and audit.

See: [/docs/security/iam/rbac-abac.md](/docs/security/iam/rbac-abac.md) and [/docs/security/iam/least-privilege-zero-trust.md](/docs/security/iam/least-privilege-zero-trust.md)

---

## Preventing IDOR/BOLA

- Always include tenant/owner predicates in queries. Do not rely solely on client‑provided IDs.
- Derive principal from verified token/session; ignore client‑supplied userId/tenantId fields.
- Use surrogate, non‑predictable IDs (UUIDv4/ULID) exposed externally; still verify ownership.

Example (service layer)
```ts
// Require tenant and subject from verified token; ignore body.userId
const { sub, tenant } = req.user;
const order = await db.getOrder(req.params.orderId, tenant);
if (!order || order.userId !== sub) return res.status(404).end();
```

DB guard (SQL)
```sql
SELECT * FROM orders WHERE id = $1 AND tenant_id = $2;
```

---

## Function-Level Authorization

- Define capabilities (verbs on resources): orders:view, orders:create, admin:users:update.
- Map roles to capabilities; check capabilities per endpoint.
- Require admin APIs to be on separate routes, hostnames, or projects where possible.

Example (Express)
```js
function requireScope(scope) {
  return (req, res, next) => req.user?.scopes?.includes(scope) ? next() : res.status(403).end();
}
app.post("/v1/orders", requireScope("orders:create"), createOrder);
app.post("/admin/users/:id/role", requireScope("admin:users:update"), updateRole);
```

---

## Mass Assignment and Privilege Fields

- Never bind request bodies wholesale to models.
- Maintain explicit allow‑lists of mutable fields; ignore privileged fields from client (role, isAdmin, balance, ownerId).

Example
```ts
const allowed = (({ name, email }) => ({ name, email }))(req.body);
await db.updateUser(req.params.id, allowed);
```

---

## Multi‑Tenant Isolation

- Include tenant_id in all resource keys and queries.
- Enforce at API, service, and DB layers; add query builders that auto‑inject tenant predicates.
- Separate storage namespaces per tenant when feasible (buckets, prefixes, DB schemas).

---

## Centralized Policy and Decision Logging

- Use PDP/PEP pattern (OPA/Rego, Cedar, or vendor) for consistent checks and audit.
- Log allow/deny with principal, resource, action, and reason; avoid PII in logs; store immutably.

Rego example
```rego
package orders.authz
default allow = false
allow { input.action == "orders:create"; input.user.tenant == input.resource.tenant }
```

---

## Session and Token Hygiene

- Rotate session IDs on privilege escalate; invalidate sessions on password change or account recovery.
- Token binding where possible; store refresh tokens securely (bound to device context).
- Device/session management UI: list active sessions; allow user‑initiated revocation.

---

## Testing and Verification

- Unit tests for authZ: simulate different users/tenants and verify denies.
- Integration tests: verify 403 on unauthorized actions; 404 on cross‑tenant resource access to avoid disclosures.
- Static checks: linters to forbid direct use of user‑supplied IDs without tenant checks.
- DAST: scanners for IDOR; manual tests for mass assignment (try setting role/admin fields).

---

## Incident Response

- Revoke tokens/sessions; rotate signing keys if compromise suspected.
- Tighten policies; add deny overrides for vulnerable paths.
- Add regression tests; conduct postmortem and update review checklists.

---

## Checklist

Authentication
- [ ] MFA for privileged access; rate limits on auth endpoints; breach password checks.
- [ ] Sessions: Secure, HttpOnly, SameSite cookies; rotation on login/privilege change.
- [ ] JWTs verified with JWKS; iss/aud/exp/nbf enforced; short TTL; refresh rotation.

Authorization
- [ ] Deny‑by‑default; explicit capabilities per endpoint.
- [ ] Tenant and ownership checks on every data access path; IDs non‑predictable.
- [ ] No mass assignment; explicit allow‑lists of mutable fields.
- [ ] Centralized policy engine or shared library; decisions logged.

Testing/Operations
- [ ] Unit/integration tests for authZ paths; DAST for IDOR/mass assignment.
- [ ] Session/token revocation flow; admin APIs isolated.
- [ ] Audit logs immutable; alerts on unusual access patterns.

---

## Cross‑References

- AppSec overview: [/docs/security/application-security/](../_index.md)
- API Security: [/docs/security/application-security/api-security.md](../api-security.md)
- IAM Models: [/docs/security/iam/rbac-abac.md](/docs/security/iam/rbac-abac.md)
- Zero Trust: [/docs/security/iam/least-privilege-zero-trust.md](/docs/security/iam/least-privilege-zero-trust.md)
