---
title: "Threat Hunting & Indicators of Compromise (IOCs)"
draft: false
---

# Threat Hunting & Indicators of Compromise (IOCs)

Threat hunting is a hypothesis-driven search for suspicious activity that evaded controls. IOCs provide concrete signals (hashes, domains, IPs, JA3, filenames) while TTP hunting focuses on behaviors (MITRE ATT&CK). Mature programs blend both: ingest curated intel, transform to detections, and continuously run hypotheses based on your environment.

Cross-refs
- TI/IR index: [/docs/security/threat-intel-ir/_index.md](/docs/security/threat-intel-ir/_index.md)
- SIEM: [/docs/security/threat-intel-ir/siem.md](/docs/security/threat-intel-ir/siem.md)
- Forensics & Log Analysis: [/docs/security/threat-intel-ir/forensics-log-analysis.md](/docs/security/threat-intel-ir/forensics-log-analysis.md)
- CSPM/CNAPP: [/docs/security/cloud-security/cspm.md](/docs/security/cloud-security/cspm.md)
- Kubernetes Security: [/docs/security/os-security/kubernetes-security.md](/docs/security/os-security/kubernetes-security.md)

---

## Operating Model

- Intel intake
  - Sources: ISACs/ISOs, vendor feeds, MISP, OpenCTI, Abuse.ch, internal incident learnings.
  - Curation: de-duplicate, score by relevance (industry, stack), expire stale items, attach TLP markings.
- Normalization and distribution
  - Normalize to STIX 2.1/TAXII or internal schema; distribute to SIEM, NIDS/IPS, EDR, DNS sinkholes, mail gateways.
- Validation
  - Test hits on historical data (backfill), evaluate FP rate, and retire overly generic indicators.
- Hypothesis-driven hunts
  - Derive from top ATT&CK techniques for your environment, recent vulnerabilities, control gaps, and outward signals (Shodan, ASN exposure).

---

## IOCs: Types and Handling

Types
- Network: IPs, domains, URLs, JA3/JA3S, TLS cert fingerprints.
- File/Process: hashes (SHA-256), filenames, paths, packers, code-signing certs.
- Email: sender domains, SPF/DKIM anomalies, attachment hashes.
- Cloud/Identity: suspicious user agents, unusual geos, OAuth app IDs, rogue public endpoints.

Best practices
- Track TTL/expiry; remove stale indicators to reduce noise.
- Tag indicators with context (campaign, TTP, confidence, first_seen/last_seen).
- Prefer high-signal indicators (exact hash) over weak (top-level domain) unless paired with behavior.

MISP JSON (concept)
```json
{
  "Event": {
    "info": "Phishing kit domains - TLP:AMBER",
    "Attribute": [
      {"type": "domain", "value": "update-login.example.net", "to_ids": true, "comment": "Confidence: 85"},
      {"type": "sha256", "value": "06a9...d3e7", "to_ids": true, "comment": "Loader binary"}
    ],
    "Tag": [{"name": "tlp:amber"}, {"name": "attck:T1566"}]
  }
}
```

---

## Detection Content Examples

Sigma (generic to SIEM)
```yaml
title: Suspicious PowerShell DownloadString
id: 0b7f6e8c-2f3b-4b1f-a5f6-1d6d9c2b1a01
status: experimental
logsource:
  product: windows
  service: powershell
detection:
  sel:
    EventID: 4104
    ScriptBlockText|contains:
      - "Invoke-Expression"
      - "DownloadString("
condition: sel
level: high
tags: [attack.t1059.001]
```

Splunk (example: AWS key misuse geo-anomaly)
```spl
index=cloudtrail eventName=AssumeRole OR eventName=ConsoleLogin
| stats dc(src_ip) AS ips values(src_ip) AS ip_list by userIdentity.arn
| where ips > 3
| lookup geo_by_ip ip AS ip_list OUTPUT country
| search country!="expected-country"
```

Microsoft 365 Defender KQL (rare consent grants)
```kql
CloudAppEvents
| where ActionType == "Consent to application"
| summarize count() by Application, AccountUpn, bin(Timestamp, 1d)
| where count_ < 3
```

Suricata (JA3-based C2 concept)
```text
alert tls any any -> any any (msg:"Suspicious JA3"; ja3_hash; content:"abcf23..."; sid:900001; rev:1;)
```

YARA (malicious loader traits)
```yara
rule packed_loader
{
  meta: description = "UPX packed loader with URL pattern"
  strings:
    $upx = "UPX!" nocase
    $url = /https?:\/\/[a-z0-9\-\.]+\/update\/[a-z0-9]{6}/ nocase
  condition:
    uint32(0) == 0x905a4d && $upx and $url
}
```

---

## Hunt Playbooks (Hypotheses)

1) Credential stuffing against login
- Hypothesis: repeated failed logins followed by success from same IP/range.
- Data: auth logs (HTTP 401/200), IdP logs.
- Procedure: group by IP→account, threshold anomalies, correlate with user-agent reputation.
- Action: block IP/range, require MFA reset, add WAF rule, update rate limit.

2) Public bucket exposure
- Hypothesis: newly public storage or object ACL change with anomalous reads.
- Data: CSPM events, storage access logs, IAM changes.
- Procedure: detect policy change, correlate with GET spikes and unusual geos.
- Action: auto-revert policy, quarantine data, initiate IR playbook.

3) Reverse shell in containers
- Hypothesis: bash spawning from a web process with outbound to rare ASN.
- Data: EDR/Falco events, egress flow logs.
- Procedure: search process tree anomalies, join with egress to non-allow-listed IPs.
- Action: isolate pod/node, capture memory/logs, redeploy from signed image.

---

## Prioritization and Scoring

- Relevance: tech stack coverage (Windows endpoints, EKS clusters, SaaS apps).
- Exploitability: exposed services, known vulnerabilities, weaponized CVEs.
- Impact: data sensitivity, privilege reach, blast radius.
- Confidence: intel source quality, rule precision, prior hit quality.

Score to a queue with owners, SLAs, and suppression expiry (time-boxed).

---

## Intel → Detection → Control

- Convert high-signal IOCs to blocking (NGFW/IP/domain, mail gateway, DNS sinkhole).
- Behavioral hunts become SIEM analytics with staged monitor → enforce.
- Feed back into controls: WAF rules, CSPM guardrails, IAM permission boundaries.

---

## Data Requirements

- Identity: IdP, SSO, cloud auth, token logs.
- Endpoint/Runtime: EDR, kernel sensors (eBPF), container runtime.
- Network: flow logs, DNS, proxy/NGFW, TLS metadata (SNI/JA3).
- Cloud/Control Plane: audit logs (CloudTrail/Admin Activity/Activity Logs), CSPM.
- App: API gateway, application logs with user/tenant correlation IDs.

Ensure time sync, retention (meets IR needs), and immutable/WORM storage for forensics.

---

## Tooling and Automation

- Intel: MISP/OpenCTI, TAXII clients, feed parsers; CTI pipelines to SIEM/EDR/NGFW.
- Hunt: notebooks (Jupyter + KQL/Splunk), hunt frameworks, ATT&CK Navigator for coverage.
- Content lifecycle: versioned rule repos, CI tests, staging gates, auto-changelog and coverage dashboards.

---

## Common Pitfalls

- Consuming all feeds → alert fatigue; no curation or expiry.
- Hash-only detections without path/behavior context → high FP or low coverage.
- One-off hunts not codified into detections; no ownership or SLAs.
- No backtesting; rules deployed directly to block inline.
- Missing data (no DNS/flow logs, short retention) undermines hunts.

---

## Checklist

- [ ] Curated, scored intel with TTL; normalized and distributed to controls.
- [ ] Hypothesis backlog aligned to ATT&CK and recent risks; owners assigned.
- [ ] Data coverage: identity, endpoint, network, cloud, app; time-synced and immutable retention.
- [ ] Content lifecycle: stage → measure → enforce; CI for detection rules.
- [ ] Feedback loop to WAF/NGFW/CSPM/IAM; incidents generate new hunts and rules.
- [ ] Metrics: hit quality (precision), time to triage/detect/contain, rule retirement rate.

---

## Cross-References

- TI/IR index: [/docs/security/threat-intel-ir/_index.md](/docs/security/threat-intel-ir/_index.md)
- SIEM: [/docs/security/threat-intel-ir/siem.md](/docs/security/threat-intel-ir/siem.md)
- Incident Response: [/docs/security/threat-intel-ir/incident-response-lifecycle.md](/docs/security/threat-intel-ir/incident-response-lifecycle.md)
- Forensics: [/docs/security/threat-intel-ir/forensics-log-analysis.md](/docs/security/threat-intel-ir/forensics-log-analysis.md)
