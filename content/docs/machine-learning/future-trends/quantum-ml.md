---
title: "Quantum Machine Learning"
weight: 40
description: "Variational quantum circuits, quantum kernels, and hybrid training under NISQ constraints. Practical stacks, encoding strategies, evaluation, and when QML is worth prototyping."
draft: false
---

# Quantum Machine Learning

Quantum ML (QML) explores using quantum circuits as trainable models or feature mappers. In today’s NISQ (Noisy Intermediate-Scale Quantum) era, success hinges on hybrid classical–quantum workflows, small circuit depths, careful data encoding, and realistic expectations: quantum advantage on practical ML tasks remains unproven under noise and limited qubits.

This guide focuses on how QML works, when to prototype, and safe, reproducible patterns with simulators and limited hardware runs.

---

## When to consider QML

Good reasons to prototype:
- Scientific/industrial problems already mapped to quantum-friendly formulations (chemistry, materials, combinatorial optimization).
- Research pilots where the objective is learning the stack (PennyLane/Qiskit), not shipping production value short-term.
- Kernel methods on low-dimensional features where quantum kernels can be benchmarked vs classical RBF/poly.

Not a good fit (for now):
- Standard tabular/text/vision tasks where classical methods are strong and cheaper.
- Latency-sensitive production serving (hardware access and queueing).
- High-dimensional raw data without domain reduction; encoding cost dominates.

---

## Building blocks

- Parameterized (variational) quantum circuits (VQCs): circuits with trainable rotation angles; output expectation values used as logits/embeddings.
- Quantum kernels: implicit feature maps via kernel k(x, x') = |⟨φ(x)|φ(x')⟩|² estimated on a quantum device.
- Hybrid training: classical optimizer (Adam/L-BFGS) updates circuit parameters using gradients (parameter-shift rule) or gradient-free methods (CMA-ES).

Constraints:
- Qubits: device-limited (tens to low hundreds) and connectivity-limited.
- Depth: coherence and noise cap usable circuit depth; error mitigation may help but adds variance/cost.
- Shot noise: finite sampling yields stochastic estimates; balance shots vs variance.

---

## Data encoding (feature maps)

- Angle encoding: map features to rotation angles (cheap, scalable).
- Amplitude encoding: embed a 2^n vector into n qubits (requires normalization; state-prep cost can be high).
- Hamiltonian encoding: evolve under H(x) for time t; problem-structured.
- Feature map design determines expressivity and inductive bias; start simple (angle encoding) and increase entanglement cautiously.

---

## Variational classifier (VQC): minimal example (PennyLane)

```python
# pip install pennylane numpy scikit-learn
import pennylane as qml
from pennylane import numpy as pnp
from sklearn.datasets import make_moons
from sklearn.model_selection import train_test_split

# Data
X, y = make_moons(n_samples=300, noise=0.2, random_state=42)
X = (X - X.mean(0)) / X.std(0)  # scale
X_tr, X_te, y_tr, y_te = train_test_split(X, y, test_size=0.3, stratify=y, random_state=42)

n_qubits = 2
dev = qml.device("default.qubit", wires=n_qubits, shots=None)

def encode(x):
    for i in range(n_qubits):
        qml.RX(x[i % len(x)], wires=i)
        qml.RY(0.5 * x[i % len(x)], wires=i)

def ansatz(weights):
    for l in range(weights.shape[0]):
        # single-qubit rotations
        for i in range(n_qubits):
            qml.RZ(weights[l, i, 0], wires=i)
            qml.RX(weights[l, i, 1], wires=i)
        # entanglement (CZ ring)
        for i in range(n_qubits):
            qml.CZ(wires=[i, (i + 1) % n_qubits])

@qml.qnode(dev, interface="autograd")
def circuit(x, weights):
    encode(x)
    ansatz(weights)
    return qml.expval(qml.PauliZ(0))

def predict_proba(X, weights):
    logits = pnp.array([circuit(x, weights) for x in X])  # in [-1, 1]
    # map to probability
    return 0.5 * (logits + 1.0)

def loss_fn(weights, Xb, yb):
    p = predict_proba(Xb, weights)
    # binary cross-entropy
    eps = 1e-8
    return -pnp.mean(yb * pnp.log(p + eps) + (1 - yb) * pnp.log(1 - p + eps))

# Initialize weights: L layers, per qubit 2 parameters
L = 2
weights = pnp.random.normal(0, 0.1, (L, n_qubits, 2), requires_grad=True)

opt = qml.GradientDescentOptimizer(stepsize=0.1)
for epoch in range(50):
    batch_idx = pnp.random.choice(len(X_tr), size=64, replace=False)
    Xb, yb = X_tr[batch_idx], y_tr[batch_idx]
    weights = opt.step(lambda w: loss_fn(w, Xb, yb), weights)
    if epoch % 10 == 0:
        train_loss = loss_fn(weights, X_tr, y_tr)
        print(f"Epoch {epoch:02d} | loss={train_loss:.4f}")

proba = predict_proba(X_te, weights)
acc = ((proba >= 0.5).astype(int) == y_te).mean()
print("Test accuracy:", float(acc))
```

Notes:
- Start with simulator (“default.qubit”); move to hardware sparingly due to queue/time limits.
- Use shallow circuits with limited entanglement; increase depth only if underfitting.

---

## Quantum kernel SVM (Qiskit)

```python
# pip install qiskit qiskit-machine-learning scikit-learn
from qiskit import BasicAer
from qiskit.utils import QuantumInstance
from qiskit.circuit.library import ZZFeatureMap
from qiskit_machine_learning.kernels import QuantumKernel
from sklearn.svm import SVC
from sklearn.model_selection import train_test_split
from sklearn.datasets import make_classification
import numpy as np

X, y = make_classification(n_samples=300, n_features=2, n_informative=2, n_redundant=0, random_state=42)
X = (X - X.mean(0)) / X.std(0)
X_tr, X_te, y_tr, y_te = train_test_split(X, y, test_size=0.3, stratify=y, random_state=42)

backend = BasicAer.get_backend("qasm_simulator")
qi = QuantumInstance(backend=backend, shots=1024, seed_simulator=42, seed_transpiler=42)
feature_map = ZZFeatureMap(feature_dimension=X.shape[1], reps=2)
qkernel = QuantumKernel(feature_map=feature_map, quantum_instance=qi)

K_tr = qkernel.evaluate(x_vec=X_tr)
K_te = qkernel.evaluate(x_vec=X_te, y_vec=X_tr)

clf = SVC(kernel="precomputed", C=1.0).fit(K_tr, y_tr)
acc = clf.score(K_te, y_te)
print("Test accuracy:", acc)
```

Notes:
- Compare against classical RBF/linear kernels under the same CV protocol.
- Kernel evaluation scales quadratically with samples; restrict to small datasets.

---

## Training and evaluation guidance

- Baselines first: logistic regression/GBM/SVM with identical splits, metrics, and calibration.
- CV rigor: small datasets inflate variance; use repeated KFold with confidence intervals.
- Hardware vs simulator:
  - Use state vector simulators for quick iteration; switch to shot-based to mimic noise.
  - On hardware, expect stochasticity; run multiple seeds and apply error mitigation cautiously.
- Report utility and cost:
  - Training/inference time, queue times, shot budget, hardware time, and variability.
  - For kernels: report kernel alignment and conditioning.

---

## Tooling and stack

- Frameworks: PennyLane (autodiff across PyTorch/TF/JAX), Qiskit (IBM), Cirq (Google), Braket SDK (AWS).
- Hybrid training:
  - Parameter-shift gradients; batch circuit evaluations to utilize hardware queues efficiently.
  - Gradient-free methods when shift rule is too costly under shots.
- Reproducibility:
  - Fix seeds, pin package versions, export circuit definitions and transpiler settings.
  - Persist raw measurement results (shots) to re-analyze without rerunning hardware.

---

## Reality check and “production”

- Latency/throughput: hardware access and queueing incompatible with typical serving SLOs.
- Cost: hardware time is scarce/expensive; restrict usage to research or offline scoring.
- Governance: document circuit structures, data encoding, mitigation methods; ensure reproducible artifacts.
- Roadmap: treat QML as a research capability with clear decision criteria to stop/continue.

---

## Decision guide

- You have a domain-structured problem (chemistry/optimization) → prototype QAOA/variational ansätze for that domain.
- You have low-d feature data and want kernel comparisons → try quantum kernels vs RBF under strict CV.
- You need classification on small tabular data → start with VQC on a simulator and benchmark against logistic/SVM; continue only if robust win.
- You need production inference → prefer classical ML; revisit QML when hardware/scaling improves.

---

## Key takeaways

- QML today is hybrid, shallow, and research-focused; treat simulators as the main execution target.
- Use simple encodings and shallow circuits; benchmark rigorously against classical baselines.
- Hardware runs are for validation, not scale; document costs, noise, and mitigation choices.
- Maintain reproducibility and realistic claims; avoid declaring advantage without strong, leakage-safe evidence.
