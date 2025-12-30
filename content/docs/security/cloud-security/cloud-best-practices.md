---
title: "Cloud Security Best Practices"
draft: false
---

# Cloud Security Best Practices

Cloud security relies on clear ownership, automated guardrails, short‑lived identities, private‑by‑default networking, and continuous posture management. This guide consolidates provider‑agnostic best practices with actionable examples across AWS, GCP, and Azure.

Cross‑refs:
- Overview and model: [/docs/security/cloud-security/_index.md](/docs/security/cloud-security/_index.md)
- Shared responsibility: [/docs/security/cloud-security/shared-responsibility.md](/docs/security/cloud-security/shared-responsibility.md)
- Cloud IAM: [/docs/security/cloud-security/cloud-iam.md](/docs/security/cloud-security/cloud-iam.md)
- CSPM: [/docs/security/cloud-security/cspm.md](/docs/security/cloud-security/cspm.md)
- Encryption and storage: [/docs/security/cloud-security/encryption-storage.md](/docs/security/cloud-security/encryption-storage.md)
- Serverless security: [/docs/security/cloud-security/serverless-security.md](/docs/security/cloud-security/serverless-security.md)

---

## Objectives

- Default‑secure org and account structure with preventive guardrails.
- Identities are short‑lived and least‑privileged; no static keys.
- Networks are private‑first with controlled egress; services are segmented.
- Data is encrypted at rest/in transit with auditable key usage and SoD.
- Workloads are hardened and provenance‑verified; supply chain trusted.
- Posture monitored continuously with auto‑remediation for critical drift.
- Evidence is generated automatically to satisfy compliance.

---

## 1) Organization and Accounts/Projects

Structure
- Separate org units per environment (dev/stage/prod) and per tenant or business unit as applicable.
- One application = one (or few) accounts/projects/subscriptions; avoid “everything in prod.”

Preventive guardrails
- AWS Service Control Policies (SCP) to deny public storage, disable key export, block regions, and prevent CloudTrail/KMS tampering.
- GCP Organization Policies to disable service account key creation, restrict external IPs, enforce CMEK, restrict domains.
- Azure Policy/Initiatives at management group level to require tags, enforce CMK, deny public IPs, restrict locations.

Example (AWS SCP: deny public S3)
```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Sid": "DenyS3PublicAccess",
    "Effect": "Deny",
    "Action": "s3:PutBucketPolicy",
    "Resource": "arn:aws:s3:::*",
    "Condition": {
      "StringLike": {
        "s3:ExistingObjectTag/public": "true"
      }
    }
  }]
}
```

Example (GCP Org Policy: disable SA key creation)
```yaml
constraint: iam.disableServiceAccountKeyCreation
policy:
  rules:
    - enforce: true
```

Example (Azure Policy: deny public IP)
```json
{
  "properties": {
    "displayName": "Deny public IP on NIC",
    "policyRule": {
      "if": {
        "allOf": [
          { "field": "type", "equals": "Microsoft.Network/networkInterfaces" },
          { "exists": "true", "field": "Microsoft.Network/networkInterfaces/ipconfigurations[*].publicIpAddress.id" }
        ]
      },
      "then": { "effect": "Deny" }
    }
  }
}
```

---

## 2) Identity and Access

Principles
- No long‑lived access keys. Use STS/OIDC (AWS), Workload Identity Federation (GCP), Managed Identity (Azure).
- Enforce MFA (hardware keys for break‑glass), short sessions, and PIM/JIT elevation.
- Prefer ABAC/tag‑based policies to avoid role explosion, scope by environment/tenant.

Practices
- Session tags or claims (tenant, environment) used in resource policies and logs.
- Permission boundaries/constraints to prevent escalation.
- Periodic access reviews and “last‑used” pruning; alerts on Owner/Administrator grants.

Cross‑ref: [/docs/security/cloud-security/cloud-iam.md](/docs/security/cloud-security/cloud-iam.md)

---

## 3) Network and Perimeter

Design
- Separate VPCs/VNets per env/tenant; default‑deny security groups/NSGs.
- Use private endpoints/Private Service Connect/Private Link to managed services.
- Central egress via NAT or proxy; domain/DNS allow‑lists for sensitive services.
- Use WAF/CDN and provider DDoS protection at the edge.

Kubernetes/service mesh
- NetworkPolicies (deny‑all + allow‑lists); service mesh for mTLS and identity‑aware authorization.
- Ingress controllers with WAF, rate limiting, and TLS enforcement.

Cross‑refs:
- Network Security: [/docs/security/network-security/_index.md](/docs/security/network-security/_index.md)
- Segmentation: [/docs/security/network-security/segmentation-zero-trust.md](/docs/security/network-security/segmentation-zero-trust.md)

---

## 4) Data Protection and Encryption

- Encrypt at rest via CMEK/KMS; envelope encryption with DEK/KEK separation.
- Encrypt in transit (TLS 1.2/1.3); use mTLS for east‑west where appropriate.
- Separate KMS admin from data admin (SoD); log key usage; rotate keys.
- Uniform bucket/object ACLs; block public access at org level; versioning + retention for recovery.

Cross‑ref: [/docs/security/cloud-security/encryption-storage.md](/docs/security/cloud-security/encryption-storage.md)

---

## 5) Workload Hardening (VMs, Containers, Functions)

- Golden images/base images with CIS hardening; minimal packages.
- Patch pipelines for OS and runtimes; immutable infra where feasible.
- Containers: drop capabilities, run as non‑root, read‑only filesystems, seccomp/AppArmor; image scanning in CI and admission.
- K8s: Pod Security Admission (restricted), NetworkPolicies, resource limits, admission policies for signature verification.
- Serverless: least‑privilege execution roles, validate event sources, control egress, short‑lived secrets via stores.

Cross‑refs:
- OS/Container: [/docs/security/os-security/_index.md](/docs/security/os-security/_index.md)
- Serverless: [/docs/security/cloud-security/serverless-security.md](/docs/security/cloud-security/serverless-security.md)

---

## 6) Supply Chain and Deploy Trust

- SBOM per build; artifact signing (cosign) and SLSA‑style provenance.
- Admission control to verify signatures/attestations at deploy; block unsigned images.
- Registry policies: disallow mutable tags in prod; enforce vulnerability thresholds.

Kubernetes admission concept (verify signatures)
```yaml
apiVersion: kyverno.io/v1
kind: ClusterPolicy
metadata: { name: verify-images }
spec:
  rules:
    - name: verify-cosign
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

Cross‑ref: [/docs/security/application-security/ssdlc.md](/docs/security/application-security/ssdlc.md)

---

## 7) Posture Management (CSPM/CNAPP)

- Continuously scan for misconfigurations (public storage, open security groups, unencrypted disks, over‑permissive IAM).
- Align with CIS Benchmarks and internal baselines; track exceptions with owner and expiry.
- Integrate with CI to catch drift pre‑deploy; auto‑remediate critical findings.

Cross‑ref: [/docs/security/cloud-security/cspm.md](/docs/security/cloud-security/cspm.md)

---

## 8) Logging, Detection, and IR

- Centralize control plane audit logs and workload logs; protect with WORM/immutability.
- SIEM detections for privilege escalation, mass deletions, cross‑region anomalies, anomalous egress.
- Runbooks for key compromise, public exposure, and rollback; periodic tabletop exercises.

Cross‑refs:
- Threat Intel & IR: [/docs/security/threat-intel-ir/](/docs/security/threat-intel-ir/)
- SSDLC evidence: [/docs/security/application-security/ssdlc.md](/docs/security/application-security/ssdlc.md)

---

## 9) Backup, DR, and Resilience

- Define RTO/RPO by system criticality; cross‑region backups and tested restores.
- Immutable backups for critical data; access controls separate from prod operators.
- Chaos testing and game days; synthetic probes and canaries for critical paths.

---

## 10) Governance, Evidence, and Compliance

- Map controls to ISO 27001/SOC 2/PCI/NIST CSF; automate evidence collection (cloud APIs, CI reports).
- Track policy exceptions with compensating controls; time‑box and review.
- Maintain change approvals for infra via IaC; ensure drift detection.

Cross‑ref: Compliance (planned): /docs/security/compliance/

---

## Common Pitfalls

- Static access keys in CI/code; lack of last‑used monitoring and rotation.
- Public buckets/open DB listeners due to missing org‑level blocks or drift.
- Over‑permissive IAM (wildcards, admin at root scopes); no permission boundaries.
- No centralized logs or immutability; inability to investigate incidents.
- Admission not verifying signatures; supply chain controls missing.
- CSPM findings ignored without owners/SLA; exceptions never expire.

---

## Checklist

Organization
- [ ] Separate accounts/projects/subscriptions per env/tenant; org guardrails in place.
- [ ] Region restrictions, public storage block, and key export denial enforced.

Identity
- [ ] Short‑lived credentials (STS/WIF/Managed Identity); MFA/PIM/JIT for admins.
- [ ] ABAC/tag‑based policies; permission boundaries; periodic access reviews.

Network
- [ ] Private endpoints; default‑deny SG/NSG; centralized egress via NAT/proxy.
- [ ] WAF/CDN and DDoS protection at edge; service mesh mTLS where appropriate.

Data
- [ ] CMEK/KMS for all at‑rest encryption; SoD for key management; key usage audited.
- [ ] Versioning/retention for storage; object ACLs uniform and private by default.

Workloads and Supply Chain
- [ ] Hardened images; containers non‑root/read‑only/caps‑dropped; admission verifies signatures.
- [ ] SBOM + provenance for builds; registry policies enforced.

Posture and Observability
- [ ] CSPM integrated with CI; auto‑remediation for critical drift.
- [ ] Centralized audit/workload logs with immutability; tuned detections and IR playbooks.

Resilience
- [ ] Backups with periodic restore tests; defined RTO/RPO; chaos drills.

---

## Cross‑References

- Shared Responsibility: [/docs/security/cloud-security/shared-responsibility.md](/docs/security/cloud-security/shared-responsibility.md)
- Cloud IAM: [/docs/security/cloud-security/cloud-iam.md](/docs/security/cloud-security/cloud-iam.md)
- Encryption & Storage: [/docs/security/cloud-security/encryption-storage.md](/docs/security/cloud-security/encryption-storage.md)
- CSPM: [/docs/security/cloud-security/cspm.md](/docs/security/cloud-security/cspm.md)
- Serverless Security: [/docs/security/cloud-security/serverless-security.md](/docs/security/cloud-security/serverless-security.md)
