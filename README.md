# Failure-Aware Workflow Orchestration Engine

A distributed workflow orchestration platform designed for reliable asynchronous execution of multi-step workflows with intelligent failure diagnostics, retry handling, and event-driven worker coordination.

---

# Overview

This project demonstrates the design and implementation of a distributed workflow orchestration engine inspired by modern orchestration platforms such as Temporal, Airflow, and AWS Step Functions.

The platform focuses on:

- Event-driven workflow execution
- Reliable asynchronous processing
- Retry and dead-letter queue handling
- Workflow state management
- AI-assisted failure diagnostics
- Reusable orchestration infrastructure

---

# Architecture

```text
Client
   ↓
Workflow API
   ↓
Workflow Engine
   ↓
Kafka Event Bus
   ↓
Worker Services
   ↓
Result Events
   ↓
Failure Analyzer
```

---

# Key Features

## Workflow Orchestration
- Multi-step workflow execution
- Sequential task coordination
- Distributed worker communication

## Event-Driven Architecture
- Kafka-based asynchronous communication
- Decoupled orchestration and business logic
- Scalable worker processing

## Reliability Engineering
- Retry handling
- Exponential backoff
- Dead Letter Queue (DLQ)
- Idempotent execution

## AI-Assisted Failure Diagnostics
- Failure categorization
- Root cause analysis
- Suggested recovery actions
- Human-readable operational insights

---

# Tech Stack

| Category | Technologies |
|---|---|
| Backend | Java, Spring Boot |
| Messaging | Apache Kafka |
| Cache | Redis |
| Database | PostgreSQL |
| AI Diagnostics | Ollama, Llama 3.1 |
| Containerization | Docker |
| Cloud | Google Cloud Platform |
| CI/CD | GitLab CI/CD |

---

# Project Structure

```text
workflow-engine/
│
├── workflow-service/
├── executor-service/
├── failure-analyzer-service/
├── flight-worker-service/
├── hotel-worker-service/
├── cab-worker-service/
├── common-library/
└── docker-compose.yml
```

---

# Workflow Example

## Trip Booking Workflow

```text
Book Flight
    ↓
Book Hotel
    ↓
Book Cab
```

---

# Example Workflow Definition

```json
{
  "workflowName": "TripBookingWorkflow",
  "steps": [
    {
      "stepName": "BookFlight",
      "topic": "flight-booking-topic",
      "maxRetries": 3
    },
    {
      "stepName": "BookHotel",
      "topic": "hotel-booking-topic",
      "maxRetries": 3
    },
    {
      "stepName": "BookCab",
      "topic": "cab-booking-topic",
      "maxRetries": 3
    }
  ]
}
```

---

# Local Setup

## Prerequisites

- Java 21+
- Maven
- Docker
- Docker Compose
- Kafka
- PostgreSQL
- Redis
- Ollama (optional)

---

# Clone Repository

```bash
git clone https://github.com/your-username/failure-aware-workflow-engine.git

cd failure-aware-workflow-engine
```

---

# Start Infrastructure

```bash
docker-compose up -d
```

---

# Run Workflow Service

```bash
cd workflow-service

mvn spring-boot:run
```

---

# Optional: Run Ollama

Install Ollama:
https://ollama.com

Pull model:

```bash
ollama pull llama3.1:8b
```

Run model:

```bash
ollama run llama3.1:8b
```

---

# API Endpoints

## Create Workflow

```http
POST /workflows
```

## Execute Workflow

```http
POST /workflows/{workflowId}/execute
```

## Workflow Status

```http
GET /workflows/{workflowId}/status
```

## Failure Analysis

```http
GET /workflows/{workflowId}/failure-analysis
```

---

# Future Enhancements

- Parallel DAG execution
- Workflow visualization UI
- Distributed tracing
- Metrics dashboard
- Kubernetes deployment
- Multi-tenant support

---

# License

MIT License
