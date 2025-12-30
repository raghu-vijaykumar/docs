---
title: "Cloud Security Posture Management (CSPM/CNAPP)"
draft: false
---

# Cloud Security Posture Management (CSPM/CNAPP)

CSPM continuously evaluates cloud configurations against security baselines and policies to catch misconfigurations before and after deployment. CNAPP extends CSPM with workload and runtime context (containers, serverless, identities) to prioritize real risk. The objective is simple: prevent high‑impact misconfigurations, reduce blast radius, and produce auditable evidence of control effectiveness.

Cross‑refs:
- Cloud Overview: [/docs/security/cloud-security/_index.md](/docs/security/cloud-security/_index.md)
- Cloud Best Practices: [/docs/security/cloud-security/cloud-best-practices.md](/docs/security/cloud-security/cloud-best-practices.md)
- Cloud IAM: [/docs/security/cloud-security/cloud-iam.md](/docs/security/cloud-security/cloud-iam.md)
- Encryption & Storage: [/docs/security/cloud-security/encryption-storage.md](/docs/security/cloud-security/encryption-storage.md)
- SSDLC: [/docs/security/application-security/ssdlc.md](/docs/security/application-security/ssdlc.md)

---

## What CSPM/CNAPP Covers

- Resource configuration
  - Storage exposure (public buckets), network exposure (open SG/NSG, public IPs), encryption disabled, missing logging.
- Identity posture
  - Over‑permissive IAM roles/policies, stale credentials, unused or long‑lived access keys, privilege escalation paths.
- Data protection
  - CMEK enforcement, KMS key policies, key rotation and usage anomalies.
- Network and perimeter
  - Internet‑reachable assets, unrestricted inbound/outbound rules, missing private endpoints.
- Workload and runtime (CNAPP)
  - Image vulnerabilities, signature verification at deploy, container/K8s policies (PSA, NetworkPolicy), exposed service accounts.
- Drift and governance
  - IaC vs runtime drift, policy exceptions with owners and expiry, framework mapping (CIS, NIST CSF, ISO).

---

## Operating Model

- Shift‑left integration
  - IaC scanning in PRs (Terraform, CloudFormation, ARM/Bicep, Helm/K8s manifests); fail high‑severity findings before apply.
- Continuous runtime scanning
  - Cloud APIs enumerated on schedule or event‑driven (Config, Cloud Asset Inventory, Activity Logs); findings de‑duplicated and triaged.
- Auto‑remediation for critical issues
  - Guardrail functions/policies that revert risky changes (e.g., block public storage, close open SG/NSG) with logging and approval workflow.
- Exception workflow
  - Time‑boxed exceptions with owner, justification, compensating controls; auto‑expire and re‑review.

---

## Baselines and Frameworks

- Standards
  - CIS Benchmarks (AWS/GCP/Azure), NIST CSF, ISO 27001 Annex A mappings, PCI/HIPAA domain‑specific controls.
- Environment overlays
  - Different severity and gating per environment (prod vs dev); stricter in prod with deploy blocks for critical issues.
- Custom policies
  - Organization‑specific rules (e.g., allowed regions, tagging requirements, approved KMS keys, private‑endpoint‑only).

Example policy ideas
- “No storage object/bucket/container public by ACL or policy.”
- “All disks and snapshots encrypted with CMEK; no default PMK for regulated data.”
- “Security groups/NSGs do not allow 0.0.0.0/0 except via approved edge.”
- “KMS keys not deletable without waiting period; rotation enabled; SoD enforced.”

---

## Workflow: From PR to Production

1) Authoring (PR)
   - IaC scanner checks Terraform/K8s manifests; blocks critical findings (public storage, open ingress, missing encryption).
2) Merge and plan
   - Policy as code evaluates planned changes; security gates prevent promotion if high severity persists.
3) Apply and verify
   - Post‑deploy CSPM validates runtime matches intended posture; drift alerts if console/manual changes differ.
4) Continuous monitoring
   - Scheduled scans, event‑driven checks, and SIEM alerts on posture changes (e.g., new public endpoint).
5) Evidence
   - Reports per framework and control; attach to compliance artifacts alongside exceptions and remediation logs.

---

## Prioritization and Risk

- Context‑aware ranking
  - Combine configuration severity with exploitability signals: internet exposure, identity reachability, data sensitivity tags, workload runtime risk.
- Ownership
  - Assign findings to the owning team (by tags/labels/repo); enforce SLAs by severity and environment.
- Suppression hygiene
  - Avoid blanket suppressions; time‑box and link to tracking tickets; require compensating controls for accepted risk.

---

## Common Findings and Fixes

- Public object storage
  - Fix: org‑level block public access; bucket policy deny; CSPM rule; lifecycle audit; exception process.
- Open security groups/NSGs
  - Fix: restrict to allowed CIDRs, use ALB/WAF at edge; private endpoints; default‑deny.
- Unencrypted resources
  - Fix: enable CMEK; enforce via policies/templates; rotate keys; evidence via KMS usage logs.
- Over‑permissive IAM
  - Fix: restrict actions/resources; use permission boundaries; remove unused roles/keys; implement access reviews.
- Missing logging
  - Fix: enable CloudTrail/Admin Activity/Activity Logs; store immutably (WORM); SIEM routing.

---

## Multi‑Cloud Notes

- Normalization
  - Abstract control families (encryption, logging, networking) across AWS/GCP/Azure; map provider APIs to common policy language.
- Data sources
  - AWS Config/CloudTrail, GCP Security Command Center/Asset Inventory, Azure Policy/Activity Logs/Defender for Cloud.
- Identity graph
  - Build a graph of who can access what via trust relationships and role assignments; detect escalation paths across accounts/projects/subscriptions.

---

## Automation and Tooling

- IaC scanners: tfsec, Checkov, kube‑score, kube‑linter, Polaris (examples)
- Policy engines: OPA/Rego, Sentinel, Conftest; admission controllers (Kyverno, Gatekeeper)
- CSPM/CNAPP platforms: native cloud tools + vendor platforms (conceptually)
- CI integration: GitHub Actions, GitLab CI, Jenkins; fail PRs on critical findings
- Notifications and tickets: integrate with chat and issue trackers; auto‑assign by tag/owner

Rego concept (deny public storage)
```rego
package org.cloud.storage

default deny = false

deny[msg] {
  input.kind == "bucket"
  input.policy.public == true
  msg := sprintf("public bucket: %v", [input.name])
}
```

---

## Evidence and Reporting

- Control posture dashboards by environment and system
- Framework reports (CIS, NIST CSF, ISO 27001) with pass/fail and exceptions
- Remediation timelines and SLAs; exception register with expiries
- Audit artifacts: policy definitions, scan results, remediation PRs, runtime validation logs

---

## Integration with Incident Response

- Real‑time alerts on high‑risk posture changes (e.g., bucket made public, KMS key disabled)
- Playbooks to auto‑revert and notify owners
- Forensics readiness: immutable logs for change attribution; tight identity auditing

Cross‑ref: [/docs/security/threat-intel-ir/](/docs/security/threat-intel-ir/)

---

## Checklist

- [ ] IaC scanning in PR with policy gates; block critical issues pre‑deploy.
- [ ] Org guardrails enforced via SCP/Org Policy/Azure Policy; exceptions time‑boxed.
- [ ] Continuous runtime CSPM scans and event‑driven checks; drift detection enabled.
- [ ] Auto‑remediation for public storage, open ingress, and missing encryption.
- [ ] IAM hygiene: no long‑lived keys; least privilege; permission boundaries; access reviews.
- [ ] Encryption posture: CMEK enforced; key rotation and SoD; KMS usage alerts.
- [ ] Centralized, immutable logs; SIEM detections for posture‑change anomalies.
- [ ] Evidence mapped to CIS/NIST/ISO; dashboards and reports per environment.

---

## Cross‑References

- Cloud Best Practices: [/docs/security/cloud-security/cloud-best-practices.md](/docs/security/cloud-security/cloud-best-practices.md)
- Cloud IAM: [/docs/security/cloud-security/cloud-iam.md](/docs/security/cloud-security/cloud-iam.md)
- Encryption & Storage: [/docs/security/cloud-security/encryption-storage.md](/docs/security/cloud-security/encryption-storage.md)
- SSDLC: [/docs/security/application-security/ssdlc.md](/docs/security/application-security/ssdlc.md)
