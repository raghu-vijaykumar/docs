---
title: "CIA Triad (Confidentiality, Integrity, Availability)"
draft: false
---

# CIA Triad (Confidentiality, Integrity, Availability)

The CIA Triad is a foundational model that frames security goals as protecting data and systems along three axes: confidentiality (no unauthorized disclosure), integrity (no unauthorized or undetected modification), and availability (usable when needed). Real systems continuously trade and balance these goals based on risk, business impact, and compliance requirements.

---

## Summary

- Confidentiality: prevent unauthorized access or disclosure.
- Integrity: prevent unauthorized changes and detect tampering or corruption.
- Availability: ensure reliable, timely access to systems and data.

Each axis maps to specific controls, metrics, and failure modes. Designs should state explicit objectives and SLOs for each, then implement layered controls with evidence and monitoring.

---

## Confidentiality

Definition
- Only authorized principals access sensitive data; data exfiltration and side-channel leakage are minimized.

Key controls
- Data-at-rest encryption with KMS/HSM; envelope encryption; separate DEK/KEK.
- Data-in-transit encryption (TLS 1.2/1.3), mTLS internally for sensitive paths.
- Strong authentication and robust authorization (RBAC/ABAC), least privilege, JIT elevation.
- Network segmentation, private endpoints, egress allow-lists; avoid public exposure by default.
- Secrets management (vaults, cloud secret stores), rotation and short TTLs.
- Data minimization, masking/tokenization; privacy controls (purpose limitation).

Metrics and evidence
- Access reviews, last-used reports for roles/keys.
- Logs of key usage (KMS), failed/denied authZ, anomaly alerts on large transfers.
- Encryption posture reports (all storage encrypted, key rotation policies).
- Data classification inventory and access pathways mapped.

Common pitfalls
- Long-lived credentials in code/CI; over-broad roles; public storage due to drift; missing egress control.

Cross-refs: [IAM](/docs/security/iam/_index.md), [PKI](/docs/security/cryptography/pki/), [TLS/SSL](/docs/security/cryptography/tls-ssl/), [Cloud IAM](/docs/security/cloud-security/cloud-iam.md)

---

## Integrity

Definition
- Data and execution remain correct, consistent, and tamper-evident; unauthorized or accidental changes are prevented or detected.

Key controls
- Authenticated encryption (AEAD) and MACs (HMAC) for messages and payloads.
- Digital signatures for artifacts, releases, and tokens; provenance/attestation (SLSA).
- Checksums and hashing (SHA-256/512) for files and backups; WORM storage for logs.
- Database constraints, immutability patterns (event sourcing), and write-ahead logging.
- Access controls at the data layer (row/tenant predicates); input validation and schema enforcement.
- Supply chain controls: SBOM, signature verification at deploy, policy admission.

Detection and monitoring
- Tamper-evident logs (hash chains, append-only storage); integrity verification jobs.
- Drift detection (IaC vs runtime), change approvals, anomaly detection on critical tables.

Common pitfalls
- Encrypting without authentication (e.g., AES-CBC w/o MAC); unsigned artifacts; mutable logs; implicit trust in CI artifacts.

Cross-refs: [Symmetric vs Asymmetric](/docs/security/cryptography/symmetric-vs-asymmetric.md), [SSDLC](/docs/security/application-security/ssdlc.md)

---

## Availability

Definition
- Systems provide timely and reliable service within agreed SLOs, resilient to component failures, load spikes, and attacks.

Key controls
- Redundancy and multi-AZ/region architectures; failover and disaster recovery plans.
- Autoscaling, rate limiting, backpressure, and queueing; load balancing.
- DDoS protection (edge/CDN/WAF), circuit breakers, retries with jitter, timeouts.
- Dependency isolation (bulkheads), graceful degradation, and feature flags.
- Backup/restore procedures with tested RTO/RPO; capacity planning and chaos testing.

Monitoring and preparedness
- SLOs/SLIs (latency, error rate, saturation); synthetic probes and canaries.
- Incident response runbooks for capacity, dependency, and edge failures.
- Game days and chaos experiments to validate recovery paths.

Common pitfalls
- Single-region deployments; tight coupling with stateful dependencies; no egress control enabling resource exhaustion; untested restores.

Cross-refs: [Network Security](/docs/security/network-security/), [Cloud Security](/docs/security/cloud-security/_index.md)

---

## Tensions and Trade-offs

- Confidentiality vs Availability
  - Stronger access controls (MFA, approvals) increase MTTR if not operationalized; use break-glass with strict guardrails and audit.
- Integrity vs Availability
  - Strict validation/policies can cause deny storms; stage rollout with dry-run, observability, and progressive enforcement.
- Availability vs Confidentiality
  - Aggressive caching/CDNs can broaden data exposure if tokenization and cache keys are weak; bind caches to identity/tenant when needed.

Design approach
- Make trade-offs explicit with risk acceptance and compensating controls.
- Use threat modeling to document abuse cases and operational mitigations.

---

## Practical Examples

Web application
- Confidentiality: TLS 1.3 everywhere; secrets in vault; least-privilege DB role; per-tenant row filters.
- Integrity: AEAD for session cookies; signed builds (cosign) with admission policies; schema validation and idempotency keys.
- Availability: autoscaling; rate limits; WAF/CDN; graceful degradation for non-critical features.

Data platform
- Confidentiality: KMS per-domain; access via temporary workload identities; private endpoints.
- Integrity: immutability (append-only logs), checksums for files, data lineage with signed manifests.
- Availability: partitioned processing, replayable pipelines, regional DR with tested RTO/RPO.

---

## Threat Mapping

- Confidentiality threats: credential theft, lateral movement, egress exfiltration, bucket exposure, MITM, SSRF to metadata.
- Integrity threats: supply chain tampering, SQL injection, log tampering, rollout of unsigned images, malicious insider changes.
- Availability threats: DDoS, resource exhaustion (unbounded fan-out), dependency outage, region failure, lock contention.

Map each threat to layered controls in all three axes; avoid single points of failure or single-control dependency.

---

## Measurement and SLO Hints

- Confidentiality SLOs: zero unauthorized access events; time to revoke compromised credentials; coverage % of encrypted resources.
- Integrity SLOs: % of artifacts signed and verified; frequency of drift/tamper detections; mean time to detect (MTTD) integrity violations.
- Availability SLOs: uptime %, p99 latency, error budgets, MTTR; tested restore frequency and success rate.

---

## Checklist

- [ ] Data classification and access pathways documented; least-privilege IAM implemented.
- [ ] All storage encrypted with KMS; TLS 1.2/1.3 in transit; secrets managed and rotated.
- [ ] AEAD/HMAC for integrity; signed artifacts and verified deployments; WORM/append-only logs.
- [ ] Multi-AZ/region design where justified; DDoS/WAF, rate limits, autoscaling, graceful degradation.
- [ ] Backups with periodic restore tests; DR runbooks; defined RTO/RPO.
- [ ] Observability for each axis: access audit, tamper detection, SLOs with alerts.
- [ ] Documented trade-offs with compensating controls and periodic review.

---

## Cross-References

- Identity & Access: [/docs/security/iam/_index.md](/docs/security/iam/_index.md)
- Cryptography: [/docs/security/cryptography/_index.md](/docs/security/cryptography/_index.md)
- Network Security: [/docs/security/network-security/_index.md](/docs/security/network-security/_index.md)
- Cloud Security: [/docs/security/cloud-security/_index.md](/docs/security/cloud-security/_index.md)
- SSDLC and AppSec: [/docs/security/application-security/_index.md](/docs/security/application-security/_index.md)
