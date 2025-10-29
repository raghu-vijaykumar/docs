---
title: "Gradient Descent: SGD, Momentum, Adam, RMSProp"
weight: 10
description: "Practical optimization for ML: step rules, momentum methods, adaptive optimizers, schedules, and diagnostics for stable convergence."
draft: false
---

# Gradient Descent: SGD, Momentum, Adam, RMSProp

Optimization turns a loss landscape into parameters that work in production. The right optimizer and schedule stabilize training, improve final quality, and reduce tuning time. This guide focuses on practical choices for tabular ML and deep learning, with clear trade-offs and failure modes.

---

## Core setup

We minimize a differentiable objective J(θ) over parameters θ.

- Batch GD: θ ← θ − η ∇J(θ) using all data; stable but slow per step.
- Stochastic GD (SGD): θ ← θ − η ∇J_i(θ) using one or few examples; noisy but fast and regularizing.
- Mini-batch SGD: the default in deep learning. Balance throughput and gradient noise (e.g., batch 32–1024).

Key knobs:
- Learning rate η (most important hyperparameter)
- Batch size (affects gradient noise and generalization)
- Regularization (weight decay, dropout, early stopping)

---

## Momentum methods

### Classical Momentum
Accumulates a velocity vector to accelerate along consistent directions and damp noise.

- v_t = β v_{t−1} + (1 − β) ∇J(θ_t)
- θ_{t+1} = θ_t − η v_t

β ≈ 0.9 is a common default.

### Nesterov Momentum
Look ahead by one momentum step to compute gradients, improving responsiveness.

- v_t = β v_{t−1} + (1 − β) ∇J(θ_t − η β v_{t−1})
- θ_{t+1} = θ_t − η v_t

Use cases:
- Deep nets with smooth losses
- Faster convergence than plain SGD on many problems

---

## Adaptive methods

### RMSProp
Scales updates by a running average of recent squared gradients (per-parameter).

- s_t = ρ s_{t−1} + (1 − ρ) [∇J(θ_t)]²
- θ_{t+1} = θ_t − η ∇J(θ_t) / (√(s_t) + ε)

Defaults: ρ≈0.9, ε≈1e−8.

### Adam
Combines Momentum (first moment) and RMSProp (second moment) with bias correction.

- m_t = β1 m_{t−1} + (1 − β1) g_t
- v_t = β2 v_{t−1} + (1 − β2) g_t²
- m̂_t = m_t / (1 − β1^t); v̂_t = v_t / (1 − β2^t)
- θ_{t+1} = θ_t − η m̂_t / (√(v̂_t) + ε)

Defaults: β1=0.9, β2=0.999, ε=1e−8 work well broadly.

### AdamW (Decoupled weight decay)
Applies L2 regularization as true weight decay, improving generalization and stability vs. Adam+L2.

- θ_{t+1} = θ_t − η [ m̂_t / (√(v̂_t) + ε) + λ θ_t ]

Prefer AdamW over Adam with L2 penalty in modern deep learning.

---

## Learning-rate schedules

Static η works for convex problems; deep networks generally benefit from schedules.

- Step decay: η ← η × γ at fixed epochs (simple, robust).
- Exponential decay: η_t = η_0 × γ^t (fast reduction, can under-train).
- Cosine annealing: Smooth decay; often paired with restarts (SGDR).
- One-cycle policy: Warm-up to a high LR, then anneal; strong baseline for vision/NLP.
- Warm-up: Start small for a few epochs to avoid early divergence, especially with large batch sizes or layer norms.

Heuristics:
- Start η higher and reduce when val loss plateaus.
- Too high η → loss explodes/NaN; too low η → slow, underfits.

---

## Batch size and generalization

- Larger batches reduce gradient noise, improve throughput, but can hurt generalization if LR isn’t scaled appropriately.
- Linear scaling rule: when batch size ×k, set η ×k (with warm-up).
- BatchNorm statistics depend on batch size; very small batches can destabilize training.

---

## Regularization and constraints

- Weight decay (L2): Smooths weights; use AdamW-style decoupling.
- Gradient clipping: clip global norm (e.g., 1.0) to avoid exploding gradients (RNNs, transformers).
- Early stopping: monitor a validation metric, restore best weights. Reduces overfitting and wasted compute.
- Label smoothing: stabilizes classification; interacts with calibration.

---

## Practical defaults

- Tabular + tree models: Optimizer irrelevant (not gradient-based). Skip scaling except for linear boosters.
- Tabular + linear/logistic: SGD or LBFGS; StandardScaler; moderate L2.
- Deep vision/NLP: AdamW + cosine or one-cycle; weight decay 0.01; warm-up 3–10% of steps.
- RNN/LSTM/GRU: Adam/AdamW; gradient clipping; consider learning-rate decay.
- Transformers: AdamW (β1=0.9, β2=0.98 in some setups), warm-up, cosine schedule.

---

## Diagnostics and failure modes

- Training loss diverges early: η too high, missing warm-up, bad initialization, exploding gradients.
- Loss decreases but accuracy stalls: wrong LR regime, class imbalance, poor regularization.
- Val improves then worsens: overfitting; increase weight decay, add dropout, or use early stopping.
- Loss zigzags heavily: reduce η or increase batch size; consider momentum tuning.
- Adam plateaus at suboptimal: try SGD+Nesterov late-stage “fine-tuning” for better minima on some tasks.

---

## PyTorch examples

### SGD with Nesterov momentum and cosine schedule
```python
import torch
from torch.optim import SGD
from torch.optim.lr_scheduler import CosineAnnealingLR

model = ...
optimizer = SGD(model.parameters(), lr=0.1, momentum=0.9, nesterov=True, weight_decay=1e-4)
scheduler = CosineAnnealingLR(optimizer, T_max=100)  # epochs

for epoch in range(100):
    model.train()
    for x, y in train_loader:
        optimizer.zero_grad()
        loss = criterion(model(x), y)
        loss.backward()
        torch.nn.utils.clip_grad_norm_(model.parameters(), max_norm=1.0)
        optimizer.step()
    scheduler.step()
```

### AdamW with warm-up and cosine decay (per-step)
```python
import math

optimizer = torch.optim.AdamW(model.parameters(), lr=3e-4, betas=(0.9, 0.999), weight_decay=0.01)

total_steps = len(train_loader) * num_epochs
warmup_steps = int(0.1 * total_steps)

def lr_lambda(step):
    if step < warmup_steps:
        return float(step) / float(max(1, warmup_steps))
    progress = (step - warmup_steps) / float(max(1, total_steps - warmup_steps))
    return 0.5 * (1.0 + math.cos(math.pi * progress))

scheduler = torch.optim.lr_scheduler.LambdaLR(optimizer, lr_lambda)

step = 0
for epoch in range(num_epochs):
    model.train()
    for x, y in train_loader:
        optimizer.zero_grad()
        loss = criterion(model(x), y)
        loss.backward()
        torch.nn.utils.clip_grad_norm_(model.parameters(), 1.0)
        optimizer.step()
        scheduler.step()
        step += 1
```

---

## TensorFlow/Keras examples

```python
import tensorflow as tf

model = tf.keras.Sequential([...])
model.compile(
    optimizer=tf.keras.optimizers.AdamW(learning_rate=3e-4, weight_decay=1e-2),
    loss="sparse_categorical_crossentropy",
    metrics=["accuracy"]
)

# Cosine decay with warmup
initial_lr = 3e-4
warmup_epochs = 3
total_epochs = 30

def scheduler(epoch, lr):
    if epoch < warmup_epochs:
        return initial_lr * (epoch + 1) / warmup_epochs
    progress = (epoch - warmup_epochs) / (total_epochs - warmup_epochs)
    return 0.5 * initial_lr * (1 + tf.math.cos(tf.constant(math.pi) * progress))

cb = tf.keras.callbacks.LearningRateScheduler(scheduler)

early = tf.keras.callbacks.EarlyStopping(
    monitor="val_loss", patience=5, restore_best_weights=True
)

model.fit(train_ds, validation_data=val_ds, epochs=total_epochs, callbacks=[cb, early])
```

---

## Numerical stability tips

- Add ε (1e−8) to denominators in adaptive methods.
- Use float32; switch to mixed precision only with care (enable loss scaling).
- Monitor gradients and parameter norms to catch instability early.
- Ensure loss is well-behaved (e.g., use log-softmax + NLL for classification).

---

## Summary

- Start with AdamW + cosine or one-cycle as a strong default for deep learning.
- Consider SGD+Nesterov once close to convergence to potentially sharpen solutions.
- Tune only a few knobs first: learning rate, weight decay, batch size, schedule.
- Add gradient clipping for RNNs/transformers; use warm-up for large batches and normalized architectures.
