---
title: "Serverless Security (Lambda, Cloud Functions, Azure Functions)"
draft: false
---

# Serverless Security (Lambda, Cloud Functions, Azure Functions)

Serverless functions reduce operational surface but concentrate risk in identity, event inputs, and data handling. Secure design focuses on least‑privilege execution roles, validated event sources, controlled egress, secrets hygiene, and supply‑chain integrity. This guide provides provider‑agnostic patterns with AWS Lambda, Google Cloud Functions/Cloud Run, and Azure Functions.

Cross‑refs:
- Cloud best practices: [/docs/security/cloud-security/cloud-best-practices.md](/docs/security/cloud-security/cloud-best-practices.md)
- Cloud IAM: [/docs/security/cloud-security/cloud-iam.md](/docs/security/cloud-security/cloud-iam.md)
- Encryption & storage: [/docs/security/cloud-security/encryption-storage.md](/docs/security/cloud-security/encryption-storage.md)
- API Security: [/docs/security/application-security/api-security.md](/docs/security/application-security/api-security.md)
- CSPM/CNAPP: [/docs/security/cloud-security/cspm.md](/docs/security/cloud-security/cspm.md)

---

## Threat Model Highlights

- Over‑permissive execution roles (read/write to all buckets/queues/DBs).
- Unvalidated event sources (forged webhook, SNS/SQS messages from other accounts, open Pub/Sub topics).
- Insecure payload handling (SSRF, injection, deserialization).
- Secret exposure via env vars/logs; long‑lived tokens embedded in code.
- Egress/data exfiltration from public network paths.
- Supply‑chain risks (dependency RCE, unsigned artifacts).
- Event storm abuse (DoS via unbounded concurrency and retries).

---

## Identity and Permissions

Principles
- One function = one least‑privileged identity. Scope to exact resources with conditions (tags/ARN/resource paths).
- Prefer managed/workload identities; no static keys.

Patterns
- AWS Lambda: minimal IAM role with least privilege; resource ARNs narrowed to bucket prefixes/queue ARNs; condition keys (aws:SourceArn, aws:SourceAccount).
- GCP: service account per function; least role bindings; Workload Identity Federation for external triggers.
- Azure: system‑assigned managed identity; role assignments at minimal scope (resource group/resource).

Example (AWS Lambda role, concept)
```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Sid": "WriteSpecificPrefix",
    "Effect": "Allow",
    "Action": ["s3:PutObject","s3:PutObjectTagging"],
    "Resource": "arn:aws:s3:::acme-artifacts/${aws:PrincipalTag/tenant}/*"
  }]
}
```

Cross‑ref: [/docs/security/cloud-security/cloud-iam.md](/docs/security/cloud-security/cloud-iam.md)

---

## Event Source Validation

Inbound events must be authentic and expected.

- API Gateway/HTTP triggers
  - Enforce auth (OAuth 2.1/JWT/mTLS); validate iss/aud/exp; strict CORS; rate limits and WAF.
  - Idempotency keys and replay protection (nonce/timestamp; reject stale requests).
- Webhooks
  - Verify provider signatures (HMAC/Ed25519); check timestamps; canonicalize payload before verify.
- Queue/Topic triggers (SQS/SNS, Pub/Sub, Service Bus)
  - Restrict publisher principals; use resource policies that only allow specific accounts/projects/tenants.
  - Encrypt messages (server‑side; application‑level if needed) and avoid sensitive PII in cleartext.
- Storage events
  - Limit to specific buckets/prefixes; ensure bucket policies forbid cross‑account sources unless intended.

Policy concept (SNS → Lambda allowlist)
```json
{
  "Statement": [{
    "Effect": "Allow",
    "Principal": {"Service": "sns.amazonaws.com"},
    "Action": "lambda:InvokeFunction",
    "Resource": "arn:aws:lambda:...:function:ingest",
    "Condition": {
      "ArnEquals": {"AWS:SourceArn": "arn:aws:sns:...:topic:trusted"},
      "StringEquals": {"aws:SourceAccount": "123456789012"}
    }
  }]
}
```

---

## Egress and Networking

- Prefer private networking: VPC connectors (Lambda VPC, GCF Serverless VPC Access, Azure VNet integration).
- Centralize egress through NAT/proxy with domain allow‑lists for dependent APIs.
- For data stores, use private endpoints/PrivateLink/PSC to avoid public IPs.
- Restrict outbound with egress policies and firewall rules where available.

Cross‑refs:
- Network fundamentals: [/docs/security/network-security/_index.md](/docs/security/network-security/_index.md)
- Encryption & storage: [/docs/security/cloud-security/encryption-storage.md](/docs/security/cloud-security/encryption-storage.md)

---

## Secrets and Configuration

- Store secrets in managed secret stores (AWS Secrets Manager/SSM, GCP Secret Manager, Azure Key Vault).
- Inject at runtime with IAM‑bound access; avoid plain env vars for sensitive data.
- Short TTLs and rotation; dynamic DB credentials where possible.
- Never log secrets; scrub request/response payloads; mask in observability tools.

---

## Payload Safety and Data Validation

- Strict schema validation for inputs (JSON schema/protobuf) before business logic.
- Enforce size limits; reject overly large payloads to prevent memory pressure/timeouts.
- Use AEAD for sensitive fields at the application layer when using untrusted intermediaries.
- Defend against SSRF: deny metadata endpoints by default; use HTTP allow‑lists.

---

## Concurrency, Retries, and Idempotency

- Configure reserved concurrency to cap cost/impact; use DLQs/DeadLetter Sinks for poison messages.
- Implement idempotent handlers: dedupe keys, conditional writes, transactional outbox patterns.
- Exponential backoff with jitter; abort on non‑retryable errors; visibility timeouts align with processing time.

---

## Supply Chain and Build Integrity

- Lock dependencies; scan (SCA) in CI; pin allowed registries.
- Sign artifacts/images (for Cloud Run/Azure Functions containerized); verify at deploy (admission policy).
- Generate SBOMs; attach provenance (SLSA) and verify at deploy.

Cross‑ref: [/docs/security/application-security/ssdlc.md](/docs/security/application-security/ssdlc.md)

---

## Observability and Evidence

- Structured logs with request IDs, tenant IDs (if applicable), and decision outcomes; avoid PII.
- Emit security events for authz denies, signature failures, input validation rejects.
- Centralize logs (CloudWatch/Logs, Cloud Logging, Azure Monitor) with immutability/WORM for forensics.
- Metrics and alerts: error rates, throttles, DLQ depth, invocation anomalies, egress denials.

---

## Provider‑Specific Notes

AWS Lambda
- Use function URLs sparingly; prefer API Gateway with auth/WAF.
- VPC config for private data stores; ensure NAT/proxy for egress if needed.
- Use Lambda Extensions cautiously; least privilege for extensions; monitor overhead.

Google Cloud Functions / Cloud Run
- Prefer Cloud Run for long‑running or containerized workloads; enforce IAP/OIDC auth and service‑to‑service identity.
- Serverless VPC Access for private data stores; egress constraints via VPC‑SC/Firewall as applicable.

Azure Functions
- Enforce Azure AD auth on HTTP triggers; use Private Endpoints and VNet integration.
- Managed Identity for downstream access; role assignments at minimal scope.

---

## Common Pitfalls

- Wildcard IAM permissions (“*”) on storage/queues/DBs.
- Accepting unsigned/unauthenticated webhooks or ignoring timestamp drift.
- Storing secrets in env vars or code; printing them in logs on error.
- Public egress without allow‑lists; calling third‑party APIs from public IPs without validating endpoints.
- Unbounded concurrency causing downstream exhaustion; missing idempotency leading to duplicates.
- No DLQ; poison messages cause infinite retries and cost.

---

## Checklist

Identity
- [ ] One managed identity per function; least privilege with resource‑level constraints.
- [ ] Resource policies limit which sources can invoke triggers by account/topic/resource.

Events
- [ ] All webhooks/HTTP calls authenticated and signed; timestamps checked.
- [ ] Queue/topic/storage events restricted to allow‑listed sources; schemas validated.

Network
- [ ] Private endpoints to data stores; centralized egress with allow‑lists.
- [ ] No direct public DB/storage access; WAF/CDN at edges where applicable.

Secrets
- [ ] Secrets in managed store; short TTL; rotation; no secrets in env vars or logs.

Runtime
- [ ] Concurrency caps; DLQs configured; idempotency implemented.
- [ ] Timeouts, memory, and retry policies tuned; backoff with jitter.

Supply chain
- [ ] Dependencies scanned; artifacts signed; provenance verified at deploy.

Observability
- [ ] Structured logs, metrics, and alerts for security‑relevant events.
- [ ] Evidence retained immutably for audits.

---

## Cross‑References

- Cloud Best Practices: [/docs/security/cloud-security/cloud-best-practices.md](/docs/security/cloud-security/cloud-best-practices.md)
- Cloud IAM: [/docs/security/cloud-security/cloud-iam.md](/docs/security/cloud-security/cloud-iam.md)
- Encryption & Storage: [/docs/security/cloud-security/encryption-storage.md](/docs/security/cloud-security/encryption-storage.md)
- CSPM/CNAPP: [/docs/security/cloud-security/cspm.md](/docs/security/cloud-security/cspm.md)
- API Security: [/docs/security/application-security/api-security.md](/docs/security/application-security/api-security.md)
