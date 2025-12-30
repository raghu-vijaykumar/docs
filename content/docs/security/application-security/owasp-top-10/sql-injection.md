---
title: "SQL Injection"
draft: false
---

# SQL Injection

SQL Injection (SQLi) occurs when untrusted input is concatenated into SQL queries, allowing attackers to alter queries, exfiltrate data, or execute destructive operations. Despite mature defenses, SQLi remains prevalent due to unsafe string building, poorly validated inputs, and edge cases in ORM/query building.

This guide provides clear mechanics, safe patterns, framework‑specific examples, and verification steps.

---

## How SQL Injection Works

When application code directly concatenates user input into a query, the database parses injected tokens as SQL, not data.

Vulnerable
```js
// Node + mysql: vulnerable
const user = req.query.user;            // attacker controls this
const sql = `SELECT * FROM users WHERE username = '${user}'`;
const rows = await db.query(sql);
```

Payload
```
' OR '1'='1
```
Resulting query
```sql
SELECT * FROM users WHERE username = '' OR '1'='1';
```

Consequences
- Auth bypass (“tautology”)
- Data extraction (UNION SELECT)
- Database modification (UPDATE/DELETE)
- Schema discovery (information_schema)
- RCE via DB extensions (xp_cmdshell, COPY FROM PROGRAM) in extreme cases

---

## Primary Defense: Parameterized Queries (Prepared Statements)

Parameters make user input “data” rather than query “code”.

Node (mysql2)
```js
const [rows] = await db.execute(
  "SELECT * FROM users WHERE username = ?",
  [req.query.user]
);
```

Java (Spring JDBC)
```java
String sql = "SELECT * FROM users WHERE username = ?";
List<User> users = jdbcTemplate.query(sql, new Object[]{username}, mapper);
```

Python (psycopg2)
```python
cur.execute("SELECT * FROM users WHERE username = %s", (username,))
```

Go (database/sql)
```go
row := db.QueryRowContext(ctx, "SELECT * FROM users WHERE username = $1", username)
```

Ruby (ActiveRecord)
```rb
User.where("username = ?", params[:username])
```

Do not interpolate values into the SQL string even if you “escape” them. Use parameters.

---

## ORM and Query Builder Pitfalls

Safe when used correctly
- Most ORMs produce parameterized SQL for filters and values.

Common mistakes
- Raw SQL helpers with string concatenation:
  - Sequelize: sequelize.query(`... ${userInput} ...`)
  - Prisma: prisma.$queryRawUnsafe(...)
  - ActiveRecord: where("id = " + params[:id])
- Dynamic ORDER BY/column names built from untrusted input.

Mitigations
- Use parameter binding helpers:
  - Prisma: prisma.$queryRaw`SELECT ... WHERE id = ${id}`
  - Sequelize: replacements / bind parameters
- For identifiers (table/column/order), map user input to an allow‑list:
  ```js
  const allow = { created: "created_at", name: "name" };
  const orderColumn = allow[req.query.sort] ?? "created_at";
  const orderDir = req.query.dir === "asc" ? "ASC" : "DESC";
  const sql = `SELECT * FROM items ORDER BY ${orderColumn} ${orderDir} LIMIT 50`;
  ```
  Only substitute from pre‑defined constants, never raw input.

---

## Stored Procedures and ORMs Are Not Magic Shields

- Stored procedures can still be vulnerable if they concatenate input internally.
- ORMs will not save you if you drop to raw queries or interpolate user data.
- Use parameters everywhere, including in procedures and raw helpers.

---

## Input Validation and Canonicalization

- Validate types and ranges before queries (e.g., integer IDs, UUIDs).
- Canonicalize encodings where relevant; avoid double‑decoding.
- Reject unexpected characters for constrained fields; prefer allow‑lists.

Example (Express + zod)
```js
import { z } from "zod";
const Q = z.object({ id: z.string().uuid() });
const { id } = Q.parse(req.query);
const row = await db.query("SELECT * FROM orders WHERE id = $1", [id]);
```

---

## Special Cases

LIKE queries and wildcards
```sql
WHERE title LIKE '%' || :q || '%'
```
- User input may contain % or _ wildcards; escape them or bind safely and pre‑escape:
  ```sql
  WHERE title LIKE :pattern ESCAPE '\'
  ```
  Build pattern server‑side: pattern = '%' + escape(q) + '%'

IN clauses
- Use array parameters if supported or expand server‑side carefully with fixed placeholders.

Identifiers (table/column names)
- Never take directly from users; map to allow‑listed constants.

---

## Platform Examples

PostgreSQL (server‑side)
```sql
-- Use parameters, not EXECUTE of concatenated strings
PREPARE get_user(text) AS
  SELECT * FROM users WHERE username = $1;
EXECUTE get_user('alice');
```

Django ORM
```python
# Safe: params bound
User.objects.filter(username=username)

# Dangerous: raw with f-strings
User.objects.raw(f"SELECT * FROM auth_user WHERE username = '{username}'")
```

Prisma
```ts
// Safe
const rows = await prisma.$queryRaw`SELECT * FROM users WHERE id = ${id}`;

// Dangerous
const rows = await prisma.$queryRawUnsafe(`SELECT * FROM users WHERE id = ${id}`);
```

---

## Detection and Testing

- Unit tests for query builders: assert no string interpolation with untrusted data.
- DAST with SQLi payloads on inputs; monitor for 500s and error signatures.
- WAF rules (e.g., AWS WAF, Cloud Armor) can reduce noise but are not primary defense.
- Database logging:
  - Log normalized/parameterized queries where possible, not raw with secrets.
  - Alert on suspicious patterns (e.g., UNION SELECT, information_schema access).

---

## Incident Response

- Rotate credentials for affected app and limit DB roles (least privilege).
- Enable point‑in‑time recovery and review data access logs.
- Patch code to parameterize; add tests; consider temporary WAF rules to reduce attack traffic.
- Conduct postmortem: identify missed reviews or lack of validation.

---

## Checklist

- [ ] All dynamic values bound as parameters; no string concatenation with user input.
- [ ] Raw SQL helpers use safe binding APIs; “unsafe” variants disabled or lint‑blocked.
- [ ] Identifier selection (columns/order) via allow‑lists only.
- [ ] Inputs validated and canonicalized (types, ranges, formats).
- [ ] ORMs used correctly; stored procedures avoid concatenation internally.
- [ ] Tests and linters detect interpolation; CI blocks unsafe patterns.
- [ ] DB user has least privilege (no unnecessary CREATE/DROP/ALTER).
- [ ] Monitoring/alerts for SQL errors and suspicious query patterns.

---

## Cross‑References

- AppSec overview: [/docs/security/application-security/](../_index.md)
- API Security (validation, idempotency): [/docs/security/application-security/api-security.md](../api-security.md)
- SSDLC (SAST/DAST, supply chain): [/docs/security/application-security/ssdlc.md](../ssdlc.md)
