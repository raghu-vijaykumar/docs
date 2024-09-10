+++
title= "System Design Interview"
tags = [ "system-design", "software-architecture", "interview" ]
author = "Me"
showToc = true
TocOpen = false
draft = false
hidemeta = false
comments = false
disableShare = false
disableHLJS = false
hideSummary = false
searchHidden = true
ShowReadingTime = true
ShowBreadCrumbs = true
ShowPostNavLinks = true
ShowWordCount = true
ShowRssButtonInSectionTermList = true
UseHugoToc = true
weight= 1
bookCollapseSection= true
+++

# The 5-Step System Design Process

## 1. Gather Functional Requirements
**Objective**: Understand what the system is supposed to do. This includes identifying the key functionalities the system must support and narrowing down the scope.

### Key Tasks:
- Ask clarifying questions to remove ambiguities.
- Define what parts of the system need to be designed and what parts are given (e.g., external APIs or services).

**Importance**: A solid understanding of functional requirements ensures that you're solving the right problem. Mistakes here lead to wasted effort and incorrect designs.

---

## 2. Capture Non-Functional Requirements
**Objective**: Define the qualities the system must exhibit, including performance, scalability, and high availability.

### Key Tasks:
- Identify workload requirements.
- Focus primarily on scalability, availability, and performance.

**Importance**: Non-functional requirements shape the architecture. Missing or misunderstanding these can lead to system failures under load or other unintended consequences.

---

## 3. Define the System's API & Sequence of Events
**Objective**: Create an API that outlines how users and other systems will interact with the system.

### Key Tasks:
- Use sequence diagrams to map out how various components will interact.
- Ensure that all use cases are covered, so no critical behavior is missed.

**Outcome**: A clear understanding of the external interactions with the system, which forms the foundation for the architecture.

---

## 4. Create the High-Level Architecture
**Objective**: Design a high-level architecture that meets the functional requirements.

### Key Tasks:
- Define the architectural style (e.g., microservices, monolithic, event-driven).
- Plan how requests will flow through the system and how data will be stored.

**Importance**: This step focuses on the core functionality and high-level structure of the system.

---

## 5. Optimize for Non-Functional Requirements
**Objective**: Refine the architecture to meet non-functional requirements such as performance, scalability, and availability.

### Key Tasks:
- Identify and eliminate single points of failure.
- Address bottlenecks.
- Optimize critical sections using techniques like data sharding or caching.

**Outcome**: A system design that not only works functionally but also performs well under load and meets reliability expectations.
