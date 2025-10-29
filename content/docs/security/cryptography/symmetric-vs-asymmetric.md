---
title: "Symmetric vs Asymmetric Cryptography"
draft: false
---

# Symmetric vs Asymmetric Cryptography

Modern systems combine symmetric and asymmetric cryptography. Symmetric ciphers are fast and used for bulk encryption; asymmetric algorithms solve key distribution and identity (signatures, key exchange). Understanding when and how to use each is foundational for building secure protocols and applications.

---

## Concepts

Symmetric (one key)
- Same secret key encrypts and decrypts.
- Very fast; constant‑time primitives exist in hardware (AES‑NI, ARMv8 AES).
- Used for: bulk data encryption (at rest, in transit after handshake), MACs (HMAC), AEAD (e.g., AES‑GCM, ChaCha20‑Poly1305).

Asymmetric (public/private keys)
- Public key encrypts or verifies; private key decrypts or signs.
- Slower; keys are larger; ideal for identity, signatures, and key exchange.
- Used for: TLS handshakes (key exchange), digital signatures (authenticity), certificate infrastructure (PKI).

Hybrid cryptography
- Real‑world protocols (TLS) use asymmetric crypto to establish shared secrets, then switch to symmetric AEAD for data.

---

## Symmetric Encryption: AEAD Modes

Choose authenticated encryption with associated data (AEAD) to provide confidentiality, integrity, and authenticity.

Recommended algorithms
- AES‑GCM (128/256) — hardware‑accelerated on most CPUs; widely supported.
- ChaCha20‑Poly1305 — faster on devices without AES acceleration; constant‑time.

Best practices
- Never reuse (key, nonce) pairs; track nonces safely (counters/unique random).
- Use distinct keys for encryption and MAC if not using AEAD (but prefer AEAD).
- Separate context/AD for protocol binding (e.g., record type, version, headers).

Node.js example (AES‑GCM)
```js
import crypto from "crypto";

function encrypt(plain, key) {
  const iv = crypto.randomBytes(12);
  const cipher = crypto.createCipheriv("aes-256-gcm", key, iv);
  const ct = Buffer.concat([cipher.update(plain), cipher.final()]);
  const tag = cipher.getAuthTag();
  return { iv, ct, tag };
}

function decrypt({ iv, ct, tag }, key) {
  const decipher = crypto.createDecipheriv("aes-256-gcm", key, iv);
  decipher.setAuthTag(tag);
  return Buffer.concat([decipher.update(ct), decipher.final()]);
}
```

Key sizes
- AES‑128 is typically sufficient; AES‑256 for long‑term secrets or policy requirements.

---

## Integrity and MACs

- HMAC (with SHA‑256/512) provides message integrity and authenticity with a symmetric key.
- Prefer AEAD over “encrypt‑then‑MAC” unless interoperability demands HMAC.

Example (HMAC)
```js
import crypto from "crypto";
const mac = crypto.createHmac("sha256", key).update(message).digest("hex");
```

---

## Asymmetric Cryptography

Key exchange
- ECDH (X25519, P‑256) to derive a shared symmetric key over an insecure channel.
- In TLS 1.3, ephemeral ECDH provides forward secrecy.

Signatures
- Ed25519 (fast, modern) or ECDSA P‑256 for compatibility; RSA‑PSS for legacy ecosystems.
- Use signatures to authenticate software releases, tokens, and handshakes.

Encryption (public‑key)
- RSA‑OAEP is still used but being replaced by hybrid KEMs (e.g., Kyber) in post‑quantum TLS.
- Generally, prefer key exchange + symmetric AEAD over direct public‑key encryption of large data.

---

## Putting It Together: TLS Handshake (Simplified)

1) Client and server agree on a key exchange (e.g., X25519).
2) Ephemeral ECDH produces shared secret; server proves identity via certificate (signature).
3) Both sides derive session keys (HKDF) for symmetric AEAD (AES‑GCM/ChaCha20‑Poly1305).
4) All application data uses symmetric AEAD; keys rotated as needed.

See: [TLS/SSL Configuration](/docs/security/cryptography/tls-ssl/) and [PKI](/docs/security/cryptography/pki/)

---

## Key Management Considerations

- Entropy: generate keys with CSPRNG; never derive manually.
- Storage: keep symmetric keys in KMS/HSM; control access and audit usage.
- Rotation: rotate keys periodically and on compromise; monitor for reuse of nonces.
- Separation: different keys per purpose (enc vs mac; prod vs stage; per tenant if feasible).

---

## Common Pitfalls

- Rolling your own mode (CBC + homemade MAC or padding) — use AEAD primitives.
- Nonce reuse in GCM → catastrophic integrity failure.
- Misusing RSA PKCS#1 v1.5 padding — prefer OAEP (encryption) and PSS (signatures).
- Over‑reliance on RSA where elliptic‑curve or post‑quantum KEMs are more appropriate.
- Storing long‑lived symmetric keys in code or config; not using a KMS.

---

## Algorithm Selection Quick Guide

- Symmetric AEAD: AES‑256‑GCM (servers/desktops) or ChaCha20‑Poly1305 (mobile/IoT).
- Key exchange: X25519 (default), P‑256 (compat), add Kyber (hybrid) for PQ mitigation.
- Signatures: Ed25519 (default), ECDSA P‑256 (compat), RSA‑PSS (legacy/PKI interop).
- Hashing: SHA‑256/SHA‑512; use Argon2id/bcrypt/scrypt for passwords (see [Hashing](/docs/security/cryptography/hashing/)).

---

## Checklist

- [ ] Use AEAD (AES‑GCM or ChaCha20‑Poly1305) for encryption.
- [ ] Derive shared keys via (EC)DH and rotate per session.
- [ ] Authenticate endpoints with signatures and certificates (PKI).
- [ ] Manage keys in KMS/HSM; rotate and audit use.
- [ ] Avoid deprecated primitives and padding modes.

---

## Cross‑References

- TLS: [/docs/security/cryptography/tls-ssl/](/docs/security/cryptography/tls-ssl/)
- PKI & Certificates: [/docs/security/cryptography/pki/](/docs/security/cryptography/pki/)
- Hashing & Passwords: [/docs/security/cryptography/hashing/](/docs/security/cryptography/hashing/)
