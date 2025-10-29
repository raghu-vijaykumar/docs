---
title: "Multi‑Factor Authentication (MFA)"
draft: false
---

# Multi‑Factor Authentication (MFA)

MFA adds a second (or more) factor beyond passwords to raise the cost of account takeover. Modern deployments emphasize phishing‑resistant factors (WebAuthn/passkeys, FIDO2 security keys) and safe recovery paths. This guide focuses on practical, production‑ready MFA architecture and controls.

---

## Factor Types and Risk

- Knowledge: Passwords, PINs — weakest; susceptible to phishing and reuse.
- Possession: Security keys (FIDO2), authenticator apps (TOTP), push, SMS — stronger; varies by phishing resistance.
- Inherence: Biometrics — good UX; use as local device unlock, not remote factor by itself.

Phishing resistance (approx.)
- WebAuthn/Passkeys (FIDO2): Strong; origin‑bound, challenge‑response; resists real‑time phishing.
- TOTP: Moderate; codes can be phished; better than SMS.
- Push: Varies; vulnerable to push fatigue unless number‑matching/biometrics enforced.
- SMS/Voice: Weak; vulnerable to SIM swap, SS7 issues; only use as last resort.

---

## Deployment Strategy

- Baseline policy
  - Require MFA for all interactive users; phishing‑resistant factors for admins and high‑risk roles.
  - Block legacy auth (IMAP/POP/Basic) that bypass MFA.
- Factor enrollment
  - Prefer platform passkeys (device‑bound or synced with hardware protection) or FIDO2 security keys.
  - Allow TOTP as backup; avoid SMS except as temporary fallback.
  - Enforce at least two distinct factors enrolled per account.
- Step‑up MFA
  - Trigger additional MFA for sensitive actions (e.g., exporting PII, elevating privileges) based on risk/context.
- Session and re‑authentication
  - Short session lifetimes for admin consoles; re‑auth on privilege changes or long‑running sessions.
  - Bind sessions to device/browser; invalidate on risk signals.

---

## WebAuthn / Passkeys

Benefits
- Origin binding prevents credential forwarding.
- Private keys never leave device; signatures prove possession.
- Good UX with platform authenticators (Touch ID, Windows Hello).

Implementation notes
- Store per‑credential metadata (credentialId, publicKey, signCount).
- Use attestation selectively (privacy vs trust trade‑off); often “none” is acceptable.
- Support resident/passkey credentials where possible; handle multi‑device sync and recovery policy.

Example (pseudo‑flow)
1) Registration: server creates challenge → client creates credential (WebAuthn) → server verifies attestation, stores public key.
2) Authentication: server creates challenge → client signs with private key → server verifies signature, origin, and counters.

Libraries
- Node: @simplewebauthn/server & browser
- Go: github.com/duo-labs/webauthn
- Java: webauthn4j

---

## TOTP and Push

TOTP (RFC 6238)
- Use per‑user random 160‑bit secrets; display as QR codes; store encrypted at rest.
- Code length 6–8; 30s timestep; allow small clock drift (±1 window).
- Rate‑limit attempts; lockout with exponential backoff and alerts.

Push
- Enforce number matching or challenge‑text confirmation.
- Limit daily prompts; rate‑limit and alert on repeated denials (push fatigue).

---

## SMS/Voice: Fallback Only

- Use only as temporary recovery; never as the only factor for admins.
- Detect SIM change events via IdP risk signals when available.
- Encourage migration to passkeys/security keys.

---

## Account Recovery and Backup

- Backup codes: generate single‑use, high‑entropy codes; display once; store hashed (e.g., bcrypt/Argon2id) server‑side.
- Recovery factors: allow registering multiple authenticators (2+), including a hardware key stored securely.
- High‑risk recovery: require human verification/approvals for admin accounts; use step‑up checks and cooldowns.
- Event logging: log all enrollments, removals, and recovery actions with immutable audit.

---

## Conditional Access and Risk Signals

Signals to incorporate
- New device/browser, geo‑velocity (impossible travel), atypical time, TOR/VPN presence.
- Device posture (MDM/EDR), OS patch levels.
- Credential leaks (breach corpuses).

Actions
- Step‑up MFA; deny; require re‑enrollment; or limit to read‑only mode.

---

## Admin and Break‑Glass

- Admins must use phishing‑resistant factors (FIDO2).
- Maintain minimal break‑glass accounts with hardware keys stored offline; monitor and test quarterly.
- Break‑glass bypass requires dual control and short TTL; alert security immediately.

---

## UX and Adoption

- Encourage passkeys by default; present them first during enrollment.
- Educate users on recognizing malicious prompts and avoiding code sharing.
- Provide clear self‑service factor management with audit trails.

---

## Example Configuration Checklist

- [ ] MFA required for all interactive users; legacy auth disabled.
- [ ] Phishing‑resistant MFA required for admins and production access.
- [ ] Two distinct factors enrolled; TOTP preferred over SMS as backup.
- [ ] Step‑up MFA on sensitive actions and risk signals.
- [ ] Session binding to device/browser; re‑auth on privilege elevation.
- [ ] Recovery via backup codes and secondary authenticators; strict process for admins.
- [ ] Full audit of enrollments, prompts, denials, and recoveries.
- [ ] Regular reporting on MFA coverage and weak factor usage (SMS).

---

## References

- FIDO2/WebAuthn: https://fidoalliance.org/fido2/
- WebAuthn spec: https://www.w3.org/TR/webauthn-2/
- NIST SP 800‑63B (Digital Identity Guidelines)
