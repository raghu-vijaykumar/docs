---
title: "LLMs in Practice"
weight: 20
description: "Practical guide to training, adapting, serving, and evaluating Large Language Models: SFT/RLHF/DPO, RAG, quantization, batching/KV cache, safety, and cost control."
draft: false
---

# LLMs in Practice

Large Language Models (LLMs) deliver strong language understanding and generation via Transformer architectures. Production success depends less on pretraining from scratch and more on effective adaptation (fine-tuning, prompting, RAG), efficient serving, safety, and measurement.

---

## Core mechanics (what matters operationally)

- Transformer blocks: self-attention scales with sequence length; prefer FlashAttention-style kernels in prod.
- Tokenization: BPE/Unigram tokenizers affect latency and context use; standardize across training/serving.
- Context window: governs prompt size and latency; use retrieval and compression to stay within limits.
- KV cache: cache attention keys/values across decoding steps to make per-token generation O(1) in time per layer.

---

## Model adaptation strategies

Choose the lightest method that meets quality and control requirements:

- Prompt engineering: few-shot/system prompts; zero infra changes, fast iteration. Guard for prompt injection and ensure deterministic templates.
- Retrieval-Augmented Generation (RAG): ground outputs in your data via retrieval; reduces hallucinations and keeps models fresh without re-training.
- Supervised Fine-Tuning (SFT): tune on instruction data for target tone and task formats. Prefer parameter-efficient fine-tuning (PEFT).
- Preference optimization (RLHF/DPO/IPO): align outputs to human preferences and safety constraints. Costly but useful for consumer-facing assistants.
- LoRA/QLoRA (PEFT): low-rank adapters; memory- and time-efficient. QLoRA enables training with 4-bit base weights.

Minimal LoRA (PyTorch/PEFT) sketch:
```python
# pip install peft transformers accelerate bitsandbytes
from transformers import AutoModelForCausalLM, AutoTokenizer
from peft import LoraConfig, get_peft_model

model_id = "meta-llama/Llama-3-8b"
tok = AutoTokenizer.from_pretrained(model_id, use_fast=True)
model = AutoModelForCausalLM.from_pretrained(model_id, load_in_4bit=True, device_map="auto")

peft_cfg = LoraConfig(
    r=16, lora_alpha=32, target_modules=["q_proj", "v_proj"], lora_dropout=0.05, bias="none", task_type="CAUSAL_LM"
)
model = get_peft_model(model, peft_cfg)
# Train with your SFT data loader; save adapters
model.save_pretrained("llama3-8b-lora")
```

When to full-fine-tune: domain shift too large, heavy safety/formatting requirements, or latency budget incompatible with RAG.

---

## Retrieval-Augmented Generation (RAG)

RAG injects authoritative context into prompts.

- Indexing: chunking (semantic-aware), embeddings (e.g., bge, e5), HNSW/IVF indexes (FAISS, Milvus, pgvector).
- Query pipeline: rewrite → retrieve → rerank → synthesize; add filters per tenant/security labels.
- Context optimization: deduplicate, compress (Map/Reduce, fusion), cite sources.
- Freshness: schedule re-embedding and index rebuilds; manage invalidation.

Quick RAG sketch (FAISS + Transformers):
```python
# pip install sentence-transformers faiss-cpu
from sentence_transformers import SentenceTransformer
import faiss, numpy as np

emb = SentenceTransformer("intfloat/e5-base-v2")
docs = ["..."]  # your chunks
X = emb.encode([f"passage: {d}" for d in docs], normalize_embeddings=True)
index = faiss.IndexFlatIP(X.shape[1]); index.add(np.array(X).astype("float32"))

def retrieve(q, k=5):
    qv = emb.encode([f"query: {q}"], normalize_embeddings=True).astype("float32")
    D, I = index.search(qv, k)
    return [docs[i] for i in I[0]]
```

---

## Inference and serving

Targets: predictable latency, high throughput, and cost efficiency.

- Engines/runtimes:
  - vLLM: PagedAttention, strong throughput with continuous batching; OpenAI-compatible server.
  - TensorRT-LLM: NVIDIA-optimized; best latency on GPUs; requires engine builds.
  - Text Generation Inference (TGI), Ollama: ease-of-use options.
- Quantization:
  - Post-training: GPTQ, AWQ, INT8/INT4; trade slight quality for large memory savings.
  - KV cache quant: further memory reduction with careful quality checks.
- Batching and parallelism:
  - Continuous/dynamic batching with max queue delay (e.g., 20–50 ms).
  - Tensor/TP parallel for larger models; ensure comms bandwidth (NVLink/IB).
- Speculative/medusa decoding:
  - Draft smaller model proposes tokens; larger verifier accepts/rejects to reduce latency.

vLLM server example:
```bash
pip install vllm
python -m vllm.entrypoints.openai.api_server \
  --model meta-llama/Llama-3-8b-instruct \
  --dtype auto --tensor-parallel-size 1 \
  --max-num-batched-tokens 8192 --enforce-eager
```

OpenAI-compatible query:
```python
import os, requests
url = "http://localhost:8000/v1/chat/completions"
payload = {"model": "meta-llama/Llama-3-8b-instruct", "messages": [{"role":"user","content":"Summarize..."}]}
print(requests.post(url, json=payload, timeout=30).json())
```

Latency levers:
- Quantize weights and KV cache; enable FlashAttention2; tune max_tokens, max_batch, queue delay.
- Use pinned CPU threads; avoid oversubscription; profile token/sec and p50/p95.

---

## Evaluation and safety

Measure both utility and undesired behaviors.

- Task evals: exact match/F1/ROUGE for generation; BLEU for translation; custom judge models for open-ended prompts.
- Benchmarks: MMLU, GSM8K, HumanEval; use as sanity checks, not deployment gates.
- Instruction-following: use MT-Bench/arena-style pairwise preference evals.
- Safety: jailbreak prompts, toxic content, PII leakage; red-team with automated suites.
- Hallucination control: cite sources with RAG; require grounded answers (refusal when missing).

Guardrails:
- Content filters (regex+ML), prompt templates with system policies, tool allowances/denies.
- Output post-processing: schema validation, constrained decoding (regex/CFG), PII redaction.

---

## Cost control

- Right-size model: prefer 7–13B for many enterprise tasks with strong prompts/RAG; scale up only when needed.
- Quantize (INT8/INT4) and shard across fewer, bigger GPUs vs many small (utilization matters).
- Share KV cache across requests where engines allow; enable continuous batching.
- Offload embeddings to cheaper CPU instances; cache hot embeddings and retrieval results.
- Profile end-to-end: tokenizer, retrieval, model, network; optimize the slowest segment first.

---

## Deployment patterns

- Backend choices: KServe + vLLM/Triton, self-managed GPU nodes, or managed (SageMaker/Vertex/AzureML) with autoscaling.
- Canary and shadow: validate latency and safety before ramping traffic; log prompts/outputs for audits.
- Multi-tenant isolation: per-tenant rate limits/quotas; context window budgets; secure data filters in retrieval.
- Observability: token/sec, queue delay, violation counts, refusal rates, grounding coverage, and per-slice metrics.

---

## Minimal decision guide

- Knowledge QA/chat over internal docs → RAG on 7–13B instruct model + vLLM
- Structured extraction → Constrained decoding with smaller models + strong prompts/few-shot
- Code assistance → 7–34B code-tuned models, higher context; cache/project-aware retrieval
- Long-context summarization → Long-context models or segment+hierarchical summarization

---

## Summary

Adapt LLMs with the lightest method that achieves quality (prompting/RAG → PEFT → full FT). Serve with batching, KV cache, and quantization (vLLM/TensorRT-LLM). Measure real task quality and safety; add retrieval grounding and guardrails. Control cost by right-sizing models and optimizing bottlenecks before scaling out.
