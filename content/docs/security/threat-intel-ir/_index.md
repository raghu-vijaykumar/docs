---
title: "Threat Intelligence & Incident Response"
draft: false
---

# Threat Intelligence & Incident Response

Threat Intelligence (TI) and Incident Response (IR) transform raw signals into decisions and durable improvements. TI curates indicators and TTPs that matter to your environment; IR prepares the organization to detect, triage, contain, eradicate, recover, and learn from incidents with minimal impact.

This section provides an operating model, lifecycle, artifacts, and cross-links to SIEM, hunting, and forensics. It is designed to be actionable and evidence-driven.

Subpages
- Threat Hunting & IOCs: [/docs/security/threat-intel-ir/threat-hunting-ioc.md](/docs/security/threat-intel-ir/threat-hunting-ioc.md)
- SIEM: [/docs/security/threat-intel-ir/siem.md](/docs/security/threat-intel-ir/siem.md)
- Incident Response Lifecycle: [/docs/security/threat-intel-ir/incident-response-lifecycle.md](/docs/security/threat-intel-ir/incident-response-lifecycle.md)
- Forensics & Log Analysis: [/docs/security/threat-intel-ir/forensics-log-analysis.md](/docs/security/threat-intel-ir/forensics-log-analysis.md)

Cross-refs
- CSPM/CNAPP: [/docs/security/cloud-security/cspm.md](/docs/security/cloud-security/cspm.md)
- Cloud Best Practices: [/docs/security/cloud-security/cloud-best-practices.md](/docs/security/cloud-security/cloud-best-practices.md)
- Kubernetes Security: [/docs/security/os-security/kubernetes-security.md](/docs/security/os-security/kubernetes-security.md)
- SSDLC: [/docs/security/application-security/ssdlc.md](/docs/security/application-security/ssdlc.md)

---

## Objectives

- Reduce Mean Time To Detect (MTTD) and Mean Time To Recover (MTTR).
- Prioritize high-impact threats with context from your assets, identities, and data sensitivity.
- Produce audit-ready evidence of detections, decisions, and remediation effectiveness.
- Convert incidents into improvements across controls, playbooks, and developer practices.

---

## Operating Model

- Intelligence intake
  - Sources: vendor feeds, ISACs/ISOs, open threat intel, SOC findings, red team output, bug bounty.
  - Curation: normalize, de-duplicate, score by relevance (industry, tech stack, exposure).
- Detection engineering
  - Turn TTPs into detections: rules, analytics, and behavioral models with tests and ownership.
  - Stage new detections in monitor mode; measure precision/recall before enforcement.
- Incident response
  - Preparedness: playbooks, on-call rotations, communications plan, forensics readiness, tabletop exercises.
  - Response: identify → contain → eradicate → recover → lessons learned.
- Continuous improvement
  - Post-incident reviews drive rule tuning, control changes, and SSDLC requirements.

---

## Roles and Responsibilities (RACI concept)

- Detection Engineering (D)
  - Build and maintain SIEM rules, pipelines, and coverage metrics.
- SOC/Blue Team (R)
  - Monitor, triage, escalate, and execute playbooks; maintain IR hygiene.
- Incident Commander (A)
  - Owns the incident; coordinates comms and decisions; delegates workstreams.
- Forensics (C/R)
  - Evidence collection, chain of custody, analysis, timeline reconstruction.
- SRE/Operations (R)
  - Containment actions (isolate, rotate, block); restore services per RTO/RPO.
- Legal/Privacy/Comms (C/A for external comms)
  - Regulatory notifications, customer comms, and evidence preservation guidance.
- Product/Engineering (C/R)
  - Remediation PRs, control changes, and rollout; SSDLC updates.

---

## Lifecycle Overview

1) Preparation
   - Asset inventory, runbooks, access to systems/logs, golden images, and tooling validated.
2) Detection & Analysis
   - Alerts and intel triaged with context; hypotheses built; scope and priority set.
3) Containment
   - Short-term (isolate, block indicators), then long-term (patch, rotate, reconfigure).
4) Eradication
   - Remove malware/backdoors, revoke persistence, fix vulnerabilities.
5) Recovery
   - Restore from clean state; validate with canaries and heightened monitoring.
6) Lessons Learned
   - Postmortem; update detections, controls, playbooks; track actions to closure.

Metrics
- MTTD, MTTR, detection precision, false positive rate, time-to-contain (MTTC), control effectiveness (% blocked pre-impact).

---

## Evidence and Artifacts

- Case records
  - Timeline, scope, indicators, decisions, containment steps, eradication, recovery, postmortem actions.
- Technical evidence
  - Logs (immutable/WORM), packet captures, memory/disk images, config snapshots, hashes, SBOMs/provenance.
- Governance
  - Approval records for customer/regulatory communications, exception handling, and control changes.

Retention
- Align with legal/regulatory requirements; maintain chain of custody; document access.

---

## Detections to Prioritize

- Identity and access
  - Privilege escalations, new owner-level grants, mass failed logins, anomalous OAuth/STS/WIF usage.
- Data exfiltration
  - Public storage creation, large egress bursts, unusual destinations, bypassing private endpoints.
- Runtime and infrastructure
  - Reverse shells, crypto miners, unsigned image deployments, registry changes, KMS key disablement.
- Control plane tampering
  - Logging disabled, KMS key scheduled for deletion, policy weakened (SCP/org policy/NSG/Firewall).

---

## Playbooks (Examples)

- Credential compromise
  - Revoke sessions/tokens, rotate credentials, block source IPs, investigate lateral movement, force MFA re-enroll where needed.
- Public data exposure
  - Revert configuration via guardrails, rotate object ACLs/tokens, notify data owners, start breach triage if sensitive data.
- Ransomware/crypto miner
  - Isolate nodes/workloads, snapshot for evidence, terminate processes, restore from clean images, patch and rotate credentials.
- Supply chain integrity failure
  - Quarantine affected artifacts, block unverified signatures in admission, roll back, and reissue signed builds.

Each playbook includes severity matrix, roles, communications templates, run commands, rollback, and evidence collection steps.

---

## Testing and Preparedness

- Tabletop exercises
  - Simulate top risks quarterly; involve cross-functional teams; record gaps and action items.
- Red/purple teaming
  - Emulate relevant ATT&CK techniques; validate detection coverage and response readiness.
- Drills
  - Backup restore tests, KMS key compromise drills, “public bucket” guardrail validation, token revocation speed.

---

## Common Pitfalls

- Alert fatigue with no ownership or tuning; high MTTD due to noisy rules.
- Over-reliance on blocking without staged validation → outages.
- Missing forensics readiness: no immutable logs, no memory snapshot process, no chain of custody.
- Incomplete communications plans; late legal/privacy involvement.
- Postmortems without action tracking; no feedback loop into SSDLC and controls.

---

## Checklist

- [ ] Up-to-date playbooks with owners and escalation paths; on-call rotations staffed.
- [ ] SIEM detections for identity escalation, exfiltration, runtime anomalies, and control-plane tampering.
- [ ] Immutable logging and forensics procedures; evidence retention and chain of custody defined.
- [ ] Guardrails (SCP/Org Policy/Azure Policy) to prevent common posture failures; auto-remediation enabled.
- [ ] Regular table-top exercises and red/purple team validations; gaps tracked to closure.
- [ ] Metrics (MTTD/MTTR/precision) reported to leadership; continuous improvement loop active.

---

## Cross-References

- SIEM: [/docs/security/threat-intel-ir/siem.md](/docs/security/threat-intel-ir/siem.md)
- Incident Response Lifecycle: [/docs/security/threat-intel-ir/incident-response-lifecycle.md](/docs/security/threat-intel-ir/incident-response-lifecycle.md)
- Threat Hunting & IOCs: [/docs/security/threat-intel-ir/threat-hunting-ioc.md](/docs/security/threat-intel-ir/threat-hunting-ioc.md)
- Forensics & Log Analysis: [/docs/security/threat-intel-ir/forensics-log-analysis.md](/docs/security/threat-intel-ir/forensics-log-analysis.md)
