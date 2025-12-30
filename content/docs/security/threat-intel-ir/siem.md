---
title: "Security Information & Event Management (SIEM)"
draft: false
---

# Security Information & Event Management (SIEM)

A SIEM ingests, normalizes, and analyzes security‑relevant telemetry to detect threats and provide evidence for investigations and compliance. Effective SIEM programs are built on high‑quality data, normalized schemas, detection engineering discipline, and measurable outcomes (precision, MTTD/MTTR), not just “more logs.”

Cross‑refs
- TI/IR index: [/docs/security/threat-intel-ir/_index.md](/docs/security/threat-intel-ir/_index.md)
- Threat Hunting & IOCs: [/docs/security/threat-intel-ir/threat-hunting-ioc.md](/docs/security/threat-intel-ir/threat-hunting-ioc.md)
- Incident Response Lifecycle: [/docs/security/threat-intel-ir/incident-response-lifecycle.md](/docs/security/threat-intel-ir/incident-response-lifecycle.md)
- Forensics & Log Analysis: [/docs/security/threat-intel-ir/forensics-log-analysis.md](/docs/security/threat-intel-ir/forensics-log-analysis.md)
- CSPM/CNAPP: [/docs/security/cloud-security/cspm.md](/docs/security/cloud-security/cspm.md)

---

## Objectives

- Centralize high‑value security telemetry with consistent schemas and retention.
- Turn TI and TTPs into versioned detections with ownership and tests.
- Reduce MTTD/MTTR via reliable alerts, triage context, and auto‑enrichment.
- Provide audit‑ready evidence (immutable storage, access controls, chain of custody).

---

## Data Ingestion and Normalization

Sources (baseline)
- Identity: IdP/SSO (OIDC/SAML), MFA, PAM, VPN/Zero Trust access.
- Cloud control plane: AWS CloudTrail, GCP Admin Activity/Cloud Audit, Azure Activity; CSPM findings.
- Network/edge: WAF, NGFW/Proxy, DNS, NetFlow/VPC Flow Logs, CDN events.
- Endpoint/Runtime: EDR/EPP, kernel/eBPF sensors (Falco/Tetragon), container runtime events.
- Application/API: gateway logs, authN/Z decisions, important business events.
- Data/KMS: storage access logs, DB audit, KMS key usage and policy changes.

Normalization
- Adopt a common schema (ECS/OCSF) for fields (src_ip, user, action, resource).
- Time sync: NTP everywhere; record timezone/offsets; prefer RFC3339 timestamps.
- Parse at the edge when possible (structured logs) to reduce SIEM parsing cost/complexity.

Retention tiers
- Hot (searchable): 30–90 days for detection and IR.
- Warm/Cold (cheaper): 180–365+ days for compliance and historical hunts.
- Immutable/WORM copies for forensics and legal holds.

---

## Detection Engineering

Principles
- Versioned rules in a repo; PR reviews; test data and unit tests where possible.
- Stage new rules (“monitor”) before “enforce” to measure precision/recall and volume.
- Ownership per rule with on‑call rotation, runbook, and severity.

Rule types
- Threshold/sequence: failed logins → success; mass permission changes; anomalous API create+delete.
- Behavioral analytics: rare consent grants, unusual geo/device posture, novel service account usage.
- Threat intel: IOC matches (IP/domain/hash/JA3), correlated with context to reduce FP.

Enrichment
- Asset inventory (owner, tenant, env, data classification).
- Identity data (group/role, device posture).
- Vulnerability context (is asset vulnerable/exposed?).
- Geo/IP reputation, ASN, URL category.

KPIs
- Precision/false positive rate.
- MTTD/MTTR; time to triage (MTTT).
- Rule coverage against prioritized ATT&CK techniques.

Example (conceptual YAML for a rule artifact)
```yaml
id: auth-geo-anomaly-001
title: "Login from unusual geo after failed attempts"
severity: high
signal:
  index: idp_auth
  query: >
    failed_logins_by_ip > 10 in 10m followed_by_success
    AND geo_country NOT IN allowed_countries[user]
enrichment:
  - user_groups
  - mfa_status
  - last_login_country
runbook: "docs/runbooks/auth-geo-anomaly.md"
owner: "sec-detection@company.com"
stage: monitor
```

---

## Alert Triage and Workflow

Workflow
- Alert → auto‑enrich → severity classification → ticket creation with owner and SLA.
- Suppression hygiene: time‑boxed suppressions; justification and expiry required.
- Feedback loop: update detection or add compensating controls (WAF/NGFW/CSPM/IAM).

Runbooks include
- Required context (asset owner, tenant, env), investigation steps, containment actions, and evidence checklist.

---

## Architecture and Scaling

- Pipeline pattern
  - Collect → Parse/Normalize → Enrich → Store → Detect → Alert → Ticket.
- Cost controls
  - Sample or exclude low‑value verbose logs; keep structured logs; push parsing left.
  - Tiered storage; archive to object storage with searchable indices for hot paths only.
- High availability
  - Ensure ingestion and alerting SLAs; backlog buffering; dead letter handling for malformed events.

---

## Content Lifecycle

- Source of truth: git repo with detections, tests, metadata, and coverage dashboards.
- CI for detections: validate syntax, run tests with sample logs, simulate volumes.
- Release notes: document rule changes; track impact on alert volume and precision.
- Coverage mapping: ATT&CK/NIST/ISO control mapping for audits.

---

## Common Detections to Prioritize

Identity/Access
- Privilege escalation (new owner/administrator grants, risky role assumptions).
- Impossible travel/geo‑velocity; disabled MFA; suspicious device posture.
- OAuth app consent anomalies; programmatic access from unusual ASNs.

Cloud/Posture
- Public storage creation; change to network rules (0.0.0.0/0); KMS key disabled/scheduled for deletion.
- Org policy/SCP weakening; logging disabled; new external IPs on sensitive assets.

Runtime/Network
- Reverse shell patterns, crypto miner behaviors; unsigned image deployments.
- DNS exfiltration patterns, rare destination ASNs, C2 beaconing intervals.

Data access
- Sudden spikes in reads of sensitive buckets/tables; cross‑tenant access attempts.

---

## Evidence and Compliance

- Access control: RBAC to SIEM, tamper‑evident logs, audit trails for queries/exports.
- Reports: detection efficacy, alert SLAs, exception registers, framework mappings (ISO/NIST/SOC2).
- Forensics: archive of raw logs and packet captures with retention and chain of custody.

---

## Common Pitfalls

- “Collect everything” without normalization → cost and noise; low signal.
- Rules deployed to block without monitor phase → outages and distrust.
- No ownership/runbooks → alerts languish; extended MTTR.
- Missing immutable storage → weak evidence; non‑compliance.
- No feedback loop into SSDLC/CSPM/IAM → repeat incidents.

---

## Checklist

- [ ] High‑value sources onboarded and normalized (identity, cloud, network, endpoint, app, KMS).
- [ ] Versioned detections with tests, owners, runbooks, and coverage mapping.
- [ ] Staged rollout (monitor → enforce); precision and volume tracked.
- [ ] Auto‑enrichment with asset/identity/vuln context; ticketing integration with SLAs.
- [ ] Tiered retention with immutable/WORM archives; access logged.
- [ ] Regular reporting of MTTD/MTTR and detection efficacy; post‑incident updates applied.

---

## Cross‑References

- TI/IR index: [/docs/security/threat-intel-ir/_index.md](/docs/security/threat-intel-ir/_index.md)
- Threat Hunting & IOCs: [/docs/security/threat-intel-ir/threat-hunting-ioc.md](/docs/security/threat-intel-ir/threat-hunting-ioc.md)
- Incident Response: [/docs/security/threat-intel-ir/incident-response-lifecycle.md](/docs/security/threat-intel-ir/incident-response-lifecycle.md)
- Forensics: [/docs/security/threat-intel-ir/forensics-log-analysis.md](/docs/security/threat-intel-ir/forensics-log-analysis.md)
