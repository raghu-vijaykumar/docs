---
title: "Kubernetes Security"
draft: false
---

# Kubernetes Security

Kubernetes expands the attack surface across API server, control plane components, nodes, container runtime, networking, and supply chain. A secure cluster enforces least privilege from admission to data plane, validates software provenance, limits egress and lateral movement, and produces evidence for incident response.

Cross-refs:
- Containers: [/docs/security/os-security/container-security.md](/docs/security/os-security/container-security.md)
- Cloud Best Practices: [/docs/security/cloud-security/cloud-best-practices.md](/docs/security/cloud-security/cloud-best-practices.md)
- Serverless: [/docs/security/cloud-security/serverless-security.md](/docs/security/cloud-security/serverless-security.md)
- CSPM/CNAPP: [/docs/security/cloud-security/cspm.md](/docs/security/cloud-security/cspm.md)
- SSDLC: [/docs/security/application-security/ssdlc.md](/docs/security/application-security/ssdlc.md)
- Segmentation/Zero Trust: [/docs/security/network-security/segmentation-zero-trust.md](/docs/security/network-security/segmentation-zero-trust.md)

---

## Threat Model Summary

- API server exposure and weak authn/z (cluster-admin via default bindings).
- Insecure admission (unrestricted privilege escalation, hostPath, host networking).
- Supply chain risks: unsigned images, mutable tags, vulnerable base images.
- Lateral movement: pods without NetworkPolicies, shared service accounts.
- Node compromise → container breakout, secret theft, workload tampering.
- Etcd exposure or weak encryption → secret disclosure.
- Lack of egress control → exfiltration and C2.

---

## Cluster Hardening Overview

- Identity and AuthN/Z
  - Strong authentication (OIDC/webhook); disable anonymous; remove legacy tokens.
  - RBAC deny-by-default; avoid cluster-admin grants; bind narrowly to namespaces.
- Admission and Policy
  - Pod Security Admission (PSA) set to at least baseline, preferably restricted.
  - Policy-as-code (Kyverno/Gatekeeper) to enforce non-root, read-only FS, capability drops, signature verification.
- Network and Segmentation
  - Default-deny NetworkPolicies; namespace-based segmentation per app/tenant/env.
  - Restrict egress; private endpoints to cloud services (IRSA/WIF/Managed Identity).
- Supply Chain
  - Sign images (cosign); verify signatures and SBOM/provenance at admission.
  - Scan images (Trivy/Grype) pre-deploy; disallow mutable tags in prod.
- Secrets and Data
  - Encrypt Secrets at rest (etcd encryption); use external secret manager integration.
  - Mount secrets as files (tmpfs) with least privilege; avoid env vars for highly sensitive values.
- Nodes and Runtime
  - Hardened base OS; disable unused services; auto-patching; restricted container runtime.
  - Drop capabilities, no privilege escalation, seccomp/AppArmor/SELinux.
- Observability and IR
  - Centralized, immutable audit logs; runtime sensors (Falco/Tetragon); forensics readiness.

---

## Authentication and Authorization (RBAC)

- Disable anonymous access:
  - apiserver flags: --anonymous-auth=false (or restrict via RBAC).
- Prefer OIDC for users/groups; short-lived tokens; no static client-certs for humans.
- Service Accounts
  - One service account per workload; no default service account use (set automountServiceAccountToken: false where possible).
  - Cloud identity binding: IRSA (EKS)/Workload Identity (GKE)/Azure AD Workload Identity (AKS) for cloud API access.

Example (restrict default SA)
```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: default
  namespace: app
automountServiceAccountToken: false
```

RBAC principle
- No clusterrolebinding to cluster-admin; create minimal roles and namespace-scoped bindings.
- Use labels/annotations to drive automated binding where appropriate; review regularly.

---

## Admission Controls and Pod Security

Pod Security Admission (PSA)
- Set namespace labels:
  - pod-security.kubernetes.io/enforce: "restricted"
  - pod-security.kubernetes.io/audit: "restricted"
  - pod-security.kubernetes.io/warn: "restricted"

Example (namespace with PSA)
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: payments
  labels:
    pod-security.kubernetes.io/enforce: "restricted"
    pod-security.kubernetes.io/enforce-version: "latest"
```

Policy as Code (Kyverno/Gatekeeper)
- Enforce non-root, read-only rootfs, drop ALL capabilities, no host namespaces, no hostPath.
- Require image signatures and disallow latest tags in prod.
- Validate required labels/annotations (owner, env, data-classification) for governance.

Kyverno example (non-root baseline)
```yaml
apiVersion: kyverno.io/v1
kind: ClusterPolicy
metadata: { name: baseline-security }
spec:
  validationFailureAction: Enforce
  rules:
  - name: require-sec-settings
    match: { resources: { kinds: ["Pod"] } }
    validate:
      message: "Non-root, read-only, no privilege escalation, drop ALL caps required."
      pattern:
        spec:
          securityContext:
            runAsNonRoot: true
          containers:
          - securityContext:
              readOnlyRootFilesystem: true
              allowPrivilegeEscalation: false
              capabilities:
                drop: ["ALL"]
```

---

## Network Policies and Egress Control

- Default deny:
  - Create a baseline deny-all ingress/egress policy per namespace; allow only necessary flows.
- Service-to-service allow lists:
  - Policies reference selectors (app/role/tenant) rather than IPs.
- Egress:
  - Use egress policies and CNI features (e.g., Cilium egress gateway) to restrict external destinations.
  - For cloud services, prefer PrivateLink/PSC and VPC-native controls.

Deny-all baseline
```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: default-deny
  namespace: app
spec:
  podSelector: {}
  policyTypes: ["Ingress","Egress"]
```

---

## Supply Chain: Images, Signing, and Provenance

- Build
  - Minimal/distroless images; multi-stage builds; dependency pinning; SBOM generated.
- Sign
  - Sign images with cosign; store attestations (provenance, SBOM).
- Verify
  - Admission verifies signature, issuer, and provenance; block unsigned/mutable-tag images.

Kyverno verifyImages (concept)
```yaml
apiVersion: kyverno.io/v1
kind: ClusterPolicy
metadata: { name: verify-images }
spec:
  rules:
  - name: verify
    match: { resources: { kinds: ["Pod"] } }
    verifyImages:
    - imageReferences: ["ghcr.io/org/*"]
      attestors:
      - entries:
        - keys: { publicKeys: |
            -----BEGIN PUBLIC KEY-----
            ...
            -----END PUBLIC KEY----- }
```

Registry controls
- Private registries; no :latest in prod; vulnerability thresholds enforced.

---

## Secrets and etcd Encryption

- Enable encryption at rest for Secrets (api-server encryption-config).
- External secret managers (AWS Secrets Manager/SSM, GCP Secret Manager, Azure Key Vault) integrated via CSI drivers or external-secrets operator.
- Mount secrets as files; restrict read permissions; avoid env vars for highly sensitive secrets.

KMS/etcd reference
- Ensure etcd not publicly reachable; TLS for etcd peer/client; restrict access to control plane nodes only.

---

## Nodes, Runtime, and OS Hardening

- OS
  - CIS-hardened base; auto-update critical patches; disable swap (K8s requirement); restrict SSH.
- Kubelet
  - Rotate certificates; require auth; protect read-only port; restrict anonymous auth.
- Runtime
  - Containerd preferred; seccomp default; AppArmor/SELinux enforcing where applicable.
- Resource constraints
  - Set requests/limits to prevent resource exhaustion; use PDBs and HPA responsibly.

---

## Multi-Tenancy and Isolation

- Soft multi-tenancy (namespaces)
  - PSA restricted, default-deny NetPolicies, per-namespace resource quotas, separate service accounts and roles.
- Stronger isolation
  - Separate clusters per tenant/risk domain; separate node pools; consider gVisor/Kata for workload isolation.
- Ingress
  - Separate ingress controllers per tenant/risk domain; WAF integration and mTLS for internal.

---

## Observability, Audit, and Incident Response

- Audit logs
  - Enable and centralize API audit logs; WORM/immutable storage; SIEM parsing and detections (sudden rolebinding changes, secrets read spikes).
- Runtime detections
  - Falco/Tetragon for syscall/network anomalies (reverse shells, crypto miners, unexpected outbound).
- Forensics readiness
  - Golden AMIs/Images; node snapshot procedures; container image and SBOM archives; admission decision logs.

Detections to prioritize
- Privilege escalation attempts (hostPath, privileged: true).
- New clusterrolebindings to cluster-admin.
- Pods starting with :latest or from unapproved registries.
- Excessive secret reads by a single SA.

---

## Backup, DR, and Upgrades

- Etcd backups encrypted and tested restore.
- Declarative cluster state (GitOps) with signed manifests; restore via re-apply.
- Regular K8s upgrades; keep N-1 support policy; test PSA/policy impact before upgrade.

---

## Common Pitfalls

- Using default service account with broad permissions.
- No NetworkPolicies → flat network and unbounded lateral movement.
- Allowing hostPath, hostNetwork, privileged pods by default.
- Pulling images with :latest and no signature verification.
- Secrets in env vars and logs; etcd not encrypted at rest.
- Single cluster shared by untrusted tenants without strong isolation controls.

---

## Checklist

Identity and RBAC
- [ ] Anonymous disabled; OIDC/Webhook auth; short-lived tokens.
- [ ] No cluster-admin bindings; namespace-scoped roles and bindings; SA per workload; automount disabled when not needed.

Admission and Policy
- [ ] PSA enforced at restricted; policy engine enforces non-root, read-only FS, no host namespaces, drop ALL caps.
- [ ] Image signature and SBOM/provenance verification at admission; no mutable tags in prod.

Network
- [ ] Default-deny NetworkPolicies; least-privilege allow lists; egress restrictions and PrivateLink/PSC for cloud services.

Secrets and Data
- [ ] etcd encryption at rest; external secret manager integration; secrets mounted as files; least-privilege access.

Nodes and Runtime
- [ ] Hardened OS; kubelet secured; containerd + seccomp/AppArmor/SELinux; resource requests/limits.

Observability and IR
- [ ] Centralized, immutable audit logs; runtime sensors; SIEM detections; incident playbooks and forensics procedures.

DR and Upgrades
- [ ] Encrypted etcd backups tested; GitOps for desired state; regular version upgrades validated against policies.

---

## Cross-References

- Container Security: [/docs/security/os-security/container-security.md](/docs/security/os-security/container-security.md)
- Cloud IAM and Workload Identity: [/docs/security/cloud-security/cloud-iam.md](/docs/security/cloud-security/cloud-iam.md)
- CSPM/CNAPP: [/docs/security/cloud-security/cspm.md](/docs/security/cloud-security/cspm.md)
- SSDLC: [/docs/security/application-security/ssdlc.md](/docs/security/application-security/ssdlc.md)
- Network Segmentation & Zero Trust: [/docs/security/network-security/segmentation-zero-trust.md](/docs/security/network-security/segmentation-zero-trust.md)
