---
weight: 1
bookFlatSection: true
title: "Anti-Patterns"
draft: false
---

# Anti-Patterns

{{< markmap >}}

```markmap
# Architectural Anti-Patterns
- **Big Ball of Mud**
  - ❌ No clear structure, difficult to maintain.
  - ✅ Refactor into modular components, enforce coding standards, and use domain-driven design (DDD).
- **Lava Flow**
  - ❌ Dead code remains, increasing technical debt.
  - ✅ Regularly audit and remove unused code with proper version control.
- **Stovepipe System (Silos)**
  - ❌ Isolated components hinder integration and reuse.
  - ✅ Use standardized APIs and microservices for interoperability.
- **God Object / God Class**
  - ❌ A single class/module handles too many responsibilities.
  - ✅ Follow the Single Responsibility Principle (SRP) and break into smaller classes.
- **Golden Hammer**
  - ❌ Overusing a single technology for all problems.
  - ✅ Choose the best tool for each use case instead of a default solution.
- **Spaghetti Code**
  - ❌ Unstructured, tangled code makes debugging difficult.
  - ✅ Use modular design, proper naming conventions, and refactor regularly.
- **Reinventing the Wheel**
  - ❌ Building custom solutions for problems with existing tools/libraries.
  - ✅ Research and adopt well-established solutions before in-house development.
- **Distributed Monolith**
  - ❌ Microservices are tightly coupled, behaving like a monolith.
  - ✅ Enforce service independence, implement event-driven architectures, and decouple dependencies.
- **Anemic Domain Model**
  - ❌ Domain models act as data containers without business logic.
  - ✅ Embed business logic into domain models following Domain-Driven Design (DDD).
- **Vendor Lock-In**
  - ❌ Heavy reliance on a single cloud provider or proprietary technology.
  - ✅ Use multi-cloud strategies, open standards, and portable architectures.
- **Traffic Jam (Synchronous Bottleneck)**
  - ❌ Too many synchronous calls slow performance.
  - ✅ Implement asynchronous processing, caching, and message queues.
- **Hardcoded Configuration**
  - ❌ Environment-specific values are embedded in the code.
  - ✅ Use environment variables, configuration files, or secrets management tools.
- **Over-Engineering**
  - ❌ Unnecessary complexity makes the system harder to maintain.
  - ✅ Follow KISS (Keep It Simple, Stupid) and YAGNI (You Ain’t Gonna Need It) principles.
- **Sequential Processing (Lack of Parallelism)**
  - ❌ Tasks are executed sequentially, reducing efficiency.
  - ✅ Use parallel processing, multi-threading, or async workflows.
- **Accidental Complexity**
  - ❌ The system is overcomplicated due to poor design choices.
  - ✅ Focus on simplicity, eliminate unnecessary abstractions, and prioritize developer experience.
```

{{< /markmap >}}
