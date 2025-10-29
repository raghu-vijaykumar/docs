---
title: "Reinforcement Learning"
linktitle: "Reinforcement Learning"
description: "Sequential decision-making through trial-and-error learning, from Markov Decision Processes to deep reinforcement algorithms."
date: 2025-10-28
draft: false
tags: ["reinforcement learning", "MDP", "Q-Learning", "DQN", "sequential decision making"]
categories: ["Machine Learning"]
weight: 4
toc_enable: true
---

# Reinforcement Learning

Reinforcement learning trains agents to make optimal sequential decisions through interaction with environment. Unlike supervised learning requiring labeled datasets, RL agents learn from sparse reward signals by discovering which actions yield highest cumulative benefits. This paradigm enables autonomous behavior development in complex, uncertain environments.

## Core Framework

### Agent-Environment Interaction

RL operates within sequential decision-making loops:

```mermaid
graph LR
    A[Agent] --> B[Action a<sub>t</sub>]
    B --> C[Environment]
    C --> D[Reward r<sub>t</sub>]
    C --> E[New State s<sub>t+1</sub>]
    D --> A
    E --> A

    A --> F[Policy π]
    F -.->|Selects| B
    A --> G[Value Function<br/>V(s) or Q(s,a)]
    G -.->|Guides| F
    A --> H[Model<br/>P(s'|s,a)]
```

**Interaction Cycle:**
1. Agent observes environment state sₜ
2. Agent selects action aₜ using policy π
3. Environment provides reward rₜ and transitions to sₜ₊₁
4. Agent updates knowledge and policy based on experience
5. Repeat until termination or convergence

### Fundamental Elements

**State Space (S):** Environment configurations, fully or partially observable

**Action Space (A):** Available agent choices at each state

**Reward Function (R):** Scalar feedback signals guiding learning

**Policy (π):** Decision function mapping states to action distributions

**Value Functions:**
- **State Value V(s)**: Expected cumulative reward from state
- **Action Value Q(s,a)**: Expected cumulative reward from state-action pair
- **Advantage A(s,a)**: Relative value vs averaged action quality

**Discount Factor (γ ∈ [0,1]):**
- Near term preference (γ close to 0)
- Long term consideration (γ close to 1)
- Mathematical convergence guarantee

## Markov Decision Processes (MDPs)

MDPs formalize sequential decision-making under uncertainty:

### Definitional Framework

**Stochastic Environment:**
- **Markov Assumption**: Next state depends only on current state and action
- **State Transitions**: P(s'|s,a) probability distribution
- **Reward Dynamics**: Expected reward R(s,a,s') or immediate reward R(s,a)

**MDP Components:**
- **S**: Finite set of states
- **A**: Finite set of actions per state
- **P**: Transition function P(s'|s,a)
- **R**: Reward function R(s,a) or R(s,a,s')
- **γ**: Discount factor

### Planning vs Learning

**Planning Problems:**
- Environment dynamics P and R are known
- Find optimal policy through mathematical computation
- Approaches: Value iteration, policy iteration, linear programming

**Learning Problems:**
- Environment dynamics unknown or partially known
- Learn optimal behavior through experience
- Approaches: Temporal difference learning, Monte Carlo methods

### Value Functions

**Bellman Equations:**
- **State Values**: V(s) = E[Σᵗγᵗ rₜ₊₁|s`}
- **Action Values**: Q(s,a) = E[Σᵗγᵗ rₜ₊₁|s₀=s,a₀=a]
- **Recursive Relationship**: Q(s,a) = R(s,a) + γ Σₛ' P(s'|s,a) maxₐ' Q(s',a')

**Optimal Value Functions:**
- **V*(s)**: Highest achievable value from state s
- **Q*(s,a)**: Optimal action value function
- **Bellman Optimality**: Q*(s,a) = R(s,a) + γ maxₐ' Q*(s',a')

### Policy Evaluation

**Policy π Evaluation:**
1. Initialize V₀(s) = 0 for all s
2. For each state: Vₖ₊₁(s) = Σ_a π(a|s) [R(s,a) + γ Σₛ' P(s'|s,a) Vₖ(s')]
3. Repeat until convergence (Δ < ε)

**Policy Improvement:**
- For each state: Act according to greedy policy wrt current V
- π'(s) = argmax_a Σₛ' P(s'|s,a) [R(s,a) + γ V(s')]

## Q-Learning

Model-free reinforcement learning learning optimal action values directly through experience.

### Tabular Q-Learning

**Algorithm Core:**
1. Initialize Q(s,a) arbitrarily (typically zeros)
2. For each episode:
   - Observe initial state s
   - While not termination:
     - Select action a using ε-greedy policy (explore with probability ε)
     - Execute action, observe reward r and next state s'
     - Compute Q-update: Q(s,a) ← Q(s,a) + α[r + γ maxₐ' Q(s',a') - Q(s,a)]
     - Set s ← s'

```python
import numpy as np

class QLearning:
    def __init__(self, state_space, action_space, alpha=0.1, gamma=0.99, epsilon=0.1):
        self.Q = np.zeros((state_space, action_space))
        self.alpha = alpha  # Learning rate
        self.gamma = gamma  # Discount factor
        self.epsilon = epsilon  # Exploration probability
        self.state_space = state_space
        self.action_space = action_space

    def act(self, state):
        """ε-greedy action selection"""
        if np.random.random() < self.epsilon:
            return np.random.randint(self.action_space)  # Explore
        else:
            return np.argmax(self.Q[state])  # Exploit

    def learn(self, state, action, reward, next_state, done):
        """Q-value update"""
        current_q = self.Q[state, action]
        if done:
            target = reward
        else:
            target = reward + self.gamma * np.max(self.Q[next_state])

        self.Q[state, action] += self.alpha * (target - current_q)
```

**Convergence Properties:**
- **Gains Improvement Theorem**: Q-values monotonically increase until convergence
- **Stochastic Approximation**: Converges to Q* under appropriate learning rate schedules
- **In-sample Learning**: Learns quickly from observed trajectories

### Exploration Strategies

**ε-Greedy Exploration:**
- Simple but effective: ε probability random action, (1-ε) greedy selection
- Fixed ε: Constant exploration throughout
- Decaying ε: Reduce exploration as learning progresses

**Softmax Exploration:**
- Probabilistic action selection based on current Q-values
- τ (temperature) parameter controls randomness
- Higher τ: More exploration, lower τ: More exploitation

**Boltzmann Policy:**
- π(a|s) ∝ exp(Q(s,a)/τ)
- Smooth probability distribution over actions

### Temporal Difference vs Monte Carlo

**Exact Temporal Difference (TD(0)):**
- Bootstrapped: Adjust Q using next state estimate
- Q(s,a) ← r + γ max_a' Q(s',a')
- Converges faster with less data

**First Visit Monte Carlo:**
- Complete episode needed for value updates
- Q(s,a) ← Q(s,a) + α[G - Q(s,a)]
- G = Σᵏ γᵏ rₓ₊ₖ (undiscounted returns from starting point)
- Unbiased but higher variance

## Deep Q-Networks (DQN)

Deep neural networks approximate Q-functions for high-dimensional state spaces.

### Function Approximation

**Non-Linear Q-Approximation:**
- Deep networks capture complex state-value relationships
- Continuous state spaces become tractable
- Feature hierarchies automatically learned

```python
import torch
import torch.nn as nn

class DQN(nn.Module):
    def __init__(self, input_dim, output_dim):
        super(DQN, self).__init__()
        self.layers = nn.Sequential(
            nn.Linear(input_dim, 128),
            nn.ReLU(),
            nn.Linear(128, 128),
            nn.ReLU(),
            nn.Linear(128, 64),
            nn.ReLU(),
            nn.Linear(64, output_dim)
        )

    def forward(self, x):
        return self.layers(x)

    def act(self, state, epsilon=0.1):
        """ε-greedy action with exploration"""
        if np.random.random() < epsilon:
            return np.random.randint(self.layers[-1].out_features)
        else:
            with torch.no_grad():
                q_values = self.forward(torch.Tensor(state).unsqueeze(0))
                return torch.argmax(q_values).item()
```

### Experience Replay

**Replay Buffer:**
- Store transition tuples (s, a, r, s', done)
- Sample mini-batches for training stability
- Break temporal correlations between samples

```python
from collections import deque
import random

class ReplayBuffer:
    def __init__(self, capacity=10000):
        self.buffer = deque(maxlen=capacity)

    def push(self, transition):
        self.buffer.append(transition)

    def sample(self, batch_size):
        return random.sample(self.buffer, batch_size)

    def __len__(self):
        return len(self.buffer)

# Training loop with experience replay
def train_dqn(env, q_network, target_network, optimizer, buffer):
    batch = buffer.sample(BATCH_SIZE)
    states, actions, rewards, next_states, dones = zip(*batch)

    # Convert to tensors
    states = torch.stack(states) if isinstance(states[0], torch.Tensor) else torch.tensor(states, dtype=torch.float32)
    actions = torch.tensor(actions, dtype=torch.int64)
    rewards = torch.tensor(rewards, dtype=torch.float32)
    next_states = torch.stack(next_states) if isinstance(next_states[0], torch.Tensor) else torch.tensor(next_states, dtype=torch.float32)
    dones = torch.tensor(dones, dtype=torch.float32)

    # Q-learning update with target network
    q_values = q_network(states)
    next_q_values = target_network(next_states)
    target_q_values = q_network(states)

    q_value = q_values.gather(1, actions.unsqueeze(1)).squeeze(1)
    next_q_value = target_q_values.gather(1, next_q_values.max(1)[1].unsqueeze(1)).squeeze(1)
    expected_q_value = rewards + gamma * next_q_value * (1 - dones)

    loss = nn.functional.mse_loss(q_value, expected_q_value)
    optimizer.zero_grad()
    loss.backward()
    optimizer.step()
```

### Target Networks

**Fixed Target Network:**
- Separate network for computing bootstrap targets
- Reduces temporal dependence and oscillation
- Updated occasionally (every k steps) with policy network weights

**Benefits:**
- **Stability**: Prevents wild oscillations during training
- **Convergence**: Smooths learning dynamics
- **Consistency**: Target remains constant during mini-batch updates

### Double Q-Learning

**Double DQN Modification:**
- Use policy network for action selection
- Use target network for action evaluation
- **Action Selection**: a' = argmax Q_policy(s',a')
- **Value Estimation**: target = r + γ Q_target(s',a')

**Reduces Overestimation Bias:**
- Max operation tends to overestimate Q-values
- Double Q-learning separates selection from evaluation
- Significant improvement in sample efficiency

## Advanced Policies & Optimization

### Policy Gradient Methods

**REINFORCE Algorithm:**
- Direct policy optimization through gradient ascent
- Monte Carlo estimation of expected returns
- Gradient: ∇J(θ) = E[∇_θ log π(a|s) G_t]

**Advantages:**
- **Monte Carlo Policy Gradient**: First-visit Monte Carlo for expected returns
- **Function Approximation**: Neural networks parameterize policy
- **Continuous Actions**: Natural handling of continuous action spaces

### Actor-Critic Methods

**Architecture Components:**
- **Actor**: Policy network π_θ(a|s) - responsible for action selection
- **Critic**: Value function network V_φ(s) - evaluates policy quality
- **Advantage Function**: A(a,s) = Q(s,a) - V(s) relative value estimation

**Training Dynamics:**
- Critic learns value function through TD learning
- Actor updates policy based on critic's value estimates
- Balance between policy and value function learning

### Proximal Policy Optimization (PPO)

**Trust Region Policy Optimization:**
- Conservative policy updates within trust region
- Avoids destructive policy changes
- Clipped objective function prevents large deviations

**PPO Implementation:**
```python
def ppo_loss(old_log_probs, new_log_probs, advantages, clip_param=0.2):
    """Clipped PPO objective"""
    ratio = torch.exp(new_log_probs - old_log_probs)
    clipped_ratio = torch.clamp(ratio, 1.0 - clip_param, 1.0 + clip_param)
    return torch.min(ratio * advantages, clipped_ratio * advantages)
```

## Exploration Challenges

### Sample Efficiency

**Sparse Rewards:**
- environment feedback infrequent or binary
- Exploration required to discover reward locations
- Curse of credit assignment across time

**Distributional Shift:**
- Training distributions differ from execution
- Off-policy learning addresses sampling mismatch
- Replay buffers enable multi-step trajectories

### Reward Engineering

**Reward Shaping:**
- Hand-designed reward modifications to accelerate learning
- Potential function ΔR(s,s') ensuring optimality preservation
- Scalarization of complex objectives into single signal

**Multi-Objective RL:**
- Multiple competing reward functions
- Pareto optimality trade-offs
- Weighted sum scalarization for simplicity

### Partial Observability

**Partially Observable MDPs (POMDPs):**
- Agent lacks complete state information
- Belief states represent probability distributions
- Memory and information processing requirements

**Recurrent Policies:**
- LSTM networks maintain internal state representations
- History-dependent action selection
- Bridging perception and memory

## Applications & Extensions

### Inverse Reinforcement Learning

**Learning from Demonstration:**
- Expert trajectories provide learning signal
- Infer implicit reward function from behavior
- Without reward engineering requirements

### Hierarchical Reinforcement Learning

**Options Framework:**
- Subgoal generation and achievement
- Temporal abstraction through macro-actions
- Hierarchical policy architectures

### Multi-Agent Reinforcement Learning

**Cooperative Settings:**
- Team-based coordination and task allocation
- Communication protocols development
- Emergent cooperation patterns

**Competitive Settings:**
- Game-theoretic equilibrium computation
- Adversarial agent interactions
- Emergent strategic behaviors

### Applications

**Game Playing:**
- Board games (Chess, Go, StarCraft)
- Computer vision games (Atari, Monaco Demolition Racer)
- Real-time strategy games

**Robotics:**
- Autonomous vehicle navigation
- Manipulator arm control
- Quadruped locomotion

**Resource Management:**
- Network optimization and routing
- Computational resource allocation
- Network security threat response

## Challenges & Future Directions

### Sample Complexity

**Fundamental Limitations:**
- Sample complexity exponential in Markov chain diameter
- Statistical learning theory provides lower bounds
- Practical algorithms often far exceed theoretical minima

### Scalability Issues

**State Space Explosion:**
- Curse of dimensionality affects value function learning
- Function approximation mitigates but introduces approximation error
- Architecture design remains critical

### Ethical Considerations

**Robust Value Learning:**
- Value function verification and monitoring
- Distributionally robust reinforcement learning
- Safety constraints incorporation

**Algorithmic Fairness:**
- Equitable policy application across demographics
- Bias elimination in reward function design
- Fair multi-agent systems development

Reinforcement learning provides a general framework for sequential decision-making across diverse domains. Advances in function approximation, exploration strategies, and optimization techniques continue expanding applicability to increasingly complex, real-world problems.
