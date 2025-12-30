---
title: "Public Key Infrastructure (PKI)"
draft: false
---

# Public Key Infrastructure (PKI)

PKI provides the trust fabric for authenticating endpoints, establishing encrypted channels, and signing artifacts. It defines how keys and certificates are issued, validated, rotated, and revoked in a way that scales across organizations and services.

This guide focuses on operational PKI for TLS, mTLS, service identity, and code signing, with concrete practices and pitfalls.

---

## Core Concepts

- Root CA
  - Offline, long‑lived, self‑signed certificate.
  - Signs intermediate CAs only. Protect with HSM, multi‑party control, and physically secured environment.

- Intermediate CA
  - Online or semi‑online. Shorter lifetime than root (e.g., 1–5 years).
  - Enforces issuance policy via profiles (EKU, key usages, name constraints).
  - Multiple intermediates allow staged rotations and scoping by use (server auth, client auth, code signing).

- End‑Entity Certificate
  - Leaf certificates used by servers, clients, or for code signing.
  - Short‑lived (hours/days) preferred when automated; 90 days common for public TLS.

- Certificate Chain
  - Leaf → Intermediate(s) → Root. Clients validate signatures up the chain to a trusted root.

- Trust Store
  - Client’s CA bundle. Public (OS/browser) or private (enterprise). Keep separate per environment/tenant where appropriate.

---

## Certificate Profiles and Extensions

- Key Usage
  - Digital Signature (signatures), Key Encipherment / Key Agreement (TLS 1.2 and below), Content Commitment (code signing).

- Extended Key Usage (EKU)
  - ServerAuth (TLS server), ClientAuth (TLS client/mTLS), CodeSigning, EmailProtection (S/MIME).
  - Constrain leaf certs to necessary EKUs only.

- Subject Alternative Name (SAN)
  - DNS names and/or IP addresses. Required for modern TLS; CN is ignored for name verification.

- Name Constraints (for intermediates)
  - Limit issuance to specific domains (e.g., permittedDNS=*.example.com). Powerful, but not universally supported in public PKI.

---

## Certificate Lifecycle

- Issuance
  - Generate keypair, create CSR with correct SANs and EKUs, submit to CA.
  - For public TLS, use ACME (Let’s Encrypt/ZeroSSL); for private PKI, automate via APIs (Vault PKI/Smallstep/SPiffe).

- Rotation
  - Prefer short‑lived certs with automated renewal.
  - Roll intermediate CAs with overlap; publish AIA/CRL/OCSP correctly; stage clients to trust new intermediates first.

- Revocation
  - OCSP stapling on servers; CRLs for legacy; short lifetime reduces dependency on revocation.
  - For internal PKI, implement revocation signaling via OCSP responders and distribution points.

- Auditing
  - Log issuance and revocation with immutable storage; monitor for unusual patterns (e.g., spikes, off‑hours issuance).

---

## Operational Patterns

### ACME for Public TLS

- ACME automates domain validation (HTTP‑01/DNS‑01) and issuance.
- Use certbot, lego, or platform‑native integrations (Cloudflare, AWS ACM).
- Prefer 90‑day certificates with auto‑renewal and pre‑expiry alerting.

### Private PKI for mTLS and Service Identity

- Options: HashiCorp Vault PKI, Smallstep CA, SPIFFE/SPIRE (X.509 SVIDs), Cloud provider CAs (AWS PCA, GCP CAS, Azure).
- Automate issuance via sidecars/agents (e.g., SPIRE agent) or bootstrap using short‑lived bootstrap tokens.
- Use distinct trust domains per environment (dev/test/prod) to prevent credential reuse.

### Key Management

- Server keys: generated on host/HSM; restrict filesystem ACLs; separate ownership from application user where possible.
- Client keys: distribute via enrollment protocols (EST, SCEP) or agent‑based issuance; protect at rest (TPM/secure enclave).
- Signing keys: code‑signing keys must be HSM‑backed and gated by approvals.

---

## Example Workflows

### Generate a CSR with OpenSSL

```bash
# Generate key (ECDSA P-256)
openssl ecparam -name prime256v1 -genkey -noout -out server.key

# Create CSR with SANs
cat > san.cnf <<'EOF'
[ req ]
default_bits       = 4096
prompt             = no
default_md         = sha256
req_extensions     = req_ext
distinguished_name = dn

[ dn ]
CN = app.example.com

[ req_ext ]
subjectAltName = @alt_names
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth

[ alt_names ]
DNS.1 = app.example.com
DNS.2 = www.example.com
EOF

openssl req -new -key server.key -out server.csr -config san.cnf
```

### Issue a Leaf Cert with Smallstep (private PKI)

```bash
# Initialize a CA (once)
step ca init --name "Example Internal CA" --dns ca.example.internal --address ":443" --provisioner admin@example.com

# Create a certificate (server)
step ca certificate "app.example.internal" server.crt server.key \
  --san app.example.internal --san app.prod.svc.cluster.local
```

### Vault PKI Issuance (Conceptual)

```bash
# Configure a role
vault write pki_int/roles/app-role \
  allowed_domains="example.internal,svc.cluster.local" \
  allow_subdomains=true \
  server_flag=true client_flag=false \
  max_ttl="72h"

# Issue a cert
vault write pki_int/issue/app-role common_name="app.example.internal" ttl="24h"
```

---

## mTLS Deployment Guidance

- Identity
  - Use SPIFFE IDs in SAN URI (spiffe://trust-domain/ns/namespace/sa/service) to decouple identity from DNS.
  - Enforce authorization at L7 using identity from the client certificate.

- Trust and Renewal
  - Separate trust bundles per environment; rotate CAs with overlapping validity.
  - Automate cert rollover; reload servers without downtime (SIGHUP, hot reload).

- Policy
  - Constrain client cert EKU to ClientAuth only; limit SANs to expected subjects (or SPIFFE URIs).
  - Implement deny‑by‑default at gateways and validate cert chains to your private roots only.

---

## Certificate Transparency and Monitoring

- Public TLS must be logged to CT logs; rely on CT monitoring (e.g., crt.sh, custom watchers) to detect misissuance.
- Private PKI: Build internal transparency (append‑only logs, signed issuance records) to support forensics and compliance.

---

## Troubleshooting

- Handshake failures
  - Check protocol/cipher overlap, SNI/SAN mismatch, expired certs, missing intermediates (send full chain).
- Path building issues
  - Ensure AIA points to intermediates; include correct order in server chain file.
- Name verification failures
  - CN ignored; SAN must include exact DNS/IP; wildcards do not match multi‑label (e.g., *.example.com ≠ a.b.example.com).

---

## Security Hardening

- Short lifetimes: Prefer hours/days for internal certs; automate renewal.
- Key protection: HSM/TPM where possible; never store private keys in Git or images.
- Separation of duties: Different approvers for CA ops vs issuance; dual control for CA key usage.
- Environment isolation: Distinct trust domains and CAs for dev/staging/prod.
- Least privilege: Limit which roles can request which SANs/EKUs.

---

## Common Pitfalls

- Using CN instead of SAN; clients fail hostname verification.
- Shipping incomplete chains; some clients cannot fetch intermediates.
- Long‑lived leaf certs (1+ year); increases risk and complicates revocation.
- Overly permissive issuance policies (any SAN) leading to privilege escalation.
- Failing to roll intermediates before expiry; widespread outages.

---

## Checklist

- [ ] Root CA offline with HSM; intermediates online with scoped policy.
- [ ] Issuance automated (ACME/private API); short‑lived leaf certs.
- [ ] SAN/EKU/key usages constrained per role; distinct trust bundles per environment.
- [ ] OCSP stapling enabled; CT monitored for public certs.
- [ ] Full chain served by endpoints; intermediates rotated with overlap.
- [ ] Keys protected (HSM/TPM/KMS); no private keys in source/images.
- [ ] Audit logs for issuance/revocation with alerting on anomalies.
