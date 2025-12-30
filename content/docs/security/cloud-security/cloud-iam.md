---
title: "Cloud IAM (AWS, GCP, Azure)"
draft: false
---

# Cloud IAM (AWS, GCP, Azure)

Cloud IAM controls who can do what, on which resources, under which conditions. Effective design applies least privilege, short‑lived credentials, and guardrails at the organization level—then composes finer policies at the project/account/subscription and resource layers.

This guide provides provider‑agnostic patterns and concrete examples for AWS IAM, GCP IAM, and Azure RBAC/Entra ID.

---

## Principles

- Least privilege by default
  - Start with deny‑all; grant minimal actions on minimal resource scopes; add conditions (time, IP, tags).
- Short‑lived credentials
  - Assume roles (AWS STS), Workload Identity Federation (GCP), Managed Identity (Azure); avoid static keys.
- Separation of duties
  - Distinct roles for build/deploy/operate/audit; dual control for sensitive grants; break‑glass accounts with hardware MFA.
- Organizational guardrails
  - Org/management‑group level deny/constraints (AWS SCPs, GCP Org Policies, Azure Policy) to enforce non‑negotiables.
- Attribute and tag‑based access
  - Prefer tags/labels/conditions to avoid role explosion, especially in multi‑tenant and environment‑scoped setups.
- Auditable and reviewable
  - Central logs, last‑used reports, access reviews, change approvals, and time‑boxed exceptions.

Cross‑refs
- Zero Trust: [/docs/security/iam/least-privilege-zero-trust.md](/docs/security/iam/least-privilege-zero-trust.md)
- Org guardrails: [/docs/security/cloud-security/shared-responsibility.md](/docs/security/cloud-security/shared-responsibility.md)

---

## Organization Guardrails

- AWS Service Control Policies (SCP)
  - Deny dangerous actions account‑wide (e.g., disable CloudTrail, make S3 public, stop KMS key rotation).
- GCP Organization Policy
  - Constraints like restrictVpcPeering, allowedPolicyMemberDomains, requireOsLogin, disableServiceAccountKeyCreation.
- Azure Policy/Blueprints
  - Enforce TLS, require tags, deny public IP on NICs, restrict locations, require customer‑managed keys.

Pattern
- Deny at the top for what must never happen; allow only specific clouds/regions/services; exceptions are time‑boxed and reviewed.

---

## AWS IAM

Hierarchy
- Organization → Accounts → IAM (roles, users, policies) → Resource policies (S3/KMS/etc).

AssumeRole (STS)
- Workflows mint short‑lived credentials; attach session tags to scope resources and audit context.

Least privilege policy (S3, tenant scoped)
```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Sid": "TenantScopedS3Access",
    "Effect": "Allow",
    "Action": ["s3:GetObject","s3:PutObject","s3:ListBucket"],
    "Resource": [
      "arn:aws:s3:::tenant-data",
      "arn:aws:s3:::tenant-data/${aws:PrincipalTag/tenant}/*"
    ],
    "Condition": {
      "StringEquals": { "aws:PrincipalTag/tenant": "${aws:PrincipalTag/tenant}" }
    }
  }]
}
```

Permission boundaries
- Cap what a role or user can ever be granted; use to prevent privilege escalation.

Resource policies
- S3 bucket policies, KMS key policies, SQS/SNS policies: restrict access by principal, VPC endpoint, and condition keys.

Best practices
- Disable long‑lived access keys; rotate if unavoidable; alert on unused >30 days.
- Use Access Analyzer and IAM Last‑Accessed data to prune permissions.
- Enforce MFA for console; hardware keys for break‑glass; session duration ≤ 1–8h.

---

## GCP IAM

Hierarchy
- Organization → Folders → Projects → Resources.
- Bindings of (member, role) optionally with Conditions (CEL).

Conditional bindings (tenant + time)
```yaml
bindings:
- role: roles/storage.objectAdmin
  members:
  - serviceAccount:svc-writer@project.iam.gserviceaccount.com
  condition:
    title: tenant-scope
    expression: resource.name.startsWith("projects/_/buckets/tenant-data/objects/acme/")
- role: roles/storage.objectViewer
  members:
  - group:prod-readers@example.com
  condition:
    title: business-hours
    expression: "request.time.getHours() >= 8 && request.time.getHours() < 18"
```

Workload Identity Federation
- Exchange external identities (GitHub OIDC, other clouds) for short‑lived service account credentials without static keys.

Org Policies
- Disable service account key creation; restrict external IPs; require CMEK on storage/database; domain allow‑lists.

Best practices
- Prefer groups over individual users; manage via Google Groups synced from IdP.
- Minimize primitive roles (owner/editor/viewer); use predefined or custom least‑privilege roles.
- Cloud Asset Inventory + Policy Analyzer for drift and excessive grants.

---

## Azure RBAC and Entra ID

Hierarchy
- Management groups → Subscriptions → Resource groups → Resources.

RBAC
- Role definitions (actions/notActions) bound via role assignments to principals at a scope (mgmt group/subscription/rg/resource).

Managed Identity
- System‑assigned or user‑assigned identities for workloads (VMs, Apps, AKS) to access Azure resources without secrets.

Sample role assignment (CLI concept)
```bash
# Assign Storage Blob Data Contributor to a managed identity on a specific container scope
az role assignment create \
  --assignee-object-id $IDENTITY_OBJECT_ID \
  --role "Storage Blob Data Contributor" \
  --scope "/subscriptions/$SUB/resourceGroups/rg-app/providers/Microsoft.Storage/storageAccounts/sa/BlobServices/default/containers/tenant-acme"
```

Conditional Access (Entra ID)
- Enforce MFA, device compliance, and risk‑based access for interactive users; pair with Privileged Identity Management (PIM) for JIT elevation.

Azure Policy
- Enforce customer‑managed keys, deny public IPs, restrict locations; deploy‑if‑not‑exists initiatives for guardrails.

Best practices
- Use PIM for time‑bound admin roles; approve+MFA gates.
- Prefer managed identities over app registrations with client secrets; if secrets used, rotate with Key Vault.
- Use Activity Logs + Defender for Cloud for detection; export to SIEM.

---

## Kubernetes and Cloud IAM

- Use cloud‑native identities for workloads (IRSA on EKS, Workload Identity on GKE, Azure AD Workload Identity for AKS).
- Map service accounts to cloud roles with least privilege; avoid node‑wide credentials.
- Admission controls to block pods without identity annotations when accessing cloud APIs.

---

## Multi‑Tenant Patterns

- Per‑tenant prefixes/namespaces and tag‑based policies to scope data access.
- Include tenant_id in claims or session tags; enforce in policy conditions and data access layers.
- Separate accounts/projects/subscriptions for high‑risk or regulated tenants to reduce blast radius.

---

## Detection, Review, and Hygiene

- Centralize audit logs (CloudTrail, Admin Activity logs, Azure Activity) and analyze for privilege escalation paths.
- Use access reviews periodically; remove unused roles; alert on wildcard actions or Owner‑level grants.
- Policy testing: simulate (AWS IAM Access Analyzer, GCP Policy Troubleshooter) before rollout.

---

## Common Pitfalls

- Static access keys embedded in CI or code; no rotation or last‑used checks.
- Granting broad admin roles at subscription/project scope to “unblock” teams.
- Missing org‑level guardrails allowing public storage, open networks, or key export.
- Long session durations without MFA; lack of JIT elevation for privileged tasks.
- Service accounts with excessive roles shared across apps.

---

## Checklist

- [ ] Org‑level guardrails: SCP (AWS), Org Policy (GCP), Azure Policy.
- [ ] No static keys; short‑lived creds via STS/Workload/Managed Identities.
- [ ] Roles scoped to minimal resources with conditions/tags/labels.
- [ ] Permission boundaries/constraints prevent escalation.
- [ ] Break‑glass with hardware MFA; PIM/JIT for admin elevation.
- [ ] Centralized audit logs; access reviews and last‑used pruning.
- [ ] Policy simulation/testing before rollout; CI checks for IaC IAM drift.

---

## Cross‑References

- Cloud overview: [/docs/security/cloud-security/_index.md](/docs/security/cloud-security/_index.md)
- Zero Trust & Least Privilege: [/docs/security/iam/least-privilege-zero-trust.md](/docs/security/iam/least-privilege-zero-trust.md)
- IAM models: [/docs/security/iam/rbac-abac.md](/docs/security/iam/rbac-abac.md)
- Shared responsibility: [/docs/security/cloud-security/shared-responsibility.md](/docs/security/cloud-security/shared-responsibility.md)
