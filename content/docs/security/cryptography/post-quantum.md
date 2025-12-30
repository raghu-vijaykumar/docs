---
title: "Post‑Quantum Cryptography (PQC)"
draft: false
---

# Post‑Quantum Cryptography (PQC)

Quantum computers threaten common public‑key algorithms (RSA, ECDH/ECDSA) via Shor’s algorithm. Symmetric crypto (AES, HMAC) and hashes (SHA‑2/SHA‑3) are comparatively resilient (Grover’s algorithm gives only a quadratic speedup). The practical risk today is “harvest‑now, decrypt‑later”: attackers record traffic and data encrypted with classical public‑key schemes, intending to decrypt it once large quantum machines exist.

This guide explains the PQC landscape, NIST’s standards, migration patterns, and how to adopt crypto‑agility without breaking systems.

---

## What’s at Risk (and What Isn’t)

- At high risk (public‑key)
  - Key exchange: ECDH (X25519/P‑256), RSA key exchange.
  - Signatures: ECDSA (P‑256, P‑384), RSA‑PKCS#1/RSA‑PSS.
- Lower risk (symmetric and hashing)
  - AES‑128 remains acceptable (Grover implies ~64‑bit security); AES‑256 recommended for long‑term confidentiality.
  - HMAC‑SHA‑256/512, SHA‑256/512: increase output length if margins are required.

Implication
- Transition public‑key algorithms to quantum‑resistant alternatives.
- Increase symmetric key sizes (128 → 256) for long‑term secrets.

---

## NIST Standardization Status

NIST PQC (Round 3/4) selections (2022–2024) and drafts (2024–2025):

- Key Encapsulation Mechanism (KEM)
  - ML‑KEM (Kyber): NIST’s standardized KEM for key exchange.
- Digital Signatures
  - ML‑DSA (Dilithium): lattice‑based, primary signature scheme.
  - Falcon: lattice‑based, smaller signatures, more implementation complexity.
  - SPHINCS+: hash‑based, conservative fallback with larger signatures.

Operational guidance
- Prefer ML‑KEM (Kyber) for key agreement (often in hybrid with X25519).
- Prefer ML‑DSA (Dilithium) for code signing and certificates as ecosystems mature.
- Keep SPHINCS+ available for high‑assurance contexts.

---

## Migration Patterns

Hybrid first
- Combine classical + PQ algorithms so security holds unless both are broken.
  - TLS 1.3 hybrid KEM: X25519 + ML‑KEM (Kyber) in the handshake.
  - SSH hybrid KEX: sntrup761x25519 (widely deployed), with future ML‑KEM variants.
- Benefits: Backward compatibility and resilience during the transition.

Crypto‑agility
- Abstract algorithm choices behind policy/config; make them updateable without code changes.
- Negotiate algorithms at runtime (TLS cipher/KEM suites, SSH KEX lists, JOSE alg headers).

Phased rollout
1) Inventory cryptographic use (TLS, SSH, code signing, CT logs, PKI, application tokens).
2) Enable hybrid KEM in TLS on internal links first; monitor handshake metrics.
3) Pilot PQ signatures for internal artifacts (container/image signing).
4) Coordinate with CAs/PKI vendors for PQ‑ready certificates and CT integration.
5) Switch external endpoints as clients/browsers support grows.

---

## TLS: Where to Start

- TLS 1.3 hybrid KEM (X25519 + ML‑KEM):
  - Many vendors/CDNs have piloted hybrid key exchange; expect mainstream support across 2024–2026.
  - Monitor handshake size/latency impact (slightly larger ClientHello/ServerHello).
- Cipher suites remain AEAD (AES‑GCM/ChaCha20‑Poly1305).
- Keys derived via HKDF from the hybrid shared secret.

Operational tips
- Keep session resumption and 0‑RTT disabled unless risk‑assessed.
- Ensure middleboxes and WAF/CDN paths tolerate larger handshakes.

---

## SSH and Internal Services

- SSH already supports hybrid (e.g., sntrup761x25519) in many distributions.
- Prefer host/user key algorithms that remain strong (Ed25519) while planning PQ updates.
- Service meshes and mTLS:
  - Track vendor roadmaps for hybrid KEMs at the proxy/mTLS layer.
  - Expect control‑plane and data‑plane updates to be staggered.

---

## PKI, Certificates, and Signatures

- Certificates
  - Classical roots will persist; introduce PQ end‑entity keys when browser/OS trust stores support them.
  - Hybrid or multi‑cert approaches may be used during transition.
- Code/artifact signing
  - Adopt ML‑DSA (Dilithium) for internal signing early; retain classical signatures in parallel for ecosystem compatibility.
  - Store signatures and attestations with algorithm metadata for verification agility.

Storage and archival
- Encrypt long‑term data with AES‑256; wrap keys with PQ KEM or store in KMS/HSM that supports PQ wrapping when available.

---

## Performance and Size Considerations

- KEM (Kyber): small ciphertexts/keys and fast operations; good for TLS.
- Signatures (Dilithium): larger public keys/signatures than ECDSA; assess bandwidth/storage impact.
- Falcon: smaller signatures but harder to implement securely (floating‑point nuances).

Measure
- Track handshake time, CPU, memory, and failure rates.
- Watch for MTU and fragmentation on constrained links due to larger handshakes/certs.

---

## Implementation Checklist

- [ ] Inventory all public‑key usages (TLS, SSH, S/MIME, PGP, code signing, JWT/JWS/JWE, mTLS/mesh).
- [ ] Enable hybrid KEM (X25519 + ML‑KEM) where supported; monitor compatibility and metrics.
- [ ] Raise symmetric strength to AES‑256 for long‑term confidentiality workloads.
- [ ] Begin dual‑signing internal artifacts with ML‑DSA + current algorithm; store both.
- [ ] Ensure crypto‑agility: configurable algs, negotiable suites, and metadata‑rich signatures.
- [ ] Plan PKI evolution: CA/vendor support for PQC, CT logging implications, and rollover strategy.
- [ ] Educate teams on “harvest‑now, decrypt‑later” risk and retention policies for sensitive recordings.

---

## Common Pitfalls

- Big‑bang cutovers without hybrid → client incompatibility and outages.
- Hard‑coding algorithms in code → expensive migrations later.
- Ignoring long‑term secrets (backups, archives) that need AES‑256 and PQ wrapping now.
- Underestimating certificate/handshake size impacts on constrained clients and middleboxes.

---

## Example: Conceptual Hybrid TLS Config

Note: Real configuration is vendor‑specific and evolving. Conceptually:
```text
tls:
  min_version: TLS1.3
  kem_groups:
    - x25519_mlkem768   # Hybrid group (example naming)
    - x25519            # Fallback for legacy
  cipher_suites:
    - TLS_AES_256_GCM_SHA384
    - TLS_CHACHA20_POLY1305_SHA256
  certificate:
    classical_key: ECDSA_P256
    pq_signature: ML-DSA (Dilithium2)  # dual-signing during transition
```

---

## Cross‑References

- TLS configuration: [/docs/security/cryptography/tls-ssl/](/docs/security/cryptography/tls-ssl/)
- PKI and certificates: [/docs/security/cryptography/pki/](/docs/security/cryptography/pki/)
- Symmetric vs asymmetric: [/docs/security/cryptography/symmetric-vs-asymmetric.md](/docs/security/cryptography/symmetric-vs-asymmetric.md)
