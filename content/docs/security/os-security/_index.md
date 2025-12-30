---
title: "Operating System Security"
weight: 6
bookCollapseSection: true
draft: false
---

# Operating System Security

OS security provides the foundation for all higher layers. Hardening userspace and kernel, enforcing least privilege, protecting credentials, and isolating workloads reduce the blast radius of compromises and make detection and recovery tractable.

This section focuses on pragmatic hardening for Linux and Windows, SSH security, and container/Kubernetes primitives that align with zero trust and least privilege.

---

## Key Themes

- Principle of least privilege
  - Minimize who can do what and where (users, groups, sudo, capabilities, SELinux/AppArmor).
- Trusted boot and patching
  - Secure/Measured boot (UEFI/TPM), timely kernel and OS updates, live patching where feasible.
- Credential hygiene
  - Strong password/Kerberos policies, key-based SSH, PAM settings, LSASS/DPAPI protection on Windows.
- System isolation
  - Namespaces/cgroups/LSMs on Linux; AppLocker/WDAC and sandboxing on Windows.
- Logging and auditing
  - OSQuery/Auditd/Sysmon for telemetry; forward to SIEM with integrity protection.

---

## Linux Hardening

- Accounts and sudo
  - Disable direct root SSH; use sudo with logged justifications; require MFA for privilege elevation where supported.
- Filesystem
  - Noexec/nosuid/nodev on temp and containers’ writable mounts; separate partitions for /var, /tmp.
- Kernel and LSMs
  - Enable SELinux (enforcing) or AppArmor; define profiles for exposed services.
  - Sysctl hardening (source routing off, ICMP/ARP sanity, nftables/iptables default deny).
- Services
  - Disable unused services; use systemd sandboxing (PrivateTmp, ProtectSystem, ProtectHome, CapabilityBoundingSet).
- Secrets
  - Do not store secrets on disk where possible; use tmpfs for transient; restrict perms; use KMS/vault-backed agents.

See: [SELinux & AppArmor](selinux-apparmor.md) and [SSH Hardening](ssh-hardening.md)

---

## Windows Hardening

- Baselines
  - Apply CIS/Microsoft Security Baselines; enforce BitLocker with TPM+PIN; Secure Boot enabled.
- Credential protections
  - Windows Defender Credential Guard; LSASS protection; disable WDigest; restrict NTLM; use Kerberos where possible.
- Application control
  - WDAC/AppLocker allow-listing; disable unsigned PowerShell; controlled folder access.
- Updates and Defender
  - Continuous patching; Microsoft Defender AV/EDR with tamper protection; ASR rules for macro/script blocking.
- RDP and remote admin
  - Network Level Authentication; restrict to jump hosts; enforce MFA; audit and alert on remote logons.

See: [Windows Security](windows-security.md)

---

## SSH and Remote Access

- Key-only authentication; disable passwords; disable root login; restrict with AllowUsers/AllowGroups and Match blocks.
- Strong key types (Ed25519, ECDSA P-256) and modern KEX/MACs; rotate authorized_keys and enforce command restrictions for automation users.
- Use bastion hosts or access brokers with session recording; short-lived certs (OpenSSH CA) instead of long-lived keys.

See: [SSH Hardening](ssh-hardening.md)

---

## Containers and Kubernetes

- Container isolation
  - Minimal base images, drop Linux capabilities, run as non-root, read-only root FS; user namespaces where supported.
- Supply chain
  - SBOMs and signed images (cosign); verify on admission; provenance (SLSA).
- Kubernetes
  - RBAC least privilege, Namespaces, NetworkPolicies, Pod Security Admission (baseline/restricted), seccomp/apparmor.
  - Secrets via external stores; avoid hostPath; restrict privileged pods; audit logs to SIEM.

See: [Container Security](container-security.md)

---

## Monitoring and Audit

- Linux: auditd rules for auth, sudo, file integrity; OSQuery for inventory and queries; forward via Fluent Bit/rsyslog.
- Windows: Sysmon with tuned config, Security logs, PowerShell transcription and logging.
- Golden images and drift detection with immutable infrastructure and compliance scanners (CIS, Lynis, Defender for Cloud).

---

## Where to Go Next

- [SSH Hardening](ssh-hardening.md)
- [Container Security](container-security.md)
- [SELinux & AppArmor](selinux-apparmor.md)
- [Windows Security](windows-security.md)
- [User Privileges & Access Control](user-privileges.md)
- [Secure Boot & Kernel Security](secure-boot-kernel.md)
