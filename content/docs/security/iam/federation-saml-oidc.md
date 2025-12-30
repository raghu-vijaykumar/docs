---
title: "Identity Federation (SAML/OIDC)"
draft: false
---

# Identity Federation (SAML/OIDC)

Federation links identities across trust boundaries so users authenticate with a central Identity Provider (IdP) and access external applications (Service Providers/Relying Parties) without separate credentials. Properly implemented federation reduces password sprawl, centralizes MFA and risk policies, and enables consistent lifecycle management.

This guide focuses on protocol choices (OIDC vs SAML), trust configuration, attribute/claim mapping, provisioning, and hardening patterns.

---

## Core Concepts

- Identity Provider (IdP)
  - The authority that authenticates users and issues assertions/tokens (e.g., Okta, Entra ID/Azure AD, Google, ADFS, Keycloak).
- Service Provider (SP) / Relying Party (RP)
  - The application that consumes assertions/tokens to establish a session and authorize access.
- Federation protocol
  - SAML 2.0 (XML assertions) or OpenID Connect (OIDC: JSON over OAuth 2.1).
- Provisioning protocol
  - SCIM 2.0 for automated user/group lifecycle (joiner/mover/leaver).

Key goals
- Centralize MFA and conditional access at IdP.
- Use least‑privilege attribute mapping and role assignment.
- Automate lifecycle via SCIM; avoid orphaned access.

---

## Protocol Choice: OIDC vs SAML

OpenID Connect (OIDC)
- Best for modern web/mobile/SPA and API scenarios.
- JSON tokens (ID Token as JWT), discovery via .well-known.
- Flows: Authorization Code + PKCE (preferred), Device Flow, Client Credentials (service).
- Strong library support and simpler developer ergonomics.

SAML 2.0
- Common in enterprise SaaS and legacy integrations.
- Browser‑posted signed XML assertions; bindings include HTTP‑Redirect and HTTP‑POST.
- Mature admin tooling; verbose and nuanced (XML canonicalization, signature wrapping).

Decision guide
- Prefer OIDC when both sides support it.
- Use SAML when app/vendor only supports SAML or requires complex enterprise attributes.
- Mixed ecosystems often run both: OIDC for in‑house apps, SAML for older SaaS.

---

## Initiation Modes

- SP‑initiated (recommended UX)
  - User goes to app → redirect to IdP → authenticate → return with token/assertion.
  - Better for deep links and app‑specific contexts.
- IdP‑initiated
  - User launches app from IdP portal.
  - Ensure strict RelayState/state validation to avoid open redirects.

---

## Trust Configuration

SAML
- Exchange metadata XML (IdP SSO URL, certificates; SP ACS URL, entity IDs).
- Require signed assertions (and/or signed responses); validate audience, recipient, NotBefore/NotOnOrAfter, InResponseTo.
- Use SHA‑256+ signatures and recent certs; roll keys with overlap and monitoring.

OIDC
- Rely on discovery: https://idp.example.com/.well-known/openid-configuration
- Fetch JWKS for signature verification (rotate keys automatically).
- Validate iss, aud, exp, nbf, nonce (for web), and at_hash where applicable.

Clock skew
- Keep clocks in sync (NTP). Allow small drift (≤60s), never large windows.

---

## Claims, Attributes, and Role Mapping

Principles
- Deny‑by‑default; map only necessary attributes.
- Normalize tenancy and authorization within app; avoid pushing all logic to IdP groups.
- Keep mappings deterministic and documented.

OIDC example (ID Token claims)
```json
{
  "iss": "https://idp.example.com/",
  "aud": "api://dashboard",
  "sub": "00u1234abcd",
  "email": "alice@example.com",
  "groups": ["engineering", "prod-readers"],
  "amr": ["pwd", "fido2"],
  "exp": 1735689600
}
```

SAML attribute example (conceptual)
```xml
<saml:Attribute Name="memberOf">
  <saml:AttributeValue>engineering</saml:AttributeValue>
  <saml:AttributeValue>prod-readers</saml:AttributeValue>
</saml:Attribute>
```

Authorization tip
- Map IdP groups to coarse roles; enforce fine‑grained permissions at the app using RBAC/ABAC/ReBAC.
- Include tenant context explicitly when multi‑tenant; never trust client‑supplied tenant IDs.

---

## Sessions and Tokens

- App session
  - Create short‑lived, HttpOnly, Secure, SameSite cookies; rotate on privilege changes.
  - For SPAs, use BFF pattern so tokens stay server‑side.
- Token lifetimes
  - Short access tokens (5–15 min); refresh tokens with rotation and revocation on anomaly.
- Logout
  - OIDC RP‑initiated logout or SAML Single Logout when supported; handle partial failures gracefully.

---

## Provisioning and Lifecycle (SCIM)

- Automate user and group provisioning with SCIM 2.0.
- Attributes: username, email, displayName, groups/roles, manager, department.
- Deprovision immediately on termination/leave; remove group memberships promptly.
- For Just‑in‑Time (JIT) provisioning, create shadow accounts on first login but still use SCIM for group/role hygiene where possible.

---

## Security Controls

- MFA and risk policies at IdP
  - Phishing‑resistant MFA (WebAuthn/security keys) for admins and privileged apps.
  - Conditional access by device posture, network, time, and risk.
- Assertion/token validation
  - Strictly validate signatures and claims. Reject “none” or weak algorithms.
- Replay protection
  - Validate OAuth state and nonce; SAML InResponseTo; short assertion/token lifetimes.
- Audience and recipient
  - Lock assertions/tokens to intended client/RP; reject mismatches.
- Cookie hygiene
  - HttpOnly, Secure, SameSite=Lax/Strict. CSRF protections for cookie‑based apps.
- CORS and headers (for APIs)
  - Avoid wildcard origins with credentials; require custom headers to enforce preflight.

---

## Operational Practices

- Logging and audit
  - Centralize IdP and app logs: login successes/failures, MFA prompts/denials, token issuance, assertion consumption.
  - Record attribute mappings and role assignments; maintain change history.
- Monitoring
  - Alert on anomalies (impossible travel, brute force, abnormal IdP‑initiated launches).
- Key/cert rotation
  - Track expiry; automate JWKS refresh; support key overlap; test rotations.
- DR and break‑glass
  - Minimal, hardware‑protected break‑glass accounts; test quarterly; tight alerting.

---

## Common Pitfalls

- Accepting tokens/assertions without validating audience/issuer/expiry.
- Long‑lived sessions/refresh with no rotation or revocation.
- Over‑reliance on IdP groups for granular auth → role explosion and drift.
- IdP‑initiated flows without RelayState/state checks → open redirects.
- No automated deprovisioning (SCIM) → orphaned accounts and access.
- Exposing tokens to browsers in SPAs instead of using a BFF.

---

## Checklists

Integration
- [ ] Protocol chosen appropriately (OIDC preferred; SAML for legacy).
- [ ] Strong validation of signature, iss/aud/exp/nbf (OIDC) or audience/recipient/conditions (SAML).
- [ ] OAuth state and nonce (OIDC); InResponseTo (SAML) verified.

Security
- [ ] MFA enforced; phishing‑resistant for admins/privileged apps.
- [ ] Session cookies Secure, HttpOnly, SameSite; CSRF protections in place.
- [ ] Short access token TTL; refresh rotation and revocation on anomaly.

Provisioning
- [ ] SCIM enabled for joiner/mover/leaver; JIT only when necessary.
- [ ] Attribute/role mapping minimal and documented; tenant context explicit.

Operations
- [ ] Central logging across IdP and apps; anomaly alerts tuned.
- [ ] Key/cert rotation tested; discovery/JWKS refresh monitored.
- [ ] Break‑glass accounts protected, monitored, and tested.

---

## Cross‑References

- SSO overview: [/docs/security/iam/sso.md](./sso.md)
- MFA: [/docs/security/iam/mfa.md](./mfa.md)
- Authorization models: [/docs/security/iam/rbac-abac.md](./rbac-abac.md)
- Zero Trust principles: [/docs/security/iam/least-privilege-zero-trust.md](./least-privilege-zero-trust.md)
