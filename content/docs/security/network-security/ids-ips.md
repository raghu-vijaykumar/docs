---
title: "Intrusion Detection & Prevention Systems (IDS/IPS)"
draft: false
---

# Intrusion Detection & Prevention Systems (IDS/IPS)

IDS/IPS detect or block malicious or policy‑violating activity. Network IDS (NIDS) inspects traffic on a segment; Host IDS (HIDS/HIPS) inspects telemetry and behavior on the host. Effective use requires careful placement, tuned rules, high‑quality context, and a workflow that turns detections into durable controls, not noise.

Cross‑refs:
- Firewalls: [/docs/security/network-security/firewalls.md](/docs/security/network-security/firewalls.md)
- DDoS Mitigation: [/docs/security/network-security/ddos-mitigation.md](/docs/security/network-security/ddos-mitigation.md)
- Segmentation & Zero Trust: [/docs/security/network-security/segmentation-zero-trust.md](/docs/security/network-security/segmentation-zero-trust.md)
- Kubernetes Security: [/docs/security/os-security/kubernetes-security.md](/docs/security/os-security/kubernetes-security.md)
- SSDLC: [/docs/security/application-security/ssdlc.md](/docs/security/application-security/ssdlc.md)

---

## Definitions and Modes

- IDS (detection)
  - Monitors traffic or host activity and raises alerts; out‑of‑band (SPAN/TAP) or host agents.
- IPS (prevention)
  - Inline to the traffic path; can block/drop/terminate sessions per rules; higher operational risk.
- NIDS vs HIDS
  - NIDS inspects packets/flows; HIDS inspects logs, files, processes, registry, kernel events. HIPS adds prevention on host.

Detection approaches
- Signature‑based: match known patterns (Snort/Suricata rules, YARA). Low false positives for known threats; blind to novel ones.
- Anomaly/behavior‑based: deviations from baselines, ML heuristics. Finds novel attacks; needs tuning; can be noisy.
- Policy‑based: explicit allow‑lists/deny policies (e.g., only this service should talk to DB).

---

## Placement and Architecture

NIDS
- SPAN/TAP on critical segments (north‑south edges, DMZ, inter‑segment links).
- Virtual taps in cloud (VPC traffic mirroring, GCP Packet Mirroring, Azure vTAP) to feed sensors.
- Scale horizontally; ensure capture fidelity (no drops) and time synchronization.

IPS
- Inline behind load balancers or as gateway devices; consider fail‑open vs fail‑closed behavior.
- Use for tightly scoped high‑value paths (e.g., admin portals) to limit blast radius of a bad block.

HIDS/HIPS
- Agents on servers/containers (or eBPF sensors) to inspect processes, file integrity, user activity, and syscalls.
- Container/K8s: Falco/Tetragon style rules to detect runtime anomalies.

Cloud‑native signals
- AWS GuardDuty/Network Firewall, Azure Defender for Cloud, GCP Security Command Center: managed detections at control/data planes.

---

## Rules, Content, and Tuning

Rulesets
- Community (ET Open), vendor, and custom rules. Keep updated; validate license and quality.
- Prioritize exploit and C2 rules over generic scans to reduce noise.

Context and suppression
- Add asset/owner/tenant tags; join with vulnerability data to prioritize exploited CVEs.
- Suppress or threshold noisy events (e.g., port scans) unless high‑signal for your environment.
- Use flow context (5‑tuple, TLS SNI, JA3/JA3S) and app metadata to reduce false positives.

Staging
- Deploy new rules in detect‑only first; review hit rate and false positives before blocking.
- Version rulesets; track change tickets and outcomes.

Example Suricata rule (concept)
```text
alert tls any any -> any any (msg:"Suspicious TLS JA3 fingerprint"; ja3_hash; content:"7696733f0bb9..."; sid:100001;)
```

---

## Inline Blocking (IPS) Considerations

- Latency and throughput: ensure devices handle peak QPS; avoid adding jitter on critical paths.
- Fail‑open vs fail‑closed: fail‑open improves availability but may allow attacks; use HA pairs and health checks.
- TLS inspection: requires decryption; raises privacy and key management concerns. Prefer targeted inspection zones.

Safer blocking strategies
- Block known bad indicators (threat intel, command‑and‑control).
- Block protocol violations and obvious exploit signatures with low FP rate.
- Quarantine flows for deeper inspection; use progressive enforcement.

---

## Host‑Based Detection

- File Integrity Monitoring (FIM): watch critical paths (binaries, config) for unexpected changes.
- Process/network anomalies: reverse shells, crypto miners, unexpected outbound to rare ASNs.
- Linux: auditd/eBPF sources; Windows: Sysmon + Defender signals.
- Container/K8s: detect privilege escalations (hostPath, CAP_SYS_ADMIN), secrets access anomalies, and unapproved registries.

Example Falco rule (concept)
```yaml
- rule: Write below binary dir
  desc: "Detect writes to system binary directories"
  condition: evt.type in (open,openat,creat) and fd.directory in (/bin,/usr/bin) and evt.arg.flags contains O_WRONLY
  output: "File below binary dir written (user=%user.name process=%proc.name file=%fd.name)"
  priority: CRITICAL
```

---

## Detection Engineering Workflow

1) Intake
   - Curate rules and intel; add environment context (assets/owners/tags).
2) Simulate
   - Replay pcap/logs in a lab; evaluate precision/recall; document assumptions.
3) Stage
   - Detect‑only rollout; measure hit rate, FP, and operational impact.
4) Enforce
   - For high‑confidence detections, enable block or auto‑containment.
5) Automate
   - Ticketing with owners; auto‑enrich with asset and vulnerability data; link runbooks.
6) Review
   - Post‑incident tuning; retire noisy or redundant rules; keep a changelog.

KPIs
- Precision/false positive rate; time to triage (MTTT); time to detect (MTTD); time to contain (MTTC).
- Coverage against top attack TTPs relevant to your environment.

---

## Data, Telemetry, and SIEM

- Collect: alerts, flow logs, packet captures (rotating), host telemetry (FIM, process, registry).
- Enrich: asset inventory, vuln data, identity context (user/role), geo/IP reputation.
- Route: send to SIEM with fields normalized (ECS/OCSF); retain raw for forensics with WORM.

Cross‑refs:
- Incident Response: planned section at [/docs/security/threat-intel-ir/](/docs/security/threat-intel-ir/)
- Logging and Evidence (Cloud): [/docs/security/cloud-security/cloud-best-practices.md](/docs/security/cloud-security/cloud-best-practices.md)

---

## Testing and Validation

- Red team/emulation (Atomic Red Team, Caldera) to validate detection coverage for ATT&CK TTPs.
- Traffic generation (tcpreplay, custom pcaps) to confirm rule behavior and performance.
- Unit tests for custom rules (where supported) and CI gating for rule changes.

---

## Common Pitfalls

- Turning on every rule → alert fatigue; no ownership or workflow to resolution.
- Inline IPS everywhere without staged testing → outages and rollbacks.
- TLS inspection enabled broadly without governance → privacy, legal, and performance issues.
- Ignoring egress/C2 detections due to lack of network controls; missing segmentation.
- Sensors underprovisioned → dropped packets, missed detections.

---

## Checklist

Architecture
- [ ] Sensors placed at key north‑south and inter‑segment links; host sensors on critical systems.
- [ ] Capacity sized for peak; packet drops monitored; time sync (NTP) across sensors.

Rules and Tuning
- [ ] Curated ruleset; staged deployment; FP thresholds; context enrichment.
- [ ] Known bad C2/IAb rules blocking; exploit signatures monitored/blocked where safe.
- [ ] TLS inspection limited to governed zones with auditable trust.

Workflow and Evidence
- [ ] Alerts → tickets with owners; linked runbooks; suppression hygiene (time‑boxed).
- [ ] Alerts and flows to SIEM; raw pcaps/telemetry retained with WORM for forensics.
- [ ] Post‑incident reviews drive rule updates; KPIs tracked (precision/MTTD/MTTC).

---

## Cross‑References

- Firewalls: [/docs/security/network-security/firewalls.md](/docs/security/network-security/firewalls.md)
- DDoS Mitigation: [/docs/security/network-security/ddos-mitigation.md](/docs/security/network-security/ddos-mitigation.md)
- Segmentation & Zero Trust: [/docs/security/network-security/segmentation-zero-trust.md](/docs/security/network-security/segmentation-zero-trust.md)
- Kubernetes Security (runtime sensors): [/docs/security/os-security/kubernetes-security.md](/docs/security/os-security/kubernetes-security.md)
