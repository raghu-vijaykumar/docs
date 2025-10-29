---
title: "Cloud Security"
weight: 7
bookCollapseSection: true
draft: false
---

# Cloud Security

Cloud platforms shift parts of the operational burden to providers while introducing new risks: identity sprawl, misconfigurations at scale, multi‑account governance, and shared responsibility misunderstandings. This section focuses on provider‑agnostic patterns and concrete guardrails that apply across AWS, GCP, and Azure.

---

## Shared Responsibility Model

- Provider responsibilities
  - Physical DC security, underlying hardware, core networking, hypervisor, managed control planes.
- Customer responsibilities
  - Data security, identity & access, workload configuration, application code, network policy within accounts/projects/subscriptions.
- Service model nuance
  - IaaS: you own OS and above; PaaS: you own app configs and data; SaaS: you mostly own identity, data usage, and access policy.

Key takeaway
- You cannot outsource accountability. Map each control to either provider or customer, and verify implementations and evidence.

---

## Multi‑Account/Project/Sub Governance

- Isolate environments (dev/stage/prod) and tenants into separate accounts/projects/subscriptions.
- Use organizational policies/guardrails to enforce baseline (e.g., deny public buckets, require KMS).
- Centralize logging, billing, and security tooling per org; apply least‑privilege cross‑account roles.
- Break‑glass accounts with hardware MFA; tested and tightly monitored.

---

## Identity and Access in Cloud

- Prefer short‑lived, assumed roles (STS) or workload identities over long‑lived access keys.
- Tag‑ and attribute‑based access controls for tenant/resource scoping.
- Just‑in‑time elevation for admin actions; separate build/deploy roles from production ops.
- Rotate keys automatically; detect unused or over‑privileged roles and prune.

See: [IAM](../iam/) and [Least Privilege & Zero Trust](../iam/least-privilege-zero-trust.md)

---

## Data Protection and Encryption

- Encrypt at rest with KMS‑managed keys; separate DEK/KEK (envelope encryption).
- Encrypt in transit (TLS 1.2/1.3) with strong ciphers; use mTLS internally for sensitive workloads.
- Separate KMS admin from data admins; key usage is auditable and least‑privileged.
- Backup/restore tested; immutable backups for critical data; cross‑region copies with access controls.

Cross‑reference: [PKI](/docs/security/cryptography/pki/) and [TLS/SSL Configuration](/docs/security/cryptography/tls-ssl/)

---

## Network and Perimeter

- VPC/VNet design per environment; default‑deny security groups/NSGs; explicit allow within tiers.
- Private endpoints for managed services; egress via NAT or proxy with domain allow‑lists.
- Use provider DDoS protection and WAF/CDN at the edge; enforce HTTPS everywhere.
- Service mesh for east‑west identity (mTLS) and policy where appropriate.

See: [Network Security](/docs/security/network-security/)

---

## Workload Security

- Harden images (golden AMIs/VM images, container base images); keep OS and runtimes patched.
- Use managed services where possible to reduce operational surface (databases, queues, identity providers).
- Runtime protections: drop Linux capabilities, run as non‑root, read‑only FS, seccomp/apparmor; Pod Security Admission on K8s.
- Supply chain: SBOM, signed artifacts/images (cosign), provenance (SLSA), and verified deployments.

See: [Operating System Security](/docs/security/os-security/) and [Container Security](/docs/security/os-security/container-security.md)

---

## Cloud Storage Safety

- Block public access at org level; require explicit exception procedures.
- Uniform bucket/object ACLs; server‑side encryption by default; bucket keys where applicable.
- Object versioning and retention for recovery; lifecycle policies for data minimization.
- Monitor for public exposure continuously; alert on policy drifts.

---

## Posture Management (CSPM/CNAPP)

- Continuously scan for misconfigurations: open security groups, public buckets, overly permissive IAM, unencrypted resources.
- Track against baselines and frameworks (CIS Benchmarks, NIST CSF).
- Integrate with CI/CD to catch issues pre‑deploy; block high‑severity drifts automatically.

---

## Serverless and Managed Services

- Least privilege per function/service account; minimal network egress; parameterize environment secrets via managed stores.
- Event sources validated and authenticated; idempotency and replay protections.
- Cold start secrets: avoid embedding; fetch short‑lived credentials at runtime.
- Log invocations and errors centrally with sampling; alert on anomalies.

---

## Logging, Detection, and IR

- Centralize cloud audit logs (API calls, access events) and workload logs; protect integrity (WORM where possible).
- SIEM integration with detections for privilege escalation, cross‑region activity, mass deletions, and unusual network flows.
- Practice incident response: playbooks for key compromise, public data exposure, and misconfiguration rollback.

See: [Threat Intel & Incident Response](/docs/security/threat-intel-ir/)

---

## Compliance and Evidence

- Map controls to frameworks (ISO 27001, SOC 2, PCI DSS) and gather evidence automatically from cloud APIs.
- Use cloud‑native config recorders and conformance packs; export periodic reports.

See: [Compliance](/docs/security/compliance/)

---

## Guardrail Checklist

- [ ] Org‑level guardrails (deny public storage, require encryption, restricted regions).
- [ ] Multi‑account/project structure with centralized logging and billing.
- [ ] STS/workload identities; no long‑lived keys; JIT elevation for admins.
- [ ] KMS for all data‑at‑rest; key separation of duties; audited usage.
- [ ] Private endpoints; default‑deny SG/NSG; controlled egress; WAF/CDN at edge.
- [ ] Hardened images; patching pipelines; signed artifacts/images with verification.
- [ ] CSPM/CNAPP scanning in CI/CD and runtime; auto‑remediation for critical drift.
- [ ] Centralized audit and workload logs to SIEM with alerts; IR playbooks tested.
- [ ] Serverless permissions minimal; secrets managed; idempotency and replay checks.

---

## Where to Go Next

- Shared Responsibility Model details: [shared-responsibility.md](shared-responsibility.md)
- Cloud Security Best Practices: [cloud-best-practices.md](cloud-best-practices.md)
- Encryption and Storage: [encryption-storage.md](encryption-storage.md)
- Posture Management: [cspm.md](cspm.md)
- Cloud IAM: [cloud-iam.md](cloud-iam.md)
- Serverless Security: [serverless-security.md](serverless-security.md)
