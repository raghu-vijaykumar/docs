---
title: "RBAC, ABAC, and ReBAC"
draft: false
---

# RBAC, ABAC, and ReBAC

Authorization determines what an authenticated principal can do. Picking and combining the right model is essential for least privilege, maintainability, and auditability. This guide compares RBAC, ABAC, and ReBAC, provides migration patterns, and offers concrete policy examples.

---

## The Models at a Glance

- RBAC (Role‑Based Access Control)
  - Grants permissions to roles; users are assigned roles.
  - Simple and familiar; good for coarse‑grained access (e.g., viewer, editor, admin).
  - Pitfalls: role explosion as combinations grow; difficult to encode context (tenant, geography, time).

- ABAC (Attribute‑Based Access Control)
  - Decisions based on attributes of user, resource, and environment (time, device posture, geo).
  - Flexible and expressive; good for multi‑tenant, data‑level scoping, and context‑aware policy.
  - Pitfalls: policy sprawl if not modular; attribute governance required.

- ReBAC (Relationship‑Based Access Control)
  - Authorizes via relationships (graphs) like “user is editor of group that owns document.”
  - Natural fit for collaborative apps and hierarchical sharing.
  - Pitfalls: needs a relationship store and consistency model; debugging graph policies can be non‑trivial.

Pragmatic approach
- Use RBAC for baseline coarse permissions.
- Use ABAC to scope those permissions by tenant/resource attributes and context (device posture, risk).
- Use ReBAC for resource sharing and delegated access patterns.

---

## Structuring Permissions

Principles
- Deny by default; allow by explicit rules.
- Model permissions as actions on resources (verbs & nouns).
- Separate authentication (identity) from authorization (capability).
- Keep roles minimal; prefer capability‑based grants over monolithic “admin” roles.

Example capability set
- orders:create, orders:view, orders:update, orders:refund
- inventory:adjust, inventory:view
- reports:export

---

## ABAC: Attributes and Context

Common attributes
- User: id, tenant_id, roles, department, risk_score, device_compliant
- Resource: tenant_id, owner_id, classification, region
- Environment: time_of_day, ip_range, network_zone

ABAC decision (conceptual)
- Allow if:
  - user.tenant_id == resource.tenant_id
  - AND action in user.capabilities
  - AND user.device_compliant == true
  - AND environment.time_of_day in business_hours

---

## Policy Examples

### OPA/Rego

```rego
package authz

default allow = false

# Input:
# {
#   "action": "orders:create",
#   "user": { "id": "u1", "tenant": "t1", "roles": ["support"], "scopes": ["orders:create"], "device_compliant": true },
#   "resource": { "type": "order", "tenant": "t1" },
#   "env": { "time": "2025-10-28T10:00:00Z", "ip": "203.0.113.10" }
# }

allow {
  input.user.device_compliant
  input.user.tenant == input.resource.tenant
  input.action == "orders:create"
  input.user.scopes[_] == "orders:create"
  business_hours(input.env.time)
}

business_hours(t) {
  # Simplified: Mon-Fri 08:00-18:00
  weekday := time.weekday(time.parse_rfc3339_ns(t))
  weekday >= 1
  weekday <= 5
  hour := time.hour(time.parse_rfc3339_ns(t))
  hour >= 8
  hour < 18
}
```

### Cedar (AWS Verified Permissions style)

```cedar
permit(
  principal in User,
  action in [Action::"orders.create", Action::"orders.view"],
  resource in Order
)
when {
  principal.tenant == resource.tenant
  && principal.device_compliant == true
};
```

### AWS IAM Condition‑Scoped

```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Sid": "TenantScopedS3Access",
    "Effect": "Allow",
    "Action": ["s3:GetObject", "s3:PutObject"],
    "Resource": "arn:aws:s3:::tenant-data/${aws:PrincipalTag/tenant}/*"
  }]
}
```

### Zanzibar‑style ReBAC (conceptual)

- Relationships:
  - document:123 owner user:alice
  - document:123 editor group:eng
  - group:eng member user:bob

- Check “user:bob can edit document:123”
  - user:bob is member of group:eng
  - group:eng has editor on document:123 → allowed

---

## Combining RBAC and ABAC

Pattern
- Assign coarse roles → map to capabilities.
- Enforce ABAC constraints at decision time.

Example
- Role “support_agent” → capabilities: orders:view, orders:create (refund disabled)
- ABAC constraints:
  - tenant match (user.tenant_id == resource.tenant_id)
  - device_compliant == true
  - no access outside support hours

This avoids proliferating roles like “support_agent_prod_device_ok_business_hours,” keeping policy expressive yet manageable.

---

## Multi‑Tenant and Data Partitioning

Rules of thumb
- Always include tenant_id in resource keys and access predicates.
- Enforce tenant isolation in every data access path (service, DB, cache, search).
- For shared infra (S3, Kafka), scope access via path/prefix/topic (and tags/conditions).

DB example (SQL)
```sql
SELECT * FROM orders
WHERE tenant_id = :user_tenant
AND id = :order_id;
```

Add an authorization layer so all queries are automatically decorated with tenant predicates (query rewriter or ORM hooks).

---

## Migration Strategies

From ad‑hoc checks → RBAC
- Inventory endpoints/actions; group into minimal roles; remove implicit “admin” assumptions.
From RBAC only → RBAC + ABAC
- Introduce tenant/resource scoping and context checks (device, time).
From RBAC/ABAC → add ReBAC
- Identify sharing/delegation needs (e.g., “share report with team”).
- Introduce a relationship store and a check API; keep it orthogonal to capability checks.

---

## Operational Practices

- Centralize decisions
  - Run a PDP (policy engine) per service or centrally; expose a check API; cache short‑lived decisions.
- Version and test policies
  - Treat policies as code; unit test and simulate changes; require review and CI.
- Log and explain
  - Record allow/deny with reasons; provide decision explainability for developers and auditors.
- Least‑privilege reviews
  - Periodic analysis of grants and usage; remove unused capabilities; detect privilege escalations.

---

## Common Pitfalls

- Role explosion caused by encoding context into roles.
- Skipping tenant checks in one code path, enabling cross‑tenant data leaks.
- Using only IP‑based rules for “internal” trust; skip identity/context checks.
- Unbounded ACLEs kept in the app DB with no central policy or audit.
- No dry‑run/testing causing outages on policy rollout.

---

## Checklist

- [ ] Minimal, capability‑based roles (RBAC) defined and documented.
- [ ] ABAC constraints (tenant, device, time, region) enforced for each action.
- [ ] Relationship model (ReBAC) used where sharing/delegation is required.
- [ ] Deny‑by‑default; explicit allow only.
- [ ] Policies versioned, tested, and code‑reviewed; PDP/PEP pattern in place.
- [ ] Authorization decisions logged with reasons and attributes (redacted).
- [ ] Tenant isolation enforced at all data layers (service, DB, cache, search).
- [ ] Regular access reviews remove unused roles and privileges.
