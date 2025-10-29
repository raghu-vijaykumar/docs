---
title: "Container Security (Docker/OCI)"
draft: false
---

# Container Security (Docker/OCI)

Containers shrink the host attack surface but introduce new risks around image supply chain, privilege boundaries, inter‑pod networking, and secret handling. Harden the entire lifecycle: build (images and dependencies), store (registry and provenance), run (least privilege, isolation), and observe (runtime detection and evidence).

Cross‑refs:
- Kubernetes Security: [/docs/security/os-security/kubernetes-security.md](/docs/security/os-security/kubernetes-security.md)
- Serverless: [/docs/security/cloud-security/serverless-security.md](/docs/security/cloud-security/serverless-security.md)
- SSDLC: [/docs/security/application-security/ssdlc.md](/docs/security/application-security/ssdlc.md)
- Cloud IAM: [/docs/security/cloud-security/cloud-iam.md](/docs/security/cloud-security/cloud-iam.md)

---

## Threat Model Summary

- Malicious or vulnerable base images and dependencies.
- Privileged containers (CAP_SYS_ADMIN, host PID/net mounts) breaking isolation.
- Secret leakage via env vars, layers, or logs.
- Registry poisoning or unsigned images pulled at deploy.
- East‑west lateral movement via open network paths.
- Runtime escapes via kernel or container runtime vulnerabilities.
- Insecure volume mounts exposing host or credentials.

---

## Build: Image Hardening

Principles
- Minimal base images, pinned versions, repeatable builds, and no secrets in layers.

Practices
- Prefer distroless/scratch or slim vendor images; remove package managers and shells in final stage.
- Multi‑stage builds to keep build tools out of runtime layer.
- Pin dependency versions; lockfiles committed; vendor where feasible.

Example Dockerfile (multi‑stage, non‑root, read‑only)
```dockerfile
# Build stage
FROM golang:1.22-alpine AS build
WORKDIR /src
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 go build -trimpath -ldflags "-s -w" -o /out/app ./cmd/app

# Runtime stage
FROM gcr.io/distroless/static:nonroot
USER 65532:65532
WORKDIR /app
COPY --from=build /out/app /app/app
# Read-only fs; app must not write to image layer
ENTRYPOINT ["/app/app"]
```

Scanning
- Scan images and dependencies in CI (e.g., Trivy, Grype, Syft).
- Fail builds above severity thresholds and when fixed versions exist.

SBOM
- Generate SBOM (CycloneDX/SPDX) at build; attach as artifact and publish to registry.

---

## Supply Chain: Signing and Provenance

- Sign images with Sigstore Cosign and store attestations (SLSA provenance, SBOM).
- Verify signatures/attestations at deploy via admission control (Kyverno/Gatekeeper or platform policy).

Cosign example
```bash
# Sign and verify
cosign sign --key cosign.key ghcr.io/org/app:1.2.3
cosign verify --key cosign.pub ghcr.io/org/app:1.2.3
```

Registry controls
- Use private registries; content trust; disallow mutable tags in prod; enforce vulnerability and signature policies.
- Least privilege for CI/CD push credentials; short‑lived tokens; IP allow‑lists.

---

## Run: Least Privilege and Isolation

Security context
- Run as non‑root; drop Linux capabilities; read‑only rootfs; avoid privilege escalation.

Docker run (concept)
```bash
docker run \
  --user 65532:65532 \
  --read-only \
  --cap-drop=ALL \
  --pids-limit=256 \
  --memory=256m --cpus=0.5 \
  --security-opt no-new-privileges \
  --security-opt seccomp=/path/to/default-seccomp.json \
  --mount type=tmpfs,destination=/tmp \
  ghcr.io/org/app:1.2.3
```

Capabilities (typical minimum)
- Drop ALL; selectively add only when absolutely required (e.g., CAP_NET_BIND_SERVICE to bind <1024).
- Avoid CAP_SYS_ADMIN; it is “the new root.”

Seccomp & AppArmor
- Use the Docker/K8s default seccomp profile or stricter custom profile.
- Apply AppArmor/SELinux policies to constrain syscalls and FS access.

Volumes
- Avoid hostPath; prefer named volumes or projected secrets/configs.
- Mount with readOnly where possible; never mount host sockets (e.g., docker.sock).

Networking
- Default‑deny east‑west (K8s NetworkPolicies); expose only necessary ports.
- Define egress policies/allow‑lists for external APIs; prevent exfil.

Resource limits
- Set CPU/memory limits and requests to prevent noisy‑neighbor and DoS via resource exhaustion.

---

## Secrets Management

- Use orchestrator secret stores (K8s Secrets, plus external refs to cloud secret managers) or dedicated vaults.
- Mount secrets as tmpfs volumes or files; avoid env vars for highly sensitive values.
- Rotate automatically; scope access by workload identity (IRSA/WIF/Managed Identity).

Logging and redaction
- Never log secrets; scrub at source; use structured logging with PII redaction.

---

## Observability and Runtime Defense

- Emit structured audit and security events (authz denies, validation failures).
- Baseline process and network activity; alert on deviations (unexpected outbound, new process trees).
- Use eBPF or sensor‑based runtime tools (Falco, Cilium Tetragon) for syscall/network detections.
- Immutable log storage with retention/WORM for forensics.

---

## Policy and Admission (Kubernetes)

- Enforce Pod Security Admission “restricted” baseline.
- Admission policies to require:
  - runAsNonRoot, readOnlyRootFilesystem, drop ALL capabilities, no host namespaces, no hostPath.
  - Image signature verification and SBOM/provenance attestations.

Kyverno example (concept)
```yaml
apiVersion: kyverno.io/v1
kind: ClusterPolicy
metadata: { name: baseline-security }
spec:
  validationFailureAction: Enforce
  rules:
  - name: require-nonroot
    match: { resources: { kinds: ["Pod"] } }
    validate:
      message: "Containers must run as non-root and drop capabilities."
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

Cross‑ref: [/docs/security/os-security/kubernetes-security.md](/docs/security/os-security/kubernetes-security.md)

---

## Common Pitfalls

- Running as root; broad capabilities; writable rootfs.
- Secrets baked into images or provided via env vars and then logged.
- Pulling latest tags; no pinning; no signature verification at deploy.
- HostPath mounts and host network/pid ipc exposing the host.
- No egress control; containers can exfiltrate over arbitrary destinations.
- Missing resource limits causing cluster instability; noisy neighbors.

---

## Checklist

Build
- [ ] Minimal base, multi‑stage builds; no shells/package managers in runtime image.
- [ ] Dependencies pinned; image and deps scanned in CI; SBOM generated.

Supply chain
- [ ] Images signed (cosign); provenance/attestations produced and stored.
- [ ] Registry: private, no mutable tags in prod, vulnerability and signature gates enforced.

Runtime
- [ ] Non‑root user; read‑only rootfs; capabilities drop ALL; seccomp/AppArmor/SELinux enabled.
- [ ] No privilege escalation; no hostPath/host network/pid/ipc; volumes read‑only where possible.
- [ ] NetworkPolicies and egress allow‑lists; resource limits and requests set.

Secrets
- [ ] Secrets from manager/vault; short TTL; mounted as files; not in env; rotation automated.

Observability
- [ ] Structured logs; security events; runtime sensors; immutable log retention for forensics.

---

## Cross‑References

- Kubernetes Security: [/docs/security/os-security/kubernetes-security.md](/docs/security/os-security/kubernetes-security.md)
- Serverless Security: [/docs/security/cloud-security/serverless-security.md](/docs/security/cloud-security/serverless-security.md)
- SSDLC: [/docs/security/application-security/ssdlc.md](/docs/security/application-security/ssdlc.md)
- Cloud Best Practices: [/docs/security/cloud-security/cloud-best-practices.md](/docs/security/cloud-security/cloud-best-practices.md)
