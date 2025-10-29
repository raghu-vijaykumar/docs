---
title: "Application Security"
weight: 4
bookCollapseSection: true
draft: false
---

# Application Security

Application Security (AppSec) turns principles into code: preventing injection, controlling identity, validating inputs/outputs, protecting secrets, and shipping software safely. The goal is to reduce exploitability while preserving developer velocity—by setting secure defaults and automating checks throughout the lifecycle.

This page lays out core practices and how to integrate them into your delivery pipeline. Deeper topics (OWASP Top 10, API security, SSDLC) are linked for focused guidance.

---

## Threat Modeling in Practice

Start every significant feature with a fast, repeatable threat model:
- Assets and trust boundaries: user input points, serialization/deserialization, network egress, secrets use.
- Attacker goals and paths: injection, auth bypass, data exfiltration, misuse of privileged flows.
- Controls: validation/encoding, authentication/authorization, rate limiting, isolation, auditing.
- Residual risk and tests: abuse-case tests, negative testing, and runbooks.

Keep this exercise light (30–60 minutes); log assumptions and decisions. Update it when architecture changes.

---

## Secure Coding Fundamentals

Input handling
- Validate inputs against strict schemas (types, ranges, formats); reject by default.
- Canonicalize before validation when parsing encodings; avoid double-decoding.
- Prefer allow-lists over block-lists.

Output encoding
- HTML, attribute, URL, and JavaScript contexts require different encoders to prevent XSS.
- Use templating frameworks with auto-escaping; only allow safe-by-default rendering.

Secrets and sensitive data
- Never log secrets or full tokens; redact by pattern.
- Store secrets in managed stores; rotate automatically; use short-lived credentials.
- Use [Hashing and Password Storage](/docs/security/cryptography/hashing/) for credentials.

Dependency and supply chain hygiene
- Pin versions; use lockfiles; enable SCA (e.g., Dependabot/Renovate + OSS indexers).
- Prefer minimal, well-maintained libraries; remove unused deps.
- Verify artifacts and provenance (SBOM, signatures). See SSDLC below.

Error handling and logging
- Return generic error messages to users; log detailed diagnostics securely.
- Structure logs (JSON), include request IDs, and avoid PII unless necessary.
- Ensure logs are immutable and integrity protected.

Configuration
- Secure-by-default configs; avoid permissive flags in production (e.g., CORS * ;* 0.0.0.0 debug).
- Fail closed where feasible; circuit breakers instead of silent retries.

---

## OWASP Top 10 (Focused Guides)

Common, impactful classes of vulnerabilities with practical remediation:
- Injection (SQL/NoSQL/OS): parameterized queries, ORM safe APIs — [SQL Injection](owasp-top-10/sql-injection.md)
- Cross-Site Scripting (XSS): contextual output encoding, CSP — [XSS](owasp-top-10/xss.md)
- Cross-Site Request Forgery (CSRF): SameSite cookies, CSRF tokens, double-submit — [CSRF](owasp-top-10/csrf.md)
- Broken Authentication & Authorization: phishing-resistant MFA, short-lived tokens, centralized authZ — [Broken AuthZ](owasp-top-10/broken-authz.md)

These pages include exploit anatomy, secure defaults, and verification recipes.

---

## API Security

Design
- Prefer OAuth 2.1 / OIDC for user auth; keep custom auth minimal.
- For internal service-to-service, use mTLS identities and scoped tokens (or SPIFFE/Spire identities).
- Version your APIs; publish OpenAPI schemas; validate requests and responses against schema.

Tokens and sessions
- Use asymmetric JWTs with key rotation via JWKS; constrain audience/scope; short TTLs with refresh flows.
- Bind refresh tokens to device/session context; revoke on anomaly.
- For sessions, use Secure, HttpOnly cookies with SameSite=Lax/Strict; regenerate on privilege changes.

Runtime protections
- Rate limiting and global concurrency limits per principal (user/app/IP).
- Idempotency keys for mutating endpoints to prevent replay effects.
- Response hardening: avoid reflection of user-controlled data, strip stack traces.

Transport and discovery
- Enforce HTTPS with modern TLS; consider [TLS/SSL Configuration](/docs/security/cryptography/tls-ssl/).
- Avoid information leakage via error codes and headers; restrict CORS carefully.
- Document auth flows clearly; publish scopes and token lifetimes.

See the dedicated guide: [API Security](api-security.md)

---

## Secure Software Development Lifecycle (SSDLC)

Shift-left controls integrated from design through operations:

Design and code
- Threat modeling and secure defaults at inception.
- Language/framework guidelines and linters (e.g., ESLint security rules, Bandit, Gosec).
- Code review checklists including authZ boundaries, input/output handling, and secret use.

Automated analysis
- SAST for source scans (in PRs).
- SCA for dependency vulnerabilities and license constraints.
- IaC scanning (Terraform/K8s) for misconfigurations (open security groups, public buckets).
- DAST for deployed environments in staging.

Build, provenance, and signing
- Reproducible builds, pinned toolchains, ephemeral runners.
- SBOM generation (CycloneDX/SPDX); artifact signing (Sigstore/cosign).
- Policy to only deploy verified artifacts with attestation.

Secrets and keys
- No secrets in code or images; use workload identity or dynamic secrets (Vault).
- Key management via KMS/HSM; rotate automatically; audit usage.

Operations and monitoring
- Centralized logging with alerting; application-level security telemetry (auth failures, unusual rates).
- Runtimes with minimal privileges; sandbox potentially dangerous operations.
- Incident response ready: playbooks for auth bypass, injection, and data exposure.

See: [SSDLC](ssdlc.md)

---

## Minimal Examples

Input validation (Express.js with zod)
```js
import express from "express";
import { z } from "zod";

const app = express();
app.use(express.json());

const CreateSchema = z.object({
  name: z.string().min(1).max(100),
  email: z.string().email(),
  age: z.number().int().min(13).max(120)
});

app.post("/users", (req, res) => {
  const parsed = CreateSchema.safeParse(req.body);
  if (!parsed.success) return res.status(400).json({ error: "invalid input" });
  // proceed with parsed.data
  res.status(201).json({ ok: true });
});

app.listen(3000);
```

Rate limiting (per user, with fallback to IP)
```js
import rateLimit from "express-rate-limit";

const limiter = rateLimit({
  windowMs: 60_000,
  max: (req) => req.user ? 200 : 60,
  standardHeaders: true,
  legacyHeaders: false
});
app.use("/api/", limiter);
```

JWT verification (aud, iss, exp, nbf checks; JWKS rotation)
- Use libraries that support JWKS fetch-and-cache with kid selection.
- Enforce audience and issuer, reject none/weak algorithms, and short max token age.

---

## Cross‑References

- Transport security: [TLS/SSL Configuration](/docs/security/cryptography/tls-ssl/)
- Credentials: [Hashing and Password Storage](/docs/security/cryptography/hashing/)
- Certificates/PKI: [PKI](/docs/security/cryptography/pki/)
- Network protections: [Network Security](/docs/security/network-security/)

---

## Where to Go Next

- OWASP Top 10:
  - [SQL Injection](owasp-top-10/sql-injection.md)
  - [XSS](owasp-top-10/xss.md)
  - [CSRF](owasp-top-10/csrf.md)
  - [Broken Auth/Authorization](owasp-top-10/broken-authz.md)
- API hardening and token design: [API Security](api-security.md)
- Delivery at scale with built‑in security: [SSDLC](ssdlc.md)
