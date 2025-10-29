---
title: "Scoping: Defining What You're Actually Building"
---

# Scoping the ML Project: Setting the Foundation That Lasts

Imagine building a house without a blueprint or budget—you might get something inhabitable, but it'll likely cost twice as much as planned, take three times longer, and nobody will want to live in it. ML projects face the same fate when teams jump straight into data collection or model training without proper scoping. This phase is where you ask the hard questions that prevent spectacular failures six months down the road.

Think of scoping as your project's immune system: it identifies the fatal diseases early and establishes the antibodies to fight them. You need to understand not just *what* you're building, but *whether it's worth building* and *what success actually looks like* in your specific context.

## The Critical Questions: Cutting Through the Hype

Every promising ML idea starts with "Let's build something with AI!" But that enthusiasm quickly fades when reality sets in. The scoping phase forces you to answer three fundamental questions that separate viable projects from wishful thinking.

### What Problem Are You Actually Solving?

This seems obvious, but you'd be surprised how often teams confuse symptoms with causes. Consider these real-world examples:

- A team wanted to "improve customer satisfaction" with ML, but discovered the real bottleneck was poor UI design, not prediction accuracy.
- A manufacturing company sought "perfect defect detection" but learned their actual problem was inconsistent lighting and part positioning—much cheaper to fix mechanically than with ML.
- A content platform pursued "personalized recommendations" only to find their users already had clear search patterns they weren't leveraging.

Your scoping work reveals the true objective: what measurable outcome will justify the investment? Is it reducing manual work by 50%? Improving decision accuracy from 70% to 85%? Reducing costs by $2M annually? Without these anchors, you're sailing without a compass.

### What Data Do You Need, and Can You Get It?

ML systems are fundamentally data products—the quality of your data determines the ceiling of your performance. Scoping requires brutally honest assessments of:

- **Data Availability**: Do you have enough diverse examples? Are they labeled consistently? Can you collect more if needed?
- **Data Quality**: How noisy is your current data? What preprocessing will it need? Are there privacy or regulatory constraints?
- **Data Dynamics**: How frequently does your data change? Do you need real-time updates? What about edge cases?

I've seen teams spend months on elegant architectures only to discover their training data represented just 1% of their production scenarios. The hardest scoping decision is often admitting you need to collect entirely new data rather than using what you have.

### What Constraints Define Your Reality?

ML in research papers assumes unlimited compute, infinite time, and perfect data. Production systems live in a world of trade-offs. Your scoping must account for:

- **Compute Budget**: Can you afford GPUs? How fast must predictions be? What's your power consumption limit?
- **Operational Constraints**: Who will maintain this system? What monitoring do you need? How do you handle model failures?
- **Business Reality**: What's the acceptable timeline? What's your total budget? What's the opportunity cost of delay?

These constraints often define the project more than the technical requirements. A speech recognition system for call centers needs sub-300ms latency and 99.9% uptime, which might eliminate transformer architectures regardless of their research performance. A fraud detection model needs to balance precision/recall differently than a recommendation engine.

## Setting Realistic Success Metrics

Metrics drive behavior, and poorly chosen metrics drive your project off a cliff. Scoping requires understanding what success means in your specific domain:

- **Business Metrics**: Revenue impact, cost savings, user engagement improvements
- **Technical Metrics**: Precision, recall, F1-score, but weighted by their business value
- **Operational Metrics**: Latency, throughput, reliability, maintainability

For example, Google's early search ranking used pure relevance metrics. As they scaled, they added freshness, personalization, and user interface factors. Your initial metrics should include the business outcomes that matter, not just proxy technical measures.

## Resource Estimation: Avoiding the Budget Black Hole

One of the most neglected aspects of scoping is realistic resource estimation. Most teams underestimate costs by 3-5x and timelines by 2-4x. Proper scoping requires:

- **Data Acquisition**: How much labeling effort? External vendors? Quality control costs?
- **Compute Resources**: Development GPUs, training clusters, serving infrastructure
- **Team Expertise**: Do you need ML engineers? Domain specialists? DevOps?
- **Timeline Buffers**: Research suggests adding 50% buffer for unknowns

## The Trade-Off Matrix: Making Impossible Choices

Scoping reveals the hard truths of ML development. You can't optimize everything simultaneously:

- **Accuracy vs. Cost**: 99.9% accuracy might require 10x the compute budget
- **Speed vs. Precision**: Real-time systems often trade recall for lower latency
- **Scope vs. Feasibility**: Solving 80% of the problem in 20% of the time beats perfection in 5 years

## Common Scoping Traps and How to Avoid Them

### The Perfection Trap
Teams set "perfection" as the goal without considering diminishing returns. Every system hits a point where additional accuracy costs disproportionately more. Scoping helps you identify the "good enough" threshold.

### The Scope Creep Syndrome
New stakeholders bring requirements that expand the project by 300%. Scoping establishes boundaries and change control processes upfront.

### The Shiny New Tech Trap
Just because GPT-4 exists doesn't mean you need it. Many problems can be solved with simpler, more reliable approaches. Scoping asks: "What's the simplest solution that could work?"

## Knowing When Scoping Is Complete

Your scoping phase ends when you can answer these with confidence:

- What's our minimum viable success criteria?
- What's our maximum acceptable timeline and budget?
- Do we have a path to acquire the necessary data?
- What's our mitigation plan if the initial approach fails?

Once you have these answers documented, you're ready to move forward. The rest is just details. Poor scoping leaves teams iterating blindly; good scoping gives you a roadmap with guardrails.

## From Scoping to Execution

With clear boundaries established, you can confidently move to [data collection and preparation](data). The scoping decisions become your north star, preventing the chaos that derails so many ML projects.

Technology evolves rapidly, but the fundamentals of building reliable systems remain constant. Good scoping separates the ML engineers who ship from those who experiment.

## Related Topics

- [Data Collection](data) - Building on your scoping foundation
- [Deployment Challenges](../../deployment/challenges) - Avoiding production surprises
- [Speech Recognition Example](../../examples/speech-recognition/#scoping) - Real-world scoping in action
