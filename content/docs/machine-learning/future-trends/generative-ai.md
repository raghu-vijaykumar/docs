---
title: "Generative AI: GANs and Diffusion"
weight: 10
description: "Production-focused reference for GANs and Diffusion: mechanics, stabilization tricks, evaluation (FID/IS), and serving patterns with practical code."
draft: false
---

# Generative AI: GANs and Diffusion

Generative models synthesize new samples from learned data distributions. Two dominant families:
- GANs: generator vs discriminator trained in a minimax game.
- Diffusion: learn to denoise noise through a time-stepped reverse process.

Pick based on data/modality, fidelity vs speed, and ops constraints.

---

## When to use what

- GANs:
  - Pros: fast sampling after training; sharp images.
  - Cons: training instability, mode collapse; weaker text conditioning compared to diffusion without heavy engineering.
  - Good for: image-to-image tasks (SR/translation), domain adaptation, structured priors.

- Diffusion:
  - Pros: stable training, strong conditional generation (text/image/audio), controllability (guidance, adapters).
  - Cons: slower generation (multi-step sampling), larger memory/compute.
  - Good for: text-to-image/video/audio, restoration, controllable generation.

---

## GANs: mechanics and stabilization

Objective (non-saturating):
- Discriminator D tries to maximize log D(x) + log(1 − D(G(z)))
- Generator G tries to maximize log D(G(z))

Common stabilization:
- WGAN-GP (Earth Mover distance + gradient penalty)
- Spectral normalization (constrain Lipschitz constant)
- Two-time-scale update rule (TTUR): different lrs for D and G
- Historical averaging, label smoothing, balanced update ratios (e.g., n_critic)

Progression:
- DCGAN → ResGAN → BigGAN → StyleGAN2/3 (modulated convolutions, path length regularization)

Minimal WGAN-GP sketch (PyTorch):
```python
# pip install torch torch-fidelity
import torch, torch.nn as nn, torch.nn.functional as F

def gradient_penalty(D, real, fake):
    bsz = real.size(0)
    eps = torch.rand(bsz, 1, 1, 1, device=real.device)
    xhat = eps * real + (1 - eps) * fake
    xhat.requires_grad_(True)
    d_hat = D(xhat)
    grads = torch.autograd.grad(d_hat.sum(), xhat, create_graph=True)[0]
    gp = ((grads.view(bsz, -1).norm(2, dim=1) - 1) ** 2).mean()
    return gp

for it, x in enumerate(loader):
    x = x.to(device)
    z = torch.randn(x.size(0), z_dim, device=device)
    fake = G(z).detach()
    d_real = D(x).mean()
    d_fake = D(fake).mean()
    gp = gradient_penalty(D, x, fake)
    d_loss = (d_fake - d_real) + 10.0 * gp
    D_opt.zero_grad(); d_loss.backward(); D_opt.step()

    if it % 5 == 0:
        z = torch.randn(x.size(0), z_dim, device=device)
        g_loss = -D(G(z)).mean()
        G_opt.zero_grad(); g_loss.backward(); G_opt.step()
```

Evaluation:
- FID (Fréchet Inception Distance) correlates with visual quality/diversity.
- IS (Inception Score) less robust; report both if needed.
```python
# torch-fidelity FID example
from torch_fidelity import calculate_metrics
metrics = calculate_metrics(input1='path/to/reals', input2='path/to/fakes', cuda=True, isc=True, fid=True)
print(metrics['frechet_inception_distance'])
```

---

## Diffusion: forward and reverse process

- Forward (q): gradually add Gaussian noise over T steps: x_t = α_t x_{t-1} + σ_t ε
- Reverse (p): learn denoiser ε_θ(x_t, t, cond) to step back from noise to data
- Training: predict ε (noise) or v-parameterization; loss is MSE on noise
- Sampling: dozens to hundreds of steps; accelerated samplers (DDIM, DPM-Solver)

Conditioning and control:
- Classifier-free guidance: mix unconditional and conditional predictions to steer outputs.
- LoRA/ControlNet/T2I-Adapters: parameter-efficient fine-tuning and spatial controls.

Hugging Face diffusers (text-to-image) fast path:
```python
# pip install diffusers transformers accelerate safetensors torch --upgrade
import torch
from diffusers import StableDiffusionPipeline, DPMSolverMultistepScheduler

model_id = "runwayml/stable-diffusion-v1-5"
pipe = StableDiffusionPipeline.from_pretrained(model_id, torch_dtype=torch.float16)
pipe = pipe.to("cuda")
pipe.scheduler = DPMSolverMultistepScheduler.from_config(pipe.scheduler.config)

out = pipe(
    prompt="a photo of a red vintage car parked on a cobblestone street, golden hour",
    num_inference_steps=20,
    guidance_scale=7.5,
).images[0]
out.save("car.png")
```

Latency levers:
- Fewer steps, faster schedulers (DPM-Solver, DDIM)
- xFormers attention, attention slicing, VAE tiling
- FP16/bfloat16, TensorRT-LLM / ONNX Runtime / OpenVINO
- Batch prompts + KV/cache reuse (for transformer U-Nets where applicable)

---

## Data, safety, and policy

- Dataset curation: deduplicate, NSFW filter, license-aware (copyright/trademark).
- Prompt safety: blocklists, semantic moderation, policy prompts, and rejection sampling.
- Output filtering: CLIP-based NSFW, watermarking artifacts (visible/invisible).
- Model governance: document training data sources, fine-tuning datasets, safety red-teaming, and update logs.

---

## Serving and scaling

Patterns:
- Single-node GPU service for low QPS: FastAPI + PyTorch/autocast + diffusion pipeline; batch small requests within a deadline for throughput.
- High-throughput GPU serving: NVIDIA Triton with Python backend or TensorRT engines; KServe for Kubernetes-native routing, canaries, autoscaling.
- Multi-tenant: queueing + schedulers (per-tenant quotas); shard models by popularity; lazy load LoRA adapters.

FastAPI sketch (GPU batching window):
```python
# batch requests arriving within 50ms
from fastapi import FastAPI
from queue import Queue
import threading, time
app = FastAPI()
q = Queue()

def worker():
    while True:
        batch = []
        first_req = q.get()
        batch.append(first_req)
        t0 = time.time()
        while (time.time() - t0) < 0.05 and not q.empty():
            batch.append(q.get())
        # run diffusion on batch prompts; return results

threading.Thread(target=worker, daemon=True).start()

@app.post("/generate")
def generate(prompt: str):
    q.put(dict(prompt=prompt))
    return {"status": "queued"}
```

Triton tip:
- Build an ensemble: tokenizer → text encoder → UNet → VAE decoder; enable dynamic batching and concurrent model instances.

---

## Cost and performance checklist

- Target latency/quality envelope: steps vs scheduler vs guidance scale
- Mixed precision + memory optimizations (slicing/tiling/checkpointing)
- Batch small requests; pin CPU threads; avoid oversubscription
- Cache encoder outputs for repeated prompts; share LoRA weights across workers
- Monitor FID-like proxies offline; track live rejection rates, timeouts, and GPU utilization

---

## Common pitfalls

- GAN mode collapse: diversify noise, enforce Lipschitz (GP/spectral norm), adjust n_critic and lr ratios.
- Diffusion OOM/timeouts: too many steps, large resolutions; use tiling, reduce steps, FP16, and optimize attention.
- Weak conditioning: insufficient guidance or mislabeled fine-tune data; review prompts and adapters.
- Unclear licensing/safety: document data sources and apply filters; review outputs with policy.

---

## Key takeaways

- Use GANs for fast sampling and image-to-image with strong priors; use diffusion for robust, controllable text/image generation.
- Stabilize GANs (WGAN-GP, spectral norm) and accelerate diffusion (fast samplers, FP16, attention optimizations).
- Treat data/safety/governance as first-class; productionize with batching, Triton/KServe, and explicit SLOs.
