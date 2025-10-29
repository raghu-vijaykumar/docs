---
title: "Secure Software Development Lifecycle (SSDLC)"
draft: false
---

# Secure Software Development Lifecycle (SSDLC)

SSDLC integrates security activities into every phase of delivery—from scoping and design through build, deploy, and operate. The outcome is predictable, auditable delivery with secure defaults, automated checks, and clear ownership, without throttling developer velocity.

This guide is implementation‑oriented: where each control sits, how to automate it, and what evidence it produces.

---

## Principles

- Shift left, verify continuously
  - Do the cheapest checks earliest (threat modeling, linting), and continue verifying in CI/CD and runtime.
- Secure by default
  - Scaffolds and templates carry opinionated secure configs (linters, CSP, TLS, policies).
- Automate everything
  - Scanners, tests, provenance, signatures, and policy gates run on every change.
- Least privilege across the stack
  - Identity, infra, runtime, and CI credentials are scoped and short‑lived.
- Evidence‑driven
  - Each control emits auditable artifacts (reports, attestations, logs).

---

## Lifecycle Stages and Controls

### 1) Inception & Design

Activities
- Lightweight threat modeling per feature (30–60 minutes).
- Data classification, privacy considerations (PII/PHI/PCI).
- Architecture: trust boundaries, identity/authorization plan, rate limits, isolation.

Artifacts
- Threat model notes with assumptions, mitigations, abuse‑case tests.
- Architecture diagram, data flows, “shall” security requirements.

Cross‑ref: [Application Security overview](./_index.md), [Least Privilege & Zero Trust](/docs/security/iam/least-privilege-zero-trust.md)

---

### 2) Development

Controls
- Secure coding guidelines (language/framework specific).
- Linters and SAST in editor and pre‑commit (ESLint security rules, Bandit, Gosec).
- Secrets prevention (pre‑commit hooks: detect‑secrets, gitleaks).
- Dependency policy and updates (pin versions; renovate/dependabot with OSS indexers).
- Input validation libraries and patterns embedded in scaffolds.

Example pre‑commit (Python)
```ini
repos:
- repo: https://github.com/Yelp/detect-secrets
  rev: v1.5.0
  hooks:
  - id: detect-secrets
- repo: https://github.com/PyCQA/bandit
  rev: 1.7.9
  hooks:
  - id: bandit
    args: ["-ll", "-ii"]
```

---

### 3) Build (CI)

Controls
- Reproducible builds on ephemeral runners; minimal permissions.
- SAST and secrets scan on PRs; fail on high severity.
- SCA: dependency vulnerability and license compliance.
- IaC scanning (Terraform/K8s) for misconfigurations.
- Unit/integration/security tests (abuse cases; negative tests).
- SBOM generation (CycloneDX/SPDX).

Example GitHub Actions (excerpt)
```yaml
name: ci
on: [pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: read
      id-token: write # for OIDC -> short-lived cloud creds
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with: { node-version: 20 }
      - run: npm ci
      - run: npm run lint && npm test
      - name: SAST
        run: npx @sonarsource/sonarqube-scan
      - name: Secrets scan
        uses: gitleaks/gitleaks-action@v2
      - name: SCA
        run: npm audit --audit-level=high || true # report-only gate below
      - name: SBOM
        run: npx @cyclonedx/cdxgen -o sbom.json
```

---

### 4) Artifact Security and Provenance

Controls
- Build attestations and provenance (SLSA‑style).
- Artifact signing (Sigstore/cosign) and verification on deploy.
- Store SBOM alongside artifact; publish to registry with signatures.

Cosign example
```bash
# sign
cosign sign --key cosign.key ghcr.io/org/app:1.2.3
# verify in deploy step
cosign verify --key cosign.pub ghcr.io/org/app:1.2.3
```

Attestation (provenance)
```bash
cosign attest --predicate provenance.json --type slsaprovenance --key cosign.key ghcr.io/org/app:1.2.3
cosign verify-attestation --type slsaprovenance --key cosign.pub ghcr.io/org/app:1.2.3
```

---

### 5) Deployment (CD)

Controls
- Policy as code gates: only deploy artifacts with valid signature, attestation, and SBOM present.
- Runtime config validation (OpenAPI schema; feature flags bounds; env var allow‑lists).
- Infrastructure changes via IaC and change review; no manual drift.

Admission control (Kubernetes, policy concept)
```yaml
apiVersion: kyverno.io/v1
kind: ClusterPolicy
metadata: { name: verify-images }
spec:
  rules:
  - name: cosign-verify
    match: { resources: { kinds: ["Pod"] } }
    verifyImages:
    - imageReferences: ["ghcr.io/org/*"]
      attestors:
      - entries:
        - keys: { publicKeys: |-
            -----BEGIN PUBLIC KEY-----
            ...
            -----END PUBLIC KEY----- }
```

Network & runtime hardening
- Namespaces, NetworkPolicies (default deny), Pod Security Admission (restricted), seccomp/apparmor, read‑only FS, drop capabilities.

Cross‑ref: [Containers/Kubernetes](/docs/security/os-security/)

---

### 6) Post‑Deploy Verification

Controls
- DAST in staging with smoke tests and auth flows.
- API fuzzing on non‑prod envs using schemas (REST, gRPC).
- Canary releases and error budgets; security telemetry dashboards.

---

### 7) Operations & Monitoring

Controls
- Centralized logs (app + infra) with integrity protection; security events (auth failures, rate‑limit hits, policy denies).
- Metrics: 4xx/5xx by route, authZ denies, token verification failures, p99 latency.
- Traces: correlate user/session and policy checks.
- Alerts for anomalous access, mass deletes, permission changes.

Cross‑ref: [Threat Intel & IR](/docs/security/threat-intel-ir/)

---

## Security Gates and Policies

Set objective gates that fail builds/deploys only on meaningful risk while keeping iteration fast.

Typical gates
- SAST: High/Critical → block; Medium → warn.
- Secrets: any → block.
- SCA: exploited/high with no fix → block; known fix → require upgrade window.
- IaC: public S3/buckets, open security groups → block.
- Image: unsigned/attestation missing → block.
- Test coverage for authZ/tenant checks → must meet threshold.

OPA/Rego deploy gate (concept)
```rego
package deploy.gate

default allow = false

allow {
  input.image.signature_verified
  input.image.attestation_verified
  not input.sca.high_unresolved
  not input.iac.critical
}
```

---

## Roles and Ownership

- Product/team owns threat model and security requirements.
- Developers own remediation of SAST/SCA findings in their code.
- Platform/SRE owns CI/CD hardening, signing, policy engines, and admission controls.
- Security provides policies, tuning, exceptions, and reviews; maintains detections and IR playbooks.

---

## Evidence and Audit

For each release:
- Threat model snapshot and requirement checklist.
- CI reports: SAST, secrets, SCA, IaC, test results.
- SBOM and signature/attestation verification logs.
- Change approvals and exception records (time‑boxed).
- Runtime posture: NetworkPolicy/PSA summaries, active policies.

Store artifacts in a tamper‑evident store (e.g., write‑once bucket, versioned).

---

## Common Pitfalls

- Scanners without ownership or SLAs → backlog grows and becomes noise.
- Overly broad gates that block iteration; developers route around the process.
- No provenance/signature checks at deploy → supply chain gap persists.
- Secrets in CI variables persistent for years; lack of OIDC/STSs for ephemeral creds.
- Runtime policies not aligned with app identity → deny storms or silent over‑permissive.

---

## Checklist

Design
- [ ] Threat model completed and updated with architecture changes.
- [ ] Security requirements documented (authN/Z, rate limits, isolation, logging).

Development
- [ ] Linters and SAST in editor/PR; secure coding guidelines applied.
- [ ] Pre‑commit secrets scanning; dependency pinning and update automation.

Build
- [ ] SAST, secrets, SCA, IaC scans on PR with clear severity gates.
- [ ] SBOM generated per build; stored with artifact.

Supply chain
- [ ] Artifact signatures and provenance (SLSA‑style) created and verified.
- [ ] Registry enforces signed images; admission controllers verify in cluster.

Deploy
- [ ] Policy gates enforced (image trust, IaC checks).
- [ ] Runtime hardening (PSA, NetworkPolicy, seccomp, read‑only FS, non‑root).

Operate
- [ ] Centralized logs/metrics/traces with security signals and alerts.
- [ ] IR playbooks tested; canary and rollback procedures defined.

---

## Cross‑References

- API security: [/docs/security/application-security/api-security.md](./api-security.md)
- OWASP Top 10: [/docs/security/application-security/](./_index.md)
- Cryptography & TLS: [/docs/security/cryptography/](../cryptography/_index.md)
- IAM and Zero Trust: [/docs/security/iam/](../iam/_index.md)
- OS/Container hardening: [/docs/security/os-security/](../os-security/_index.md)
- Cloud guardrails: [/docs/security/cloud-security/](../cloud-security/_index.md)
