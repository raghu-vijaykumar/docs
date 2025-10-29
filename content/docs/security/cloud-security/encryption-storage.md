---
title: "Cloud Encryption & Storage Security"
draft: false
---

# Cloud Encryption & Storage Security

Data protection in cloud hinges on strong key management, correct encryption modes, controlled network/data paths, and operational evidence. This guide covers envelope encryption, KMS usage, storage hardening for object/block/file/DB services, client‑side encryption, and recovery posture.

Cross‑refs:
- Cloud overview: [/docs/security/cloud-security/_index.md](/docs/security/cloud-security/_index.md)
- Cloud IAM: [/docs/security/cloud-security/cloud-iam.md](/docs/security/cloud-security/cloud-iam.md)
- TLS/Transport: [/docs/security/cryptography/tls-ssl/](/docs/security/cryptography/tls-ssl/)
- PKI & Certificates: [/docs/security/cryptography/pki/](/docs/security/cryptography/pki/)
- Posture Mgmt: [/docs/security/cloud-security/cspm.md](/docs/security/cloud-security/cspm.md)

---

## Principles

- Encrypt everywhere
  - At rest with KMS‑managed customer keys (CMEK); in transit with TLS 1.2/1.3; mTLS for east‑west where needed.
- Separation of duties (SoD)
  - KMS admins separate from data/application admins; data access never implies key admin.
- Least privilege and auditable usage
  - Tight KMS key policies; log key usage and failures; alert on anomalous decrypt/encrypt patterns.
- Envelope encryption by default
  - Data Encryption Key (DEK) wraps payload; Key Encryption Key (KEK) in KMS wraps DEK; rotate KEK without re‑encrypting all data.

---

## Key Management (KMS/HSM)

Key types
- KEK (CMEK): long‑lived in KMS/HSM, used only to wrap/unwrap DEKs.
- DEK: short‑lived, generated per object/file/record or batch; managed by services/apps.

Policies
- Deny key admin to application roles; allow only encrypt/decrypt or GenerateDataKey where appropriate.
- Prevent key deletion without multi‑party approval and waiting period.
- Enforce key rotation schedules; document cryptoperiods.

Auditing
- Enable KMS audit logs; monitor for:
  - Decrypt spikes, unusual regions, first‑time principals.
  - Key policy changes, disable/enable events, scheduled deletions.

Multi‑region
- Prefer region‑scoped keys; replicate only if required, with explicit governance.
- For DR, use distinct keys per region and documented restore paths.

---

## At‑Rest Encryption Patterns

Provider‑managed encryption (default)
- Many services encrypt by default with provider‑managed keys (PMK). Suitable for low‑risk data; minimal SoD.

Customer‑managed keys (CMEK)
- Use CMEK for regulated/sensitive data. Ensures SoD, explicit control, and auditable usage.

Client‑side encryption (CSE)
- Encrypt before upload; keys never leave client/service boundary.
- Use only when you must keep cloud provider blind (e.g., sensitive PII in shared environments) and you accept loss of server‑side features (search, transforms).
- Manage envelope and key distribution carefully; consider Tink/Vault Transit or cloud KMS + application envelope.

Algorithm selection
- AEAD (AES‑GCM or ChaCha20‑Poly1305) for payloads.
- For DB fields, consider deterministic or order‑preserving schemes only with strict constraints; prefer tokenization if possible.

---

## Object Storage (S3/GCS/Azure Blob)

Defaults and blocking public access
- Enforce org‑level block‑public‑access (deny public ACLs/bucket policies).
- Bucket‑level: uniform ACLs; default private; versioning and object lock (WORM) for critical data.

Encryption
- SSE‑C (client‑provided keys), SSE‑KMS (CMEK), or service defaults.
- Prefer SSE‑KMS with bucket/prefix‑scoped KMS keys; separate keys by data domain/tenant.

Access control
- Resource policies restrict principals by attributes (tenant, environment) and network (VPC endpoints/PrivateLink).
- Use conditional keys (aws:PrincipalTag, storage.googleapis.com/object resource name prefixes, Azure SAS constraints).

Lifecycle and retention
- Lifecycle rules for retention and deletion; retention/legal hold for compliance.
- Cross‑region replication with distinct keys and access boundaries.

Monitoring
- Continuously scan for public exposure and policy drift; alert on PUTs that modify policies or ACLs.

---

## Block/File Storage (EBS/PD/Azure Disk, EFS/Filestore/Azure Files)

Block volumes
- Default encrypt all volumes with CMEK where supported; attach policies to prevent unencrypted volumes.
- Snapshot encryption and access control; scrub volumes on deletion; control snapshot sharing.

File shares
- Enforce TLS in transit for SMB/NFS; identity‑bound access (AD/LDAP/managed identities).
- Restrict network paths (private endpoints); export controls per VPC/subnet.

---

## Databases and Data Stores (RDS/Cloud SQL/Azure SQL, NoSQL, Caches)

At rest
- Enable CMEK for DB storage (data files, redo/transaction logs, backups).
- Separate keys for prod vs non‑prod; per‑system keys for blast radius control.

In transit
- Require TLS for client connections; pin minimum versions; enforce certificate validation.

Row/field‑level protection
- Row‑level security predicates for multi‑tenant.
- Field‑level encryption or tokenization for high‑sensitivity columns (PII/PHI/PCI).
- Application‑level envelope if DB‑native features are insufficient.

Backups
- Encrypt backups and snapshots with CMEK; store in restricted backup accounts/projects/subscriptions.
- Test restore paths; document RTO/RPO; verify decryption works with rotated keys.

---

## Secrets and Key Material

Secrets management
- Use cloud secret stores or Vault; disallow secrets in code/CI variables.
- Short TTLs; dynamic credentials for DBs and cloud APIs; rotate automatically.

Transit encryption for secrets
- Always TLS; prefer mTLS for sensitive internal paths; bind access by identity and policy.

Derivation and storage
- Derive keys via KDFs (HKDF); never manual derivation.
- Store only KEK handles and wrapped DEKs; never raw KEKs outside KMS/HSM.

---

## Egress and Data Paths

- Private endpoints to storage/DB; avoid Internet traversal.
- Central egress with domain allow‑lists; DLP where applicable.
- Validate and sign data movement (manifests with checksums/signatures).

---

## Evidence and Compliance

Artifacts
- KMS key policies, rotation logs, key usage metrics.
- Storage encryption posture reports; public access block settings.
- Backup/restore test logs; retention and legal hold configurations.
- DB encryption settings, TLS enforcement evidence, client parameter groups.

Mapping
- ISO 27001 A.8/A.10, SOC 2 CC6/CC7/CC8, PCI DSS 3/10/12, HIPAA 164.312(a)(2)(iv), NIST CSF PR.DS.

---

## Common Pitfalls

- Relying on provider‑managed keys where SoD is required.
- Allowing application admins to modify KMS policies or disable keys.
- Unencrypted snapshots or cross‑account snapshot sharing without guardrails.
- Public object storage via ACL drift; lack of versioning/retention.
- Backups not encrypted with CMEK or restore tests never executed.
- Client‑side encryption without robust key lifecycle → data loss.

---

## Checklist

Keys and Policies
- [ ] CMEK used for sensitive data; distinct keys per system/environment/tenant where needed.
- [ ] KMS policies enforce SoD; deny admin to app roles; deletion requires delay + approvals.
- [ ] Rotation schedules defined; key usage logging and alerts enabled.

Storage
- [ ] Object storage blocks public access org‑wide; uniform ACLs; versioning and object lock where needed.
- [ ] SSE‑KMS default on buckets and data stores; lifecycle policies defined; CRR guarded.

Databases
- [ ] DB at‑rest CMEK, TLS enforced; RLS/field‑level protection for multi‑tenant/PII.
- [ ] Backups encrypted, isolated, and periodically restored in drills.

Secrets
- [ ] Secrets in a managed store; short TTL; dynamic creds where possible.
- [ ] No secrets in code/CI; audits for repository leakage; rotation automations.

Egress
- [ ] Private endpoints to storage/DB; centralized egress with allow‑lists; data movement verified with checksums/signatures.

---

## Cross‑References

- TLS/SSL: [/docs/security/cryptography/tls-ssl/](/docs/security/cryptography/tls-ssl/)
- PKI: [/docs/security/cryptography/pki/](/docs/security/cryptography/pki/)
- CSPM: [/docs/security/cloud-security/cspm.md](/docs/security/cloud-security/cspm.md)
- Cloud IAM: [/docs/security/cloud-security/cloud-iam.md](/docs/security/cloud-security/cloud-iam.md)
