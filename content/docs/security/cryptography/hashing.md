---
title: "Hashing and Password Storage"
draft: false
---

# Hashing and Password Storage

Hashing underpins integrity checks, fingerprinting, and secure password storage. Use the right primitive for the job: plain cryptographic hashes (SHA‑2/3) for checksums and indexing, HMAC for integrity with authenticity, and specialized password hash/KDFs (Argon2id/bcrypt/scrypt/PBKDF2) for user secrets.

---

## Hash vs HMAC vs Password Hash

- Cryptographic hash (e.g., SHA‑256)
  - One‑way, deterministic digest function.
  - Use for: file checksums, content addressing, deduplication.
  - Do not use raw hashes for passwords or message authenticity.

- HMAC (e.g., HMAC‑SHA‑256)
  - Hash + secret key → message authentication code.
  - Provides integrity + authenticity with a shared secret.
  - Use for: authenticating webhooks, API request signing, cookie MACs.

- Password hashing / KDF (Argon2id, bcrypt, scrypt, PBKDF2)
  - Slow and often memory‑hard to resist offline cracking.
  - Use for: user password storage and verification.

---

## Algorithm Guidance

- Broken/legacy: MD5, SHA‑1 (collision attacks) — avoid.
- Recommended general hashes: SHA‑256, SHA‑512 (SHA‑2); SHA3‑256/512.
- HMAC: HMAC‑SHA‑256/512 with a unique key; rotate keys periodically.
- Password hashing:
  - Preferred: Argon2id (memory‑hard, modern).
  - Also safe: bcrypt (widely available), scrypt (memory‑hard).
  - Last resort for compatibility: PBKDF2‑HMAC‑SHA‑256 with high iterations.

Parameter suggestions (2025 hardware; adjust over time)
- Argon2id: memory=64–256 MB, iterations=1–3, parallelism=2–4, target ~100–250 ms
- bcrypt: cost 12–14 (target ~100–250 ms)
- scrypt: N≈2^15, r=8, p=1 (target ~100–250 ms)
- PBKDF2: ≥ 310k iterations (target ~100–250 ms)

Always tune for your environment; re‑benchmark annually.

---

## Password Storage Design

- Per‑user unique random salt (16–32 bytes). Store the salt alongside the hash.
- Use a site‑wide “pepper” (HMAC key or KMS‑protected secret) optionally wrapped by KMS/HSM; never store pepper in the DB.
- Store algorithm, parameters, salt, and hash to support rehashing.
- Enforce strong credential hygiene (length, breach checks, MFA).

Storage format example
```
$argon2id$v=19$m=65536,t=2,p=1$base64(salt)$base64(hash)
```

---

## Code Examples

### Node.js — bcrypt

```js
import bcrypt from "bcrypt"; // or `const bcrypt = require('bcrypt')`

const COST = 12;

export async function hashPassword(password) {
  // password: UTF-8 string
  const salt = await bcrypt.genSalt(COST);
  return bcrypt.hash(password, salt); // returns encoded salt+hash
}

export async function verifyPassword(password, storedHash) {
  return bcrypt.compare(password, storedHash); // constant-time compare
}
```

Rehash on login (when increasing cost):
```js
export async function login(username, password) {
  const user = await db.findUser(username);
  if (!user) return false;
  const ok = await bcrypt.compare(password, user.pwHash);
  if (!ok) return false;

  // If cost too low, transparently rehash
  const rounds = parseInt(user.pwHash.split("$")[2], 10); // "$2b$12$..."
  if (rounds < COST) {
    const newHash = await bcrypt.hash(password, await bcrypt.genSalt(COST));
    await db.updateUserHash(user.id, newHash);
  }
  return true;
}
```

### Python — Argon2id

```python
from argon2 import PasswordHasher

# ~100-250ms target depending on hardware; tune memory_cost/time_cost
ph = PasswordHasher(
    time_cost=2,
    memory_cost=65536,  # 64 MiB
    parallelism=2
)

def hash_password(password: str) -> str:
    return ph.hash(password)

def verify_password(password: str, stored: str) -> bool:
    try:
        ph.verify(stored, password)
        # Optional: rehash if params outdated
        if ph.check_needs_rehash(stored):
            new_hash = ph.hash(password)
            # persist new_hash
        return True
    except Exception:
        return False
```

### Go — Argon2id and bcrypt

```go
// Argon2id
package auth

import (
  "crypto/rand"
  "encoding/base64"
  "golang.org/x/crypto/argon2"
)

type Argon2Params struct {
  Time    uint32 // iterations
  Memory  uint32 // KiB
  Threads uint8
  KeyLen  uint32
}

var params = Argon2Params{Time: 2, Memory: 64 * 1024, Threads: 2, KeyLen: 32}

func HashArgon2id(password string) (encoded string, err error) {
  salt := make([]byte, 16)
  if _, err = rand.Read(salt); err != nil { return "", err }
  key := argon2.IDKey([]byte(password), salt, params.Time, params.Memory, params.Threads, params.KeyLen)
  return "$argon2id$v=19$m=65536,t=2,p=2$" +
    base64.RawStdEncoding.EncodeToString(salt) + "$" +
    base64.RawStdEncoding.EncodeToString(key), nil
}
```

```go
// bcrypt
import "golang.org/x/crypto/bcrypt"

func HashBcrypt(password string, cost int) (string, error) {
  b, err := bcrypt.GenerateFromPassword([]byte(password), cost) // cost 12–14
  return string(b), err
}

func VerifyBcrypt(password, hash string) bool {
  return bcrypt.CompareHashAndPassword([]byte(hash), []byte(password)) == nil
}
```

---

## Migrating from Weak Hashes

- Identify legacy hashes (MD5/SHA‑1/PBKDF2 low iterations).
- Use “rehash on successful login”:
  - Verify using old algorithm; if valid, rehash with modern KDF and update.
- For users who never log in, consider forced password reset with secure flow.
- Keep dual‑stack verification only as long as necessary.

---

## API and Operational Hygiene

- Rate limit password verification; introduce small, jittered delays on failures.
- Use constant‑time comparison (library routines) to avoid timing leaks.
- Prevent user enumeration (uniform responses for “user not found” vs “bad password”).
- Monitor for credential stuffing; integrate breach checks (k‑anonymity HIBP API).
- Centralize secrets: store pepper in KMS/HSM; log and alert on verification anomalies.

---

## Hashing for Non‑Password Secrets

- API keys: store HMAC(key, pepper) to allow lookup without storing plaintext. Example:
  - id: random public prefix
  - verifier: HMAC‑SHA‑256(key, pepper) → store base64(verifier)
  - Verification: recompute HMAC with pepper and compare in constant time.
- Webhooks: verify request body with HMAC using a per‑integration secret; reject on mismatch.
- Files/artifacts: compute SHA‑256 checksums and sign manifests; prefer digital signatures (Sigstore) for supply chain integrity.

---

## Common Pitfalls

- Using raw SHA‑256 for passwords (fast to crack).
- Reusing salts, or using static/global salts only.
- Low cost factors “for performance” instead of deploying async queues or rate limiting.
- Storing peppers in the same DB as hashes.
- Downgrading parameters silently; always rehash forward, never backward.

---

## Checklist

- [ ] MD5/SHA‑1 eliminated; SHA‑256/512 or SHA3 used for checksums.
- [ ] Passwords hashed with Argon2id (preferred) or bcrypt/scrypt/PBKDF2 (tuned).
- [ ] Per‑user salts stored; site‑wide pepper protected in KMS/HSM.
- [ ] Algorithm and parameters stored to enable rehashing.
- [ ] Login flow rehashes transparently when parameters improve.
- [ ] Rate limits and monitoring in place; breach checks integrated.
- [ ] API keys and webhooks authenticated with HMAC; no plaintext secrets stored.
