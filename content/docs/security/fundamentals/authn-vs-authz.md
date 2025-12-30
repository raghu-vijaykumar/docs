---
title: "Authentication vs Authorization"
draft: false
---

# Authentication vs Authorization

Authentication answers “who are you?” Authorization answers “what are you allowed to do?” Mixing them causes systemic defects: impersonation when authentication is weak, and overreach when authorization is absent or inconsistent. Treat them as separate subsystems with distinct controls, lifecycles, and evidence.

---

## Authentication (AuthN)

Purpose
- Verify the identity of a principal (user, service, device).

Common methods
- Passwords with MFA (TOTP, WebAuthn/passkeys, hardware keys).
- Federated login (SSO via SAML or OIDC).
- Service/workload identity (mTLS, SPIFFE, cloud-managed identities).
- API auth (OAuth 2.1 access tokens, mTLS clients).

Sessions and tokens
- Cookie-backed sessions: server stores session; browser sends cookie automatically.
  - Use Secure, HttpOnly, SameSite; protect with CSRF tokens.
- Bearer tokens (JWT/Opaque): client sends Authorization: Bearer; no cookie CSRF risk but higher XSS exposure if stored in JS.
  - Prefer keeping tokens server-side via BFF for SPAs.

OIDC essentials
- Authorization Code + PKCE for web/mobile; short-lived access tokens; refresh rotation with revocation on anomaly.
- Validate iss, aud, exp, nbf, iat, nonce (for web), and signature against IdP JWKS.

Pitfalls
- Long-lived tokens/sessions; no rotation on privilege change.
- Accepting “none” or wrong JWT alg; not checking aud/iss/exp.
- Storing tokens in localStorage (XSS exposure); avoid.

Cross-refs: [SSO](/docs/security/iam/sso.md), [Federation (SAML/OIDC)](/docs/security/iam/federation-saml-oidc.md), [MFA](/docs/security/iam/mfa.md)

---

## Authorization (AuthZ)

Purpose
- Decide whether an authenticated principal may perform an action on a resource, under context (tenant, device posture, time, risk).

Models
- RBAC: roles grant capabilities (coarse-grained).
- ABAC: attributes (tags/labels/claims) drive policy (flexible, scalable).
- ReBAC: relationships/graphs (e.g., “viewer of resource owned by group X”).
- Policy-as-code: central PDP (OPA/Rego, Cedar) + distributed PEPs.

Where to enforce
- Edge/API gateway: coarse checks (auth present, basic scopes).
- Service layer: capability checks per endpoint; tenant/ownership predicates.
- Data layer: row/column-level security, always including tenant and owner filters.

Pitfalls
- Trusting client-provided identifiers/roles; missing tenant checks (IDOR/BOLA).
- Authorizing at UI only; skipping checks in API or DB.
- Role explosion; better use attributes/claims and conditions.

Cross-refs: [RBAC/ABAC](/docs/security/iam/rbac-abac.md), [Least Privilege & Zero Trust](/docs/security/iam/least-privilege-zero-trust.md)

---

## AuthN then AuthZ: Practical Flow

1) Authenticate
   - Verify token/session, build principal context: subject (sub), tenant, groups/roles, device posture.
2) Authorize
   - Evaluate policy for action/resource with context; deny-by-default; log decision.
3) Enforce at data
   - Apply tenant/owner predicates in queries; avoid trusting client IDs.

Example (Express)
```js
// 1) AuthN middleware (OIDC JWT example, simplified)
import jwksClient from "jwks-rsa";
import jwt from "jsonwebtoken";

const client = jwksClient({ jwksUri: "https://idp.example.com/.well-known/jwks.json" });
function getKey(header, cb) {
  client.getSigningKey(header.kid, (err, key) => cb(err, key?.getPublicKey()));
}
function authenticate(req, res, next) {
  const token = req.headers.authorization?.split(" ")[1];
  if (!token) return res.status(401).end();
  jwt.verify(token, getKey, { audience: "api://orders", issuer: "https://idp.example.com/" }, (err, payload) => {
    if (err) return res.status(401).end();
    req.user = { sub: payload.sub, tenant: payload.tenant, scopes: payload.scopes || [] };
    next();
  });
}

// 2) AuthZ helper (capability check)
function requireScope(scope) {
  return (req, res, next) => req.user?.scopes?.includes(scope) ? next() : res.status(403).end();
}

// Endpoint with both
app.post("/v1/orders", authenticate, requireScope("orders:create"), async (req, res) => {
  // 3) Data-level enforcement (tenant predicate)
  const order = await db.createOrder({ ...req.body, tenant: req.user.tenant, userId: req.user.sub });
  res.json(order);
});
```

SQL guard
```sql
-- Always scope by tenant/owner; never rely on client-provided IDs alone
SELECT * FROM orders WHERE id = $1 AND tenant_id = $2 AND user_id = $3;
```

---

## Session vs Token: When to Use

- Web apps for end-users: cookie session with HttpOnly, SameSite=Lax/Strict; CSRF tokens; optional BFF for SPAs.
- Public APIs: OAuth 2.1 access tokens (short TTL); require custom headers to avoid CSRF; strict CORS.
- Service-to-service: mTLS identities, SPIFFE IDs, or short-lived JWTs issued by internal IdP; prefer audience-bound tokens.

Cross-refs: [API Security](/docs/security/application-security/api-security.md), [TLS/SSL](/docs/security/cryptography/tls-ssl/)

---

## Testing and Verification

- Unit/integration tests
  - Ensure endpoints return 401 without auth; 403 for missing capabilities.
  - Cross-tenant access attempts return 404/403; never leak existence.
- Static checks
  - Lint for direct use of client-provided IDs; require tenant predicates in data access helpers.
- DAST
  - IDOR tests (guess IDs across tenants); verify denials and no info leakage.
- Logs and evidence
  - Decision logs include principal, action, resource, decision (no PII); immutable storage.

---

## Checklist

Authentication
- [ ] Short-lived tokens/sessions; rotation on privilege change; MFA for privileged access.
- [ ] OIDC validations: iss/aud/exp/nbf/iat/nonce; signatures via JWKS; no “none”/weak algs.
- [ ] Cookies: Secure, HttpOnly, SameSite; CSRF for cookie-backed flows; avoid localStorage for tokens.

Authorization
- [ ] Deny-by-default; explicit capabilities per endpoint.
- [ ] Tenant/ownership predicates at service and DB layers; ignore client-supplied userId/tenantId.
- [ ] Centralized policy engine or shared library; decisions logged and auditable.
- [ ] Principle of least privilege; roles/attributes reviewed periodically.

Operations
- [ ] Access reviews; revoke stale sessions; anomaly detection on auth/authz failures.
- [ ] Separation of duties; break-glass with hardware MFA and tight monitoring.

---

## Cross-References

- IAM overview: [/docs/security/iam/_index.md](/docs/security/iam/_index.md)
- RBAC/ABAC: [/docs/security/iam/rbac-abac.md](/docs/security/iam/rbac-abac.md)
- Zero Trust: [/docs/security/iam/least-privilege-zero-trust.md](/docs/security/iam/least-privilege-zero-trust.md)
- SSO & Federation: [/docs/security/iam/sso.md](/docs/security/iam/sso.md), [/docs/security/iam/federation-saml-oidc.md](/docs/security/iam/federation-saml-oidc.md)
- API Security: [/docs/security/application-security/api-security.md](/docs/security/application-security/api-security.md)
