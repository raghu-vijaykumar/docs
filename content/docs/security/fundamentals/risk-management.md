---
title: "Risk Management: Threats, Vulnerabilities, Assessment, and Mitigation"
draft: false
---

# Risk Management: Threats, Vulnerabilities, Assessment, and Mitigation

Risk management aligns security work with business impact. It identifies what can go wrong, quantifies or qualifies the likelihood and impact, selects controls to reduce risk within appetite/tolerance, and continuously monitors outcomes with evidence.

This guide provides a practical approach for modern software systems: terminology, assessment methods (qualitative and quantitative), threat modeling, mitigation strategies, governance, and artifacts that auditors expect.

---

## Core Terms

- Asset
  - Anything of value (systems, data, identities, reputation, availability).
- Threat
  - Potential cause of an unwanted incident (e.g., credential stuffing, insider misuse, DDoS).
- Vulnerability
  - Weakness that can be exploited (e.g., missing input validation, public S3 bucket).
- Exploit/Attack
  - Realization of a threat against a vulnerability.
- Likelihood
  - Probability that a scenario will occur (often ordinal in qualitative methods).
- Impact
  - Consequence if the scenario occurs (confidentiality/integrity/availability/reputation/regulatory).
- Risk
  - Function of likelihood and impact (Risk = f(Likelihood, Impact)); in FAIR: probable loss over time.

---

## Approaches to Risk Assessment

### Qualitative (fast, broad alignment)
- Ordinal scales: Likelihood {Low/Med/High}, Impact {Low/Med/High/Critical}.
- Risk matrix: combine likelihood and impact to determine priority (e.g., Heatmap).
- Pros: quick, easy to communicate; Cons: coarse, subjective.

Example matrix (conceptual)
```
             Impact
Likelihood   Low   Med   High   Crit
High         M     H     H      C
Medium       L     M     H      H
Low          L     L     M      H
```

### Quantitative (FAIR-style; data-informed)
- Model loss events with distributions (e.g., frequency and magnitude).
- Estimate in monetary terms (Loss Exposure); run Monte Carlo simulations for ranges.
- Pros: decision-grade comparisons, ROI on controls; Cons: needs data and modeling discipline.

Key FAIR concepts
- Loss Event Frequency (LEF) = Threat Event Frequency × Vulnerability (probability of action success).
- Loss Magnitude (LM): Primary (response costs) + Secondary (regulatory, reputation).
- Risk = LEF × LM.

---

## Threat Modeling

Purpose
- Systematically discover and reason about threats before they become incidents.

Methods
- STRIDE (Spoofing, Tampering, Repudiation, Information disclosure, Denial of service, Elevation of privilege) — broad coverage for app/infra.
- LINDDUN (Privacy threats) — privacy risk.
- PASTA (risk-centric, multi-stage).
- Attack trees / misuse cases — scenario decomposition.

Process (lightweight, per feature/service)
1) Decompose system: assets, trust boundaries, data flows (DFDs).
2) Identify threats using mnemonics (STRIDE) per boundary and asset.
3) Rate risk (qualitative or quantitative).
4) Select mitigations; record assumptions and residual risk.
5) Derive security requirements and tests; feed SSDLC.

Artifacts
- Diagram (DFD), risk notes, assumptions, mitigations, test cases, residual risk and owner.

Cross-refs: [SSDLC](/docs/security/application-security/ssdlc.md), [Authentication vs Authorization](/docs/security/fundamentals/authn-vs-authz.md), [Security Models](/docs/security/fundamentals/security-models.md)

---

## Mitigation Strategies

Options
- Avoid
  - Remove the risky feature/process or change design (e.g., eliminate public ingress).
- Reduce
  - Add controls to lower likelihood/impact (e.g., MFA, WAF, rate limits, encryption, NetworkPolicy).
- Transfer
  - Insurance, contractual shift, or managed service with SLAs.
- Accept
  - Explicitly accept residual risk with documented rationale and review cadence.

Control categories (map to CIA and frameworks)
- Preventive: MFA, least privilege, segmentation, input validation, CSP.
- Detective: SIEM detections, anomaly alerts, integrity checks, canaries.
- Corrective: backup/restore, rollbacks, revocation flows, incident response.
- Deterrent/Compensating: legal banners, dual control, break-glass with monitoring.

Cross-refs: [Least Privilege & Zero Trust](/docs/security/iam/least-privilege-zero-trust.md), [Network Security](/docs/security/network-security/_index.md), [Cloud Shared Responsibility](/docs/security/cloud-security/shared-responsibility.md)

---

## Risk Register and Governance

Risk register fields (typical)
- ID, Title, Description, Assets, Threat, Vulnerability, Scenario.
- Likelihood, Impact, Risk rating (pre-mitigation).
- Mitigations/controls and owners; due dates; status.
- Residual risk after controls; acceptance (who/when), review date.
- Evidence links (tests, policies, logs, CSPM/IaC reports).

Workflow
- Intake from threat modeling, vulnerability scans (SAST/SCA/DAST), CSPM, pen tests, audits.
- Triage and assign owners; set SLAs by severity.
- Track through remediation; document exceptions and compensating controls.
- Report to stakeholders: trends, top risks, time-to-remediate, control effectiveness.

Evidence examples
- CI reports (SAST/SCA/IaC), SBOM, signature verification logs, CloudTrail/Admin Activity logs, WAF/CDN metrics, incident postmortems.

---

## Policies and Compliance

Security policies (living documents)
- Access control, encryption, key management, vulnerability management, logging/monitoring, incident response, change management, data retention.
- Policies should state objectives, scope, responsibilities, and references to standards.

Compliance frameworks (mapping)
- ISO 27001 Annex A, SOC 2 Trust Services Criteria, PCI DSS, HIPAA, NIST CSF.
- Use control mappings to avoid duplicate work; generate evidence automatically where possible (CSPM, CI pipelines, SIEM).

Cross-refs: Compliance (planned): /docs/security/compliance/; Cloud/SSDLC sections show evidence generation and storage.

---

## Continuous Monitoring

- Posture: CSPM/CNAPP for cloud misconfigurations; IaC drift detection; registry/image scan coverage.
- Signals: auth failures, policy denies, permission changes, mass deletions, exfil indicators.
- Metrics: MTTD/MTTR for incidents; time to patch; % resources encrypted; % policies enforced.
- Reviews: risk register review cadence (monthly/quarterly); exception renewals are time-boxed.

Cross-refs: [Threat Intel & IR](/docs/security/threat-intel-ir/), [Cloud Posture](/docs/security/cloud-security/_index.md)

---

## Practical Examples

Scenario 1: Credential stuffing on login
- Likelihood: Medium–High (commodity attack); Impact: High (account takeover).
- Controls: MFA, rate limiting/IP throttling, breached password checks, bot defense, anomaly detection.
- Evidence: rate-limit logs, MFA adoption metrics, alert history, incident drills.

Scenario 2: Public storage misconfiguration
- Likelihood: Medium (drift); Impact: Critical (PII exposure).
- Controls: org-level deny public access, CSPM rule, pre-deploy IaC checks, S3 block public access, per-bucket policy tests.
- Evidence: CSPM reports, SCP/policy definitions, audit log of changes, exception records.

Scenario 3: Supply chain tampering
- Likelihood: Low–Medium; Impact: Critical (wide compromise).
- Controls: SBOM, signatures/attestations, verified deployments, provenance (SLSA), dependency pinning/updates.
- Evidence: cosign verification logs, SBOM artifacts, admission controller logs, CI policy gates.

---

## Measurement and Decisioning

- Risk appetite/tolerance: define what is acceptable per domain (e.g., public website vs. payments).
- Cost-benefit: compare annualized loss expectancy vs. annual cost of control (quantitative) or use priority tiers (qualitative).
- Sunset controls that don’t reduce risk measurably; invest in those with highest ROI.

---

## Checklist

- [ ] Assets and trust boundaries identified; threat model maintained per system/feature.
- [ ] Risk method chosen (qualitative or FAIR-like) with consistent scales and documentation.
- [ ] Central risk register with owners, SLAs, residual risk, and review cadence.
- [ ] Controls selected across preventive/detective/corrective; mapped to CIA and frameworks.
- [ ] Evidence produced automatically where feasible; stored tamper-evidently.
- [ ] Exceptions are time-boxed with compensating controls and approvals.
- [ ] Continuous monitoring in place; metrics reported to leadership.
- [ ] Periodic tabletop and postmortems feed updates back into risk models.

---

## Cross-References

- SSDLC: [/docs/security/application-security/ssdlc.md](/docs/security/application-security/ssdlc.md)
- Cloud Security: [/docs/security/cloud-security/_index.md](/docs/security/cloud-security/_index.md)
- IAM & Zero Trust: [/docs/security/iam/_index.md](/docs/security/iam/_index.md)
- Network Security: [/docs/security/network-security/_index.md](/docs/security/network-security/_index.md)
- Threat Intel & IR: [/docs/security/threat-intel-ir/](/docs/security/threat-intel-ir/)
