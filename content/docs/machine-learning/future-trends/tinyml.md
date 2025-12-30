---
title: "AI for Edge Devices & TinyML"
weight: 30
description: "Designing, compressing, and deploying ML to microcontrollers and edge devices: quantization, pruning, distillation, compilers, telemetry, and on-device constraints."
draft: false
---

# AI for Edge Devices & TinyML

TinyML brings ML inference to highly constrained devices (microcontrollers and low-power edge). Typical targets offer:
- RAM: 8–512 KB (MCU-class) or a few MB on higher-end edge SoCs
- Flash/ROM: 256 KB–16 MB
- No OS or RTOS, limited or no FPU, strict power/latency budgets

Success hinges on compression (quantization/pruning/distillation), lean architectures, compiler support, and robust telemetry/updates.

---

## When to use TinyML

- Always-on sensing: wake-words, motion/fall detection, anomaly detection on sensors
- Privacy-by-design: process PII locally (audio/biometrics), transmit only signals/alerts
- Ultra-low latency/offline reliability: industrial controls, safety cutoffs, remote locations
- Cost/energy constraints: battery-operated or energy-harvesting devices

When not to use TinyML:
- Heavy transformer inference without accelerators
- Large-context NLP/vision beyond a few hundred kilobytes of model weights
- Frequent model churn without OTA support

---

## Model design for the edge

- Architectures:
  - Audio: tiny CNNs/DS-CNN, 1D convs with MFCC features; keyword spotting
  - Vision: MobileNetV1/V2, EfficientNet-Lite, Tiny-ViT (edge TPU), depthwise separable convs
  - Tabular/sensors: shallow MLPs, temporal convs, classical algorithms with fixed-point math
- Constraints-first design:
  - Parameter budget (flash) and activation budget (RAM) set the upper bound
  - Replace dense layers with depthwise separable convs and small kernels
  - Prefer ReLU/Hard-Swish over expensive activations without hardware support

---

## Compression toolchain

### Quantization

- Post-Training Quantization (PTQ): fast and robust for many tasks; 8-bit integer weights/activations
- Quantization-Aware Training (QAT): simulates quantization during training for accuracy recovery
- Mixed precision: keep sensitive layers in FP16/FP32 when supported

TensorFlow Lite PTQ:
```python
import tensorflow as tf

converter = tf.lite.TFLiteConverter.from_saved_model("export/saved_model")
converter.optimizations = [tf.lite.Optimize.DEFAULT]
# Optional: representative dataset for better activation scaling (recommended)
def rep_data():
    for batch in calibration_ds.take(200):
        yield [batch[0]]
converter.representative_dataset = rep_data
converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS_INT8]
converter.inference_input_type = tf.int8
converter.inference_output_type = tf.int8
tflite_model = converter.convert()
open("model-int8.tflite", "wb").write(tflite_model)
```

ONNX Runtime quantization:
```python
# pip install onnxruntime onnxruntime-tools onnx
from onnxruntime.quantization import quantize_dynamic, QuantType
quantize_dynamic("model.onnx", "model-int8.onnx", weight_type=QuantType.QInt8)
```

### Pruning and sparsity

- Structured pruning (channels/filters) is hardware-friendly
- Unstructured sparsity needs kernel support to realize speedups

Keras pruning (magnitude-based):
```python
# pip install tensorflow-model-optimization
import tensorflow_model_optimization as tfmot
prune_low_magnitude = tfmot.sparsity.keras.prune_low_magnitude

pruned_model = prune_low_magnitude(model, pruning_schedule=tfmot.sparsity.keras.PolynomialDecay(
    initial_sparsity=0.0, final_sparsity=0.5, begin_step=1000, end_step=10000
))
```

### Knowledge distillation

- Train a small student to mimic a larger teacher; reduces size with minimal accuracy loss

```python
# Student loss: blend CE with KL divergence to teacher logits
loss = α * CE(y_true, y_student) + (1-α) * T^2 * KL(softmax(z_teacher/T), softmax(z_student/T))
```

---

## Runtimes and compilers

- TensorFlow Lite / TFLite Micro (no OS; MCU targets)
- ONNX Runtime Mobile (Android/iOS/edge Linux)
- Core ML (Apple SoCs), MediaPipe for pipelines
- Apache TVM: compile models for diverse MCUs/NPUs; auto-tuning kernels
- Edge accelerators: Edge TPU (Coral), NPU (ESP32-S3 NPU variants), ARM Ethos-U, NVIDIA Jetson (bigger edge)

TFLite Micro integration sketch (C/C++):
```c
// Pseudocode: allocate arena (RAM), init interpreter, invoke
constexpr int kArenaSize = 128 * 1024;
static uint8_t tensor_arena[kArenaSize];
tflite::MicroInterpreter interpreter(model, resolver, tensor_arena, kArenaSize, error_reporter);
interpreter.AllocateTensors();
TfLiteTensor* input = interpreter.input(0);
// Fill input->data.int8 with sensor window
interpreter.Invoke();
TfLiteTensor* output = interpreter.output(0);
// Act on output->data.int8 probabilities
```

TVM compile (high level idea):
```python
# tvm.relay.frontend.from_onnx(...) -> build with target="c -mcpu=cortex-m4"
# use AutoTVM/Ansor for kernel tuning; deploy generated C runtime
```

---

## Feature pipelines on-device

- Audio: windowing → STFT → Mel filterbank → log-Mel → optional MFCC
- Vision: grayscale/resize/crop; normalize to int8 zero-point/scale
- Sensors: sliding windows, normalization, engineered deltas/ratios
- Consistency: export the same preprocessing (fixed-point) used in training; validate numerics for parity

---

## Power, latency, and memory budgets

- Measure end-to-end: sensor → preprocess → inference → post-process
- Latency envelope: define max processing time per window (e.g., 20 ms audio frame)
- Duty cycling: sleep aggressively; wake on interrupt or tiny anomaly scorer
- Memory:
  - Flash for weights; RAM for activations/tensor arena
  - Profile peak activation memory; adjust batch=1 and layer-by-layer buffers

---

## Telemetry, OTA, and lifecycle

- Telemetry: track inference counts, confidence, drift proxies (feature norms), power use
- OTA: A/B model slots with rollback; sign artifacts; verify integrity
- Shadow updates: deploy model B side-by-side for evaluation before activation
- Privacy: encrypt firmware/models at rest; secure boot; evaluate side-channel risks

---

## Safety and evaluation

- Task metrics: accuracy/F1/ROC-AUC; confusion on rare events; calibration for thresholding
- Robustness: test across temperature, vibration, noisy environments; augment during training
- False-positive/negative cost audits: design thresholds with domain stakeholders
- Field tests: capture misfires; update datasets; schedule periodic re-training

---

## Example: keyword spotting (KWS) pipeline

1) Train with log-Mel features and a depthwise-separable CNN (DS-CNN).  
2) Apply PTQ with a representative dataset (ambient audio).  
3) Export TFLite and integrate with TFLite Micro.  
4) Duty-cycle the microphone; wake on above-threshold probability.

Keras DS-CNN sketch:
```python
import tensorflow as tf
from tensorflow.keras import layers as L

def dscnn_kws(n_classes=12, time=49, freq=10):
    inp = L.Input((time, freq, 1))
    x = L.Conv2D(64, 3, padding="same", activation="relu")(inp)
    for c in [64, 64, 64]:
        x = L.DepthwiseConv2D(3, padding="same")(x)
        x = L.Conv2D(c, 1, activation="relu")(x)
        x = L.BatchNormalization()(x)
    x = L.GlobalAveragePooling2D()(x)
    out = L.Dense(n_classes, activation="softmax")(x)
    return tf.keras.Model(inp, out)
```

---

## Decision guide

- Tiny MCU, audio/sensor → TFLite Micro, DS-CNN/MLP, INT8 PTQ, fixed-point preprocessing
- Mobile/embedded vision → MobileNet/EfficientNet-Lite, TFLite/ONNX Runtime Mobile, INT8 QAT
- Accelerator present (Edge TPU/NPU) → compile with vendor toolchains; match operator sets
- Strict privacy/offline → on-device inference; upload only signals; OTA updates with signing

---

## Key takeaways

- Start with constraints (flash/RAM/latency/power) and design the model around them.
- Combine PTQ/QAT, pruning, and distillation; validate with a representative dataset.
- Use the right runtime/compiler and ensure preprocessing parity.
- Plan telemetry/OTA and safety from day one; treat models as firmware that must be observable and updatable.
