---
title: "Speech Recognition System Example"
---

# Speech Recognition System Example

Complete end-to-end application of the ML lifecycle to a speech recognition system, demonstrating all phases from scoping to ongoing monitoring.

## Overview

Voice search and speech interfaces have become widespread, with deep learning dramatically improving accuracy. This example shows the full lifecycle of building such systems.

## Scoping Phase

### Project Definition
- **Goal**: Build speech recognition for voice search
- **Input (X)**: Audio clips from smartphones
- **Output (Y)**: Transcribed text for search queries

### Key Metrics
- **Accuracy**: Word error rate goals
- **Latency**: <500ms response time (critical for UX)
- **Throughput**: Queries per second capacity
- **Resources**: Cloud GPUs, development team size, timeline

### Estimation Process
- Guesstimate based on similar projects
- Account for data acquisition challenges
- Plan for deployment constraints (mobile + cloud)

## Data Phase

### Data Collection Challenges
- **Data sources**: Purchased datasets + user opt-in data
- **Licensing**: Respect user privacy and data usage rights
- **Balance**: Mix of purchased and organic user data

### Data Definition Issues
- **Consistency**: Transcribers use different conventions
  - "Um today's weather" vs "Today's weather" vs noise
- **Standardization**: Document single convention, train annotators
- **Performance impact**: Inconsistent data hurts learning algorithms

### Volume Normalization
- Varying speaker loudness within/across clips
- Automatic gain control vs manual preprocessing
- Per-clip vs per-audio-file normalization

### Silence Padding
- How much silence before/after speech?
- Impacts VAD (Voice Activity Detection) performance

## Modeling Phase

### Architecture Selection
- **Research vs Production**: Focus on deployable accuracy over cutting-edge performance
- **Open source priority**: Leverage proven implementations (e.g., Wav2Vec, HuBERT)
- **Cloud optimization**: Balance model size with inference speed

### Data-Centric Development
- **Fixed codebase**: Use established neural architectures
- **Iterative data improvement**: Focus on data quality over model tweaks
- **Error analysis driven**: Systematic failure mode identification

Three model inputs:
- **Code**: Pretrained model + fine-tuning layers
- **Data**: Labeled speech-transcript pairs
- **Hyperparameters**: Learning rate, batch size, epochs

## Deployment Phase

### System Architecture
```
Mobile App → VAD (Edge) → ASR Service (Cloud) → Search Results
```

#### Voice Activity Detection (VAD)
- Lightweight model running on-device
- Detects speech vs silence/noise
- Saves bandwidth by sending only speech clips

#### Automatic Speech Recognition (ASR)
- Heavy model in cloud
- Converts audio to text
- High accuracy requires significant compute

#### Search Integration
- Transcribed text becomes search query
- Returns search results + transcript display

### Drift Challenges
- **Data drift**: Microphone upgrades change audio quality
- **Younger users**: Different vocal characteristics
- **Language evolution**: New terminology, accents
- **COVID impact**: Masked speech changes acoustics

### Degrees of Automation
- **Consumer application**: Full automation preferred
- **Current approach**: Human review for training data improvement
- **Scale**: Thousands of queries per second

## Monitoring Phase

### Software Metrics
- **Latency**: Keep VAD fast for mobile UX
- **Throughput**: Scale cloud ASR capacity
- **Errors**: Track cloud service failures
- **Privacy**: Monitor data handling compliance

### Input Metrics
- **Audio length**: Changes in query lengths
- **Volume distribution**: Speaker loudness shifts
- **Missing data**: Failed recordings percentage

### Output Metrics
- **Null responses**: "No speech detected" rate
- **Retrys**: Users repeating queries (indicative of errors)
- **Abandonment**: Switch to typing instead of speaking
- **Engagement**: Search completion rates

### Pipeline Monitoring
- **VAD accuracy**: How well it detects speech starts/ends
- **Clipping effects**: Changes in audio sent to ASR
- **Cascading errors**: VAD mistakes → ASR failures

## Continuous Operation

### Performance Degradation
- **Initial deployment**: Trained on adult voices
- **User evolution**: Younger demographic adoption
- **Solution**: Retrain with diverse voice data

### Data Feedback Loop
- Deployed system captures new audio
- User corrections improve training data
- Regular model retraining with updated datasets

## Lessons Learned

- **Planning matters**: Lifecycle framework prevents surprises
- **Data quality critical**: Inconsistent labeling has lasting impact
- **Monitoring essential**: Drift happens continuously
- **Feedback loops**: Production data improves future models
- **Privacy first**: User consent and data protection throughout

## Key Takeaways

Speech recognition demonstrates that deployment is only ~50% complete at launch. The remaining effort goes to monitoring, drift detection, and continuous model improvement.

## Related Topics

- [ML Lifecycle](../../lifecycle/)
- [Data Drift Detection](../../deployment/challenges/#data-drift)
- [Pipeline Monitoring](../../monitoring/pipelines/#speech-recognition-pipeline)
