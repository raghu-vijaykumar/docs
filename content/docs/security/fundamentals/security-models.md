---
title: "Security Models: Bell–LaPadula, Biba, Clark–Wilson"
draft: false
---

# Security Models: Bell–LaPadula, Biba, Clark–Wilson

Formal security models provide precise rules for how subjects (users, processes) can interact with objects (files, records) to enforce goals like confidentiality or integrity. They’re useful to reason about designs, select controls, and avoid classically known failure modes.

This guide explains three foundational models and how to map them to modern systems (cloud, Kubernetes, service meshes, data platforms).

---

## Concepts and Motivation

- Mandatory vs Discretionary Access Control
  - DAC: access is at the discretion of object owners (e.g., UNIX file perms). Flexible but error-prone.
  - MAC: policy is centrally defined and mandatory (e.g., SELinux, High/Low data labels). Safer for high-assurance.
- Lattices and Levels
  - Many models organize security labels or integrity levels into a lattice (partial order). Rules restrict information flow across levels.

Trade-offs
- Models simplify reality: they don’t capture all threats (e.g., availability, covert channels).
- Use them to shape policy and architecture, then complement with monitoring, zero trust identity, and operational controls.

Cross-refs: [Authentication vs Authorization](/docs/security/fundamentals/authn-vs-authz.md), [RBAC/ABAC](/docs/security/iam/rbac-abac.md), [Least Privilege & Zero Trust](/docs/security/iam/least-privilege-zero-trust.md)

---

## Bell–LaPadula (Confidentiality)

Goal
- Prevent unauthorized disclosure of information. Classic “military” confidentiality model.

Core Rules
- Simple Security Property (No Read Up, NRU)
  - A subject at level L can only read objects at level ≤ L.
- Star Property (No Write Down, NWD)
  - A subject at level L can only write to objects at level ≥ L (to prevent leaking high data to low).
- (Optional) Strong Star
  - Read and write only at the same level.

Implications
- Information never flows from high to low confidentiality. Helps avoid data leaks.
- Availability and integrity are not modeled; covert channels are outside scope.

Modern Mappings
- Data classification and labeling (e.g., Public, Internal, Confidential, Restricted).
- MAC systems: SELinux categories, AppArmor profiles, Windows Integrity Control.
- Multi-level databases / row-level security where labels restrict read/write.
- Service meshes with identity-based policy and data-tier isolation (deny write-down to lower tiers).

Example (conceptual lattice)
```
Top Secret > Secret > Confidential > Unclassified
```

OPA/Rego concept (prevent write-down)
```rego
package policy.confidentiality
# deny write if subject_level > object_level
deny[msg] {
  input.action == "write"
  input.subject.level > input.object.level
  msg := sprintf("no write-down: %v->%v", [input.subject.level, input.object.level])
}
```

---

## Biba (Integrity)

Goal
- Prevent unauthorized or improper modification of information (maintain correctness).

Core Rules
- Simple Integrity Property (No Read Down, NRD)
  - A subject at integrity level L cannot read objects at lower integrity (avoid contamination).
- Star Integrity Property (No Write Up, NWU)
  - A subject at integrity level L cannot write to higher integrity objects (avoid corrupting high-integrity data).
- Invocation Property
  - A subject cannot invoke (call) higher integrity subjects.

Implications
- Trust flows upward: low-trust inputs should not affect high-trust outputs.
- Complements Bell–LaPadula; focuses on integrity rather than confidentiality.

Modern Mappings
- Data pipelines: separate “raw/bronze” (low integrity) from “gold” (high integrity) layers; enforce transformations via vetted jobs.
- Production databases only writable by controlled services; reads from untrusted sources do not directly modify high-integrity state.
- Package/build systems: only trusted builders can produce artifacts; untrusted inputs are sanitized and validated.

Example (DB guard)
```sql
-- High-integrity table: only service role may write
CREATE POLICY only_service_write ON prod.high_integrity_table
  FOR INSERT TO role_service
  USING (true) WITH CHECK (current_user = 'role_service');
```

---

## Clark–Wilson (Commercial Integrity Model)

Goal
- Ensure data integrity in commercial systems via well-formed transactions, separation of duties, and auditing.

Key Concepts
- Constrained Data Items (CDI)
  - Data requiring integrity (e.g., account balances).
- Unconstrained Data Items (UDI)
  - Untrusted input that must be validated before becoming CDI.
- Transformation Procedures (TP)
  - Well-formed transactions that move state between valid states (e.g., “transfer funds”).
- Integrity Verification Procedures (IVP)
  - Checks to ensure CDI remains in a valid state.
- Separation of Duty (SoD)
  - No single subject performs conflicting roles (e.g., maker/checker).

Modern Mappings
- Application invariants + database constraints (FKs, CHECKs), stored procedures or service endpoints acting as TPs.
- Input validation pipelines converting UDI → CDI with audit trails.
- RBAC with SoD: different roles for request vs approve; workflow engines for approvals.
- Append-only audit logs; tamper-evident storage.

Example (approvals)
- TP1: create_payment (maker)
- TP2: approve_payment (checker)
- Policy: same user cannot execute both TP1 and TP2 for the same record.

---

## How They Relate and When to Use

- Bell–LaPadula: prioritize when confidentiality leakage is the primary risk (healthcare, defense, PII-heavy analytics).
- Biba: prioritize when correctness is paramount (financial ledgers, safety systems, pipelines).
- Clark–Wilson: transactional business systems needing auditability and SoD (payments, procurement).

Combinations
- Real systems blend them:
  - Apply Bell–LaPadula-style labeling to prevent data exfil.
  - Apply Biba-style integrity levels to keep low-trust inputs from corrupting core state.
  - Enforce Clark–Wilson’s TPs and SoD at the app and workflow levels.

---

## Implementing in Modern Stacks

Identity and Policy
- Use ABAC with labels/claims (classification, tenant, environment) to encode levels.
- Centralize decisions (OPA/Cedar) and push PEPs to gateways, services, and DB layers.

Operating Systems
- SELinux/AppArmor to encode allowed transitions and access based on labels.
- Windows Integrity Levels to prevent write-up to system areas.

Kubernetes
- Namespaces as coarse levels; NetworkPolicies and Admission Controllers to restrict flows.
- Pod Security Admission + seccomp/apparmor to enforce execution constraints.
- Service mesh AuthZ to prevent cross-level read/write (deny write-down; deny read-down).

Data Platforms
- Zone your lakehouse/warehouse (raw → silver → gold) and gate promotions with IVPs (data quality tests).
- Row/column-level security with labels (tenant, classification) and deny policies.
- Sign data manifests; use lineage and checksums for integrity proofs.

Supply Chain
- Only signed, verified artifacts can be promoted (No Write Up to protected repos).
- SBOM + attestation verification as IVPs at deploy time.

---

## Common Pitfalls

- Enforcing at UI but not at API/DB → bypass possible.
- Ignoring indirect flows (export/import paths, logs, caches) → write-down or contamination through side channels.
- Role explosion instead of using attributes/labels → drift and misconfiguration.
- No audit trails or SoD → integrity violations undetectable.

---

## Checklist

- [ ] Define confidentiality and integrity levels (labels) with a clear lattice or ordering.
- [ ] Map resources and subjects to levels via tags/attributes; automate classification.
- [ ] Enforce “no write-down” (Bell–LaPadula) where confidentiality is critical.
- [ ] Enforce “no read-down/no write-up” (Biba) where integrity is critical.
- [ ] Implement Clark–Wilson constructs: UDI→CDI validation, well-formed TPs, SoD, IVPs, and audit.
- [ ] Apply controls at edge, service, and data layers; not just UI.
- [ ] Test with policy simulation (unit/integration) and negative cases (IDOR, cross-level attempts).
- [ ] Monitor and log decisions; immutable, tamper-evident storage for audits.

---

## Cross-References

- IAM Models: [/docs/security/iam/rbac-abac.md](/docs/security/iam/rbac-abac.md)
- Zero Trust: [/docs/security/iam/least-privilege-zero-trust.md](/docs/security/iam/least-privilege-zero-trust.md)
- OS Security (SELinux/AppArmor): [/docs/security/os-security/_index.md](/docs/security/os-security/_index.md)
- Data protection: [/docs/security/cloud-security/encryption-storage.md](/docs/security/cloud-security/encryption-storage.md)
- SSDLC: [/docs/security/application-security/ssdlc.md](/docs/security/application-security/ssdlc.md)
