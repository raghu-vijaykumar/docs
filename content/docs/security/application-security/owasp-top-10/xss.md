---
title: "Cross‑Site Scripting (XSS)"
draft: false
---

# Cross‑Site Scripting (XSS)

XSS occurs when untrusted data flows into a browser execution context (HTML, attribute, URL, JavaScript) and executes as script. The root cause is mixing code and data. Modern frameworks reduce risk but do not eliminate it—unsafe sinks, template overrides, and sanitizer bypasses still create exploitable paths.

This guide explains contexts, safe patterns, framework specifics, and verification.

---

## XSS Types

- Reflected: Payload is reflected immediately in the response (e.g., search result).
- Stored: Payload persists in storage (DB, CMS) and is rendered to other users.
- DOM‑based: Client‑side script reads untrusted data and injects it into the DOM without server involvement.

Attack surfaces
- HTML body, attributes, URLs (href/src), inline event handlers (onclick), JavaScript execution sinks (eval, setTimeout with string), dynamic innerHTML/outerHTML, document.write, dangerouslySetInnerHTML, v-html.

---

## Contextual Output Encoding

Different contexts require different encoders. Always encode on output, not input.

- HTML text context
  - Encode: &, <, >, ", '
- HTML attribute context
  - Encode above plus whitespace; quote attributes; avoid unquoted attrs.
- URL context (href/src)
  - Encode components; validate scheme; reject javascript: data: blob: unless allowed.
- JavaScript context
  - Avoid injecting into JS; if unavoidable, JSON.stringify values and place in data attributes for later parsing.

Example (server‑side template)
```ejs
<!-- Safe: HTML-escaped -->
<div><%- e(user.name) %></div>

<!-- Dangerous: raw HTML -->
<div><%= user.name %></div>
```

---

## Content Security Policy (CSP)

CSP reduces impact by constraining sources of script execution.

Baseline production policy (tighten per app)
```
Content-Security-Policy:
  default-src 'self';
  script-src 'self' 'nonce-{random}' 'strict-dynamic';
  object-src 'none';
  base-uri 'self';
  frame-ancestors 'none';
  connect-src 'self' https://apis.example.com;
  img-src 'self' data:;
  style-src 'self' 'nonce-{random}';
```

Guidance
- Use nonces or hashes; avoid 'unsafe-inline' and 'unsafe-eval'.
- For SPAs, use a strict-dynamic policy with nonces. Generate a new random nonce per response.
- Monitor violations via report-uri/report-to and tune.

---

## Safe Rendering Patterns

- Prefer frameworks with auto‑escaping (React, Angular, Django templates, Rails ERB).
- Avoid raw HTML rendering; if necessary, sanitize with a proven library (DOMPurify) and then set innerHTML.
- For markdown rendering, sanitize output and whitelist only safe tags/attributes.

DOMPurify example
```js
import DOMPurify from "dompurify";
const safe = DOMPurify.sanitize(untrustedHtml, { ALLOWED_TAGS: ["b","i","a","code","pre"], ALLOWED_ATTR: ["href"] });
container.innerHTML = safe;
```

URL safety
```js
function safeHref(input) {
  try {
    const u = new URL(input, window.location.origin);
    if (!["http:", "https:"].includes(u.protocol)) return null;
    return u.toString();
  } catch { return null; }
}
const href = safeHref(untrustedUrl);
if (href) link.setAttribute("href", href);
```

---

## Framework‑Specific Notes

React
- Auto‑escapes by default. Avoid dangerouslySetInnerHTML. If necessary, sanitize first.
- Beware attribute injection via spread props: <img {...userInput}> is dangerous.

Angular
- Auto‑escapes in templates. Bypasses (DomSanitizer.bypassSecurityTrust…) should be rare and heavily reviewed.
- Avoid [innerHTML] binding to untrusted HTML; sanitize strictly.

Vue
- Auto‑escapes in mustaches {{ }}. v-html is dangerous; sanitize if absolutely needed.

Server‑side rendering (SSR)
- JSON embedding: escape properly or use safe JSON serialization.
```html
<script>window.__DATA__ = JSON.parse(document.getElementById("boot").textContent)</script>
```
```html
<script id="boot" type="application/json">
  {"user": {"name": "Alice"}} <!-- set with server-side JSON serializer -->
</script>
```

---

## Dangerous Sinks and APIs

Avoid or constrain:
- element.innerHTML / outerHTML / insertAdjacentHTML (use textContent or DOM creation).
- document.write, new Function, eval, setTimeout/setInterval with string.
- Inline event handlers (onclick=), javascript: URLs.
- Template engines that allow raw interpolation by default.

Safer alternatives
```js
// Instead of innerHTML
const span = document.createElement("span");
span.textContent = userInput;
container.appendChild(span);
```

---

## Sanitization

- Use well‑maintained libraries (DOMPurify) in “strict” mode where feasible.
- Whitelist tags/attributes minimally; deny style/script/event attributes entirely.
- Re‑sanitize on every render; do not store “sanitized” HTML as canonical data unless necessary and traceable.

---

## Testing and Verification

- Unit tests on renderers: ensure textContent is used, not innerHTML.
- Security tests: inject payloads like <img src=x onerror=alert(1)> and confirm blocked.
- DAST scanners; browser CSP violation reports (Report-To).
- Linters: ESLint rules to disallow eval, new Function, setTimeout string, and innerHTML use without sanitizer.

---

## Example: Express + EJS

Vulnerable
```ejs
<p>Welcome, <%= user.name %></p> <!-- outputs raw -->
```

Safe
```ejs
<p>Welcome, <%- e(user.name) %></p> <!-- escaped -->
```

CSP integration (Express)
```js
import crypto from "crypto";
app.use((req, res, next) => {
  res.locals.nonce = crypto.randomBytes(16).toString("base64");
  res.setHeader("Content-Security-Policy",
    `default-src 'self'; script-src 'self' 'nonce-${res.locals.nonce}' 'strict-dynamic'; object-src 'none'; base-uri 'self'`);
  next();
});
```
Template:
```ejs
<script nonce="<%= nonce %>">/* inline boot script */</script>
```

---

## Incident Response

- Invalidate sessions/tokens that may be exfiltrated via XSS.
- Rotate credentials stored in browser (API keys in localStorage are a design smell; prefer cookies HttpOnly).
- Patch templates/sinks; deploy CSP; add tests to prevent regressions.
- Audit telemetry for suspicious requests and beaconing.

---

## Checklist

- [ ] Use auto‑escaping frameworks; no raw HTML without sanitization.
- [ ] Encode output per context (HTML, attribute, URL, JS).
- [ ] Disallow dangerous sinks (innerHTML, eval); or sanitize and lint exceptions.
- [ ] Enforce strong CSP with nonces/hashes; no 'unsafe-inline' or 'unsafe-eval'.
- [ ] Validate and normalize URLs; reject javascript:/data:/blob: where not required.
- [ ] No inline event handlers; use addEventListener.
- [ ] Lint rules to block unsafe DOM APIs and string‑based timers/eval.
- [ ] Tests and DAST for XSS payloads; CSP reports monitored.

---

## Cross‑References

- AppSec overview: [/docs/security/application-security/](../_index.md)
- API Security: [/docs/security/application-security/api-security.md](../api-security.md)
- TLS/Transport: [/docs/security/cryptography/tls-ssl/](/docs/security/cryptography/tls-ssl/)
