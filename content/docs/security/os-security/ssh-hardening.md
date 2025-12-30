---
title: "SSH Hardening"
draft: false
---

# SSH Hardening

SSH is a critical administrative plane. Misconfiguration leads to credential theft, lateral movement, and persistent access. This guide provides production‑grade defaults for OpenSSH servers and clients, short‑lived certificates, access policy, and auditing.

---

## Objectives

- Eliminate password and root logins.
- Enforce strong key algorithms and modern KEX/MACs.
- Constrain access by user/group, source, and command.
- Prefer short‑lived user and host certificates via OpenSSH CA.
- Record, audit, and alert on suspicious activity.

---

## Server Hardening (sshd_config)

Start from deny‑by‑default and explicitly allow safe options.

Recommended baseline (OpenSSH 8.9+; adjust for your version)

```sshconfig
# Authentication
PasswordAuthentication no
KbdInteractiveAuthentication no
ChallengeResponseAuthentication no
PubkeyAuthentication yes
PermitRootLogin no

# Reduce attack surface
PermitEmptyPasswords no
AllowAgentForwarding no
AllowTcpForwarding no
X11Forwarding no
PermitTunnel no
GatewayPorts no

# Limit who can log in
AllowUsers admin@10.0.0.0/8 admin@192.168.0.0/16 devops
# or group-based:
# AllowGroups admin devops

# Modern ciphers/MACs/KEX (use server defaults when on current OpenSSH)
# As of modern OpenSSH, defaults are safe. If pinning, consider:
KexAlgorithms curve25519-sha256,curve25519-sha256@libssh.org
HostKeyAlgorithms ssh-ed25519,ecdsa-sha2-nistp256
Ciphers chacha20-poly1305@openssh.com,aes256-gcm@openssh.com,aes128-gcm@openssh.com
MACs hmac-sha2-512-etm@openssh.com,hmac-sha2-256-etm@openssh.com

# Host keys (ECDSA/Ed25519 preferred)
HostKey /etc/ssh/ssh_host_ed25519_key
HostKey /etc/ssh/ssh_host_ecdsa_key

# Login safeguards
LoginGraceTime 30
MaxAuthTries 3
MaxSessions 4
ClientAliveInterval 300
ClientAliveCountMax 2

# Logging
LogLevel VERBOSE
SyslogFacility AUTHPRIV

# Subsystem
Subsystem sftp internal-sftp

# Per-role restrictions
Match Group devops
  AllowTcpForwarding yes
  X11Forwarding no
  PermitTTY yes

# Example: automation user constrained to a single command
Match User deploy
  ForceCommand /usr/local/bin/deploy-entrypoint.sh
  AllowTcpForwarding no
  PermitTTY no
  PermitUserRC no
```

Operational notes
- Keep OpenSSH updated; defaults improve over time (don’t over‑pin unless required).
- If you need port forwarding for specific users, enable only in Match blocks.
- Consider moving SSH off 22 only for noise reduction; do not rely on obscurity.

---

## Access Control and Policy

- No shared accounts: personal identities only; use groups for permissions.
- Source restrictions: use AllowUsers user@CIDR or firewall rules (security groups, host firewall).
- Privilege separation: use sudo with reason logging; forbid direct root login.
- MFA: prefer SSH CA + SSO‑backed short‑lived certs; if using PAM‑MFA (e.g., Duo), test fail‑closed behavior carefully.

---

## Short‑Lived SSH Certificates (OpenSSH CA)

Prefer certs over static authorized_keys. A central CA signs short‑lived user/host keys.

Setup (high level)
1) Create CA keys on an offline/secured signer host:
   ```bash
   ssh-keygen -t ed25519 -f /etc/ssh/ssh_user_ca -C "ssh user ca"
   ssh-keygen -t ed25519 -f /etc/ssh/ssh_host_ca -C "ssh host ca"
   ```
2) Distribute public CA keys to servers:
   ```sshconfig
   # /etc/ssh/sshd_config
   TrustedUserCAKeys /etc/ssh/ssh_user_ca.pub
   HostCertificate /etc/ssh/ssh_host_ed25519_key-cert.pub
   # For host cert signing at build time:
   # ssh-keygen -s ssh_host_ca -I host-$(hostname) -h -n $(hostname),hostname.domain -V +52w /etc/ssh/ssh_host_ed25519_key.pub
   ```
3) Issue short‑lived user certs via broker/IdP workflow:
   ```bash
   ssh-keygen -s /etc/ssh/ssh_user_ca -I alice@prod -n alice -V +8h ~/.ssh/id_ed25519.pub -z 1
   # Optionally add principals, source address constraints, extensions.
   ```

Benefits
- Central revocation (stop issuing); tight TTLs (hours) drastically reduce key theft impact.
- Principals and extensions enforce restrictions (permit‑X11‑forwarding=no, force‑command).

---

## Authorized Keys Hygiene

If you use authorized_keys (not certs), enforce command and source limits:

~/.ssh/authorized_keys entry example
```text
from="10.0.0.0/8,192.168.1.10",command="/usr/local/bin/ro-shell.sh",no-port-forwarding,no-X11-forwarding,no-agent-forwarding ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAI... user@example
```

- Periodically reconcile authorized_keys with inventory; remove stale access.
- Disallow weak key types (ssh-rsa if using old OpenSSH; prefer ed25519/ecdsa).

---

## Client Hardening (ssh_config)

Standardize client behavior (dev laptops, jump hosts):

~/.ssh/config
```sshconfig
Host *
  PubkeyAuthentication yes
  IdentitiesOnly yes
  AddKeysToAgent yes
  ForwardX11 no
  ForwardAgent no
  ServerAliveInterval 60
  ServerAliveCountMax 2
  # Strict host key checking in CI/automation
  StrictHostKeyChecking ask
  UserKnownHostsFile ~/.ssh/known_hosts

# Use jump host (bastion)
Host *.prod.internal
  ProxyJump bastion.prod.internal

# Prefer modern algorithms (when pinning is necessary)
Host *.internal
  HostKeyAlgorithms ssh-ed25519,ecdsa-sha2-nistp256
  KexAlgorithms curve25519-sha256
  Ciphers chacha20-poly1305@openssh.com,aes256-gcm@openssh.com
```

Agent forwarding
- Keep disabled; if needed for specific hosts, enable per‑host and ensure bastions are secured.

---

## Bastions and Access Brokers

- Centralize ingress via bastion hosts or identity‑aware access proxies (e.g., Teleport, Boundary).
- Enforce session recording for administrative access; store logs immutably.
- Prefer short‑lived certs minted after SSO with device posture checks.

---

## SFTP and Chroot

Lock down file transfer users:
```sshconfig
Subsystem sftp internal-sftp

Match Group sftpusers
  ChrootDirectory /var/sftp/%u
  ForceCommand internal-sftp
  X11Forwarding no
  AllowTcpForwarding no
```
- Ensure chroot directories are owned by root and not writable by others.

---

## Monitoring, Detection, and Response

- LogLevel VERBOSE to capture key fingerprint on login; forward AUTHPRIV to SIEM.
- Detect brute force and anomalies: fail2ban or host IDS; alert on repeated denials and new geo/IPs.
- File integrity: monitor ~/.ssh and /etc/ssh for changes (auditd/OSQuery).
- Incident playbook: disable issuance (if using CA), revoke principals, rotate host keys, and block sources at firewall.

---

## Common Pitfalls

- Leaving password auth enabled; accepting keyboard‑interactive challenges.
- Allowing root login or broad AllowUsers (or none) on exposed servers.
- Agent forwarding to untrusted bastions; credential theft via agent socket.
- Over‑pinning algorithms that break new client/server defaults; causing outages on upgrades.
- Storing private keys in repos or shared drives; lack of device encryption on admin endpoints.

---

## Checklist

- [ ] Password login disabled; root login disabled; PubkeyAuthentication only.
- [ ] Access limited to specific users/groups and CIDRs; per‑role Match blocks applied.
- [ ] Modern algorithms in use (ed25519/P‑256; curve25519 KEX; AEAD ciphers).
- [ ] Short‑lived SSH certs via CA for humans and hosts; static keys phased out.
- [ ] Bastion/proxy in place with session recording for admin access.
- [ ] Authorized keys constrained (from=, command=, no‑forwarding) where used.
- [ ] Logs forwarded; alerting on brute‑force, new keys, unusual geos; file integrity on SSH paths.
- [ ] Regular review of sshd_config drift and key inventories; tested break‑glass.
