---
title: "Cross‑Site Request Forgery (CSRF)"
draft: false
---

# Cross‑Site Request Forgery (CSRF)

CSRF tricks a victim’s browser into submitting an authenticated request to your application without the user’s intent. If your app uses cookie‑based sessions, the browser will automatically attach those cookies to the forged request, allowing state changes (fund transfers, email changes, role updates) unless you enforce anti‑CSRF controls.

Modern SameSite cookies reduce risk but do not eliminate it—especially for legacy browsers, cross‑site embedding, federated flows, and misconfigured cookies.

---

## How CSRF Works

- Preconditions
  - The user is authenticated to your site with a cookie‑based session.
  - An attacker can cause the browser to make a request to your site (e.g., via <img>, <form>, fetch from an embedded page).
- Browser auto‑attaches cookies for your domain → your server sees an authenticated request → state changes occur if no CSRF protection.

Example attack (HTML form auto‑submit)
```html
<form action="https://bank.example.com/transfer" method="POST">
  <input type="hidden" name="to" value="attacker" />
  <input type="hidden" name="amount" value="1000" />
</form>
<script>document.forms[0].submit()</script>
```

---

## Primary Defenses

1) SameSite Cookies
- Set session cookies with SameSite=Lax (or Strict if UX allows), Secure, HttpOnly.
- Lax blocks most cross‑site POSTs, but allows top‑level GET navigations.
- Strict blocks all cross‑site sends, but can break legitimate flows (SSO, deep links).
- Always set Secure and HttpOnly; do not rely on defaults.

Set‑Cookie example
```
Set-Cookie: session=...; Path=/; Secure; HttpOnly; SameSite=Lax
```

2) CSRF Tokens (Synchronizer or Double‑Submit)
- Embed an unpredictable, per‑session or per‑request token in the page and submit it via form body or custom header.
- The token must not be sent automatically by the browser (i.e., not in cookies).

Synchronizer token pattern (server issues token, stores server‑side)
- Server stores token in session; template renders token into form.
- Server verifies submitted token equals stored token.

Double‑submit cookie pattern
- Server sets a CSRF cookie with random value (separate from session cookie).
- Client reads cookie via JS and sends the value in a custom header or hidden form field.
- Server compares header/body value with cookie value. Requires Strict‑Mode CORS to protect the header path.

Express example (double‑submit via header)
```js
// Issue CSRF cookie
app.get("/form", (req, res) => {
  const token = crypto.randomBytes(32).toString("base64url");
  res.cookie("csrf", token, { httpOnly: false, sameSite: "Lax", secure: true });
  res.render("form", { csrf: token });
});

// Verify on state‑changing routes
function verifyCsrf(req, res, next) {
  const tokenCookie = req.cookies.csrf;
  const tokenHeader = req.header("x-csrf-token");
  if (!tokenCookie || tokenCookie !== tokenHeader) return res.status(403).send("CSRF");
  next();
}
app.post("/transfer", verifyCsrf, handler);
```

3) Same‑Origin Policy + Custom Request Headers
- For APIs: require a custom header (e.g., X-Requested-With, X-CSRF-Token) and block requests lacking it.
- Browsers won’t send custom headers cross‑site without CORS preflight; combine with strict CORS.

---

## Complementary Controls

- Strict CORS
  - Do not use wildcard origins with credentials. Explicit allow‑list origins; set credentials only when necessary.
  - Preflight: verify and restrict allowed headers/methods.

- Idempotency and Safe Methods
  - Ensure GET/HEAD are read‑only; no state changes. Use POST/PUT/PATCH/DELETE for mutations.
  - CSRF tokens should be validated for all mutating methods.

- Re‑authentication or Step‑up MFA
  - For high‑risk actions (password/email change, financial transfers), require password re‑entry or MFA.

- Framebusting and Clickjacking Protection
  - Prevent UI redressing that can facilitate CSRF or trick users: X‑Frame‑Options: DENY or frame‑ancestors CSP.

---

## Special Cases and Pitfalls

- JSON APIs with Cookies
  - If you accept cookie‑authenticated JSON API calls from browsers, you are CSRF‑exposed.
  - Require CSRF token in a header and enforce SameSite cookies and strict CORS.

- Stateless APIs with Bearer Tokens
  - If auth is via Authorization: Bearer in JavaScript (not cookies), CSRF is largely mitigated, but you inherit XSS risk. Prefer HttpOnly cookies to store sessions; for SPAs, consider BFF pattern.

- OAuth/OIDC Redirects and Cross‑Site Flows
  - Use state and nonce parameters. Validate them strictly to prevent CSRF/open redirect during login.

- Legacy/Embedded Browsers
  - Some clients ignore SameSite. Keep CSRF tokens for critical mutations regardless of SameSite.

---

## Verification

- Automated tests
  - Ensure all POST/PUT/PATCH/DELETE endpoints require a valid CSRF token or custom header.
  - Assert GET endpoints are read‑only.

- DAST
  - Attempt cross‑site POST from an attacker page; verify request is rejected.

- Config validation
  - Inspect Set‑Cookie attributes in responses; check SameSite, Secure, HttpOnly are set.
  - Validate CORS responses: no wildcard with credentials; allowed methods/headers minimal.

---

## Minimal Example (Express + Helmet + csurf)

```js
import express from "express";
import cookieParser from "cookie-parser";
import helmet from "helmet";
import csurf from "csurf";

const app = express();
app.use(helmet());
app.use(cookieParser());
app.use(express.urlencoded({ extended: false }));
app.use(express.json());

// Cookie-based CSRF tokens (samesite Lax)
app.use(csurf({ cookie: { sameSite: "lax", httpOnly: true, secure: true } }));

app.get("/form", (req, res) => {
  res.render("form", { csrfToken: req.csrfToken() });
});

app.post("/transfer", (req, res) => {
  // If token missing or invalid, csurf throws 403 before reaching here
  res.json({ ok: true });
});
```

---

## Checklist

- [ ] Session cookies set with Secure, HttpOnly, SameSite=Lax (or Strict where possible).
- [ ] All mutating endpoints validate CSRF token or custom header.
- [ ] CORS explicitly allow‑lists origins; no wildcard when credentials are used.
- [ ] GET/HEAD are read‑only; mutations use POST/PUT/PATCH/DELETE.
- [ ] OAuth/OIDC flows validate state and nonce.
- [ ] High‑risk actions require step‑up or re‑authentication.
- [ ] Clickjacking protection via frame‑ancestors or X‑Frame‑Options.

---

## Cross‑References

- AppSec overview: [/docs/security/application-security/](../_index.md)
- API Security (CORS, headers, idempotency): [/docs/security/application-security/api-security.md](../api-security.md)
- TLS/Transport: [/docs/security/cryptography/tls-ssl/](/docs/security/cryptography/tls-ssl/)
