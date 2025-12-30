---
title: "Single Sign‑On (SSO)"
draft: false
---

# Single Sign‑On (SSO)

Single Sign‑On centralizes authentication so users sign in once with a trusted Identity Provider (IdP) and gain access to multiple applications. Properly implemented SSO improves security (central policy, MFA, device posture) and UX, while reducing password attack surface across apps.

This guide covers architecture, protocols (OIDC vs SAML), implementation patterns, and common pitfalls.

---

## Why SSO

- Security
  - Centralized MFA and risk policies; fewer passwords; faster offboarding.
  - Unified visibility and audit across apps; consistent session management.
- Productivity
  - One sign‑in for many apps; fewer account lockouts and helpdesk tickets.
- Governance
  - Central provisioning/deprovisioning (SCIM); access reviews and least‑privilege enforcement.

---

## Core Architecture

```mermaid
flowchart LR
  U[User + Browser] -->|Auth Request| App[Application (Client/RP/SP)]
  App -->|Redirect| IdP[Identity Provider]
  IdP -->|Authenticate (MFA/Device)| IdP
  IdP -->|Token/Assertion| App
  App -->|Session| U
  App -.-> Logs[Audit / SIEM]
```

Key roles
- IdP: Issues tokens/assertions (OIDC/SAML), enforces MFA, device posture, and risk‑based policies.
- Application: Verifies tokens, creates local session, enforces authorization.
- Directory/HR: Source of truth for identities and groups (SCIM provisioning).

---

## Protocols: OIDC vs SAML

OpenID Connect (OIDC)
- Modern, JSON/REST over OAuth 2.1; suitable for web/mobile APIs and SPAs.
- Tokens: ID Token (JWT), Access Token (JWT/Opaque). Metadata via well‑known discovery.
- Flows: Authorization Code + PKCE; Device Flow for CLI/TV; Client Credentials for service auth.
- Preferred for new integrations.

SAML 2.0
- XML, signed assertions posted via browser; common for legacy/enterprise SaaS.
- Strong but verbose; beware XML canonicalization pitfalls.
- Good when vendors only support SAML.

Decision guidelines
- Choose OIDC when both sides support it (better tooling and libraries).
- Use SAML for apps that only support SAML or require complex federation attributes.

---

## Initiation Modes

- Service Provider Initiated (SP‑initiated)
  - User starts at the app; app redirects to IdP and returns with token/assertion.
  - Better UX; easier deep links.
- IdP Initiated
  - User starts at IdP portal and launches apps.
  - Useful for discovery; ensure app validates state/RelayState to prevent open redirects.

---

## Security Controls

- MFA and Phishing Resistance
  - Enforce MFA at IdP; require WebAuthn/security keys for admins and sensitive apps.
- Device Posture
  - Gate high‑risk apps on managed devices; include posture claims in tokens when possible.
- Token Validation
  - Verify signature (JWKS for OIDC); check aud/iss/exp/nbf; short token lifetimes.
  - For SAML: verify signature, audience, recipient, NotBefore/NotOnOrAfter, InResponseTo.
- Session Management
  - Shorter lifetimes for admin apps; re‑auth for risky actions; bind session to device/browser.
- Replay and CSRF
  - Maintain OAuth state and nonce (OIDC); verify RelayState/binding (SAML); SameSite cookies for app sessions.

---

## Attribute Mapping and Authorization

- Map IdP attributes to app roles/tenants (e.g., groups, entitlements).
- Prefer group/role assertions at IdP; minimize custom per‑app roles to reduce drift.
- Enforce deny‑by‑default; evaluate authorization in app using identity and context.

OIDC claims example (ID Token)
```json
{
  "iss": "https://idp.example.com/",
  "aud": "api://dashboard",
  "sub": "00u1234abcd",
  "email": "alice@example.com",
  "groups": ["engineering", "prod-readers"],
  "amr": ["pwd", "fido2"],
  "acr": "urn:mace:incommon:iap:silver",
  "exp": 1735689600
}
```

SAML attribute mapping (conceptual)
```xml
<saml:Attribute Name="memberOf">
  <saml:AttributeValue>engineering</saml:AttributeValue>
  <saml:AttributeValue>prod-readers</saml:AttributeValue>
</saml:Attribute>
```

---

## Example: OIDC Authorization Code + PKCE (Web App)

1) App redirects to IdP with client_id, redirect_uri, scope, state, and PKCE challenge.
2) IdP authenticates (MFA), returns authorization code to redirect_uri.
3) App exchanges code + PKCE verifier for tokens at IdP token endpoint.
4) App validates ID Token and creates session; stores Access Token if calling APIs.

Security notes
- Store tokens server‑side; for SPAs use BFF pattern to avoid exposing tokens to the browser.
- Rotate refresh tokens; use short Access Token TTLs.

---

## Example: SAML (SP‑Initiated)

1) App generates AuthnRequest with RelayState; redirects to IdP SSO URL.
2) IdP authenticates; posts signed SAML Response/Assertion to app ACS URL.
3) App validates signature, audience, recipient, conditions; maps attributes; creates session.

Security notes
- Enforce signed responses and/or assertions; reject unsigned.
- Validate InResponseTo matches your stored request to prevent injection.

---

## Provisioning and Lifecycle (SCIM)

- Automate joiner/mover/leaver flows with SCIM 2.0.
- Provision attributes (email, groups, roles); deprovision accounts immediately on offboarding.
- Avoid orphaned access by relying on IdP as source of truth.

---

## Observability and Operations

- Centralize logs: auth successes/failures, MFA denials, token issuance, assertion consumption.
- Alert on anomalies: impossible travel, repeated denials, suspicious IdP‑initiated launches.
- Regular access reviews: verify group/role mappings, remove stale entitlements.

---

## Common Pitfalls

- Accepting tokens without verifying audience/issuer/expiry.
- Long‑lived sessions and refresh tokens with no rotation.
- IdP‑initiated SSO without validating RelayState → open redirect.
- Over‑broad attribute/role mappings granting unintended privileges.
- Not enforcing MFA for admins and privileged apps.

---

## Checklist

- [ ] OIDC preferred when available; SAML for legacy vendors.
- [ ] MFA enforced at IdP (phishing‑resistant for admins).
- [ ] Device posture checks for sensitive apps; context‑aware access.
- [ ] Strict validation of tokens/assertions (sig, aud, iss, exp, nbf).
- [ ] Short session TTLs for admin apps; step‑up auth for risky actions.
- [ ] Attribute mapping aligned to least privilege; deny‑by‑default.
- [ ] SCIM provisioning for lifecycle; immediate deprovision on offboarding.
- [ ] Comprehensive logging and anomaly detection across IdP and apps.

---

## References

- OpenID Connect Core, Discovery, and Dynamic Registration
- OAuth 2.1 (in draft), PKCE (RFC 7636), Token Exchange (RFC 8693)
- SAML 2.0 Core, Bindings, and Profiles
- SCIM 2.0 (RFC 7643/7644)
