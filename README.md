# 🤖 ProcureAI Copilot — AI-Powered Procurement Intelligence Platform

> **"An intelligent procurement assistant that combines LLM-powered natural language understanding with multi-agent orchestration to analyze suppliers, assess risks, and manage contracts."**

## 🎯 Overview

ProcureAI Copilot is a full-stack AI application that transforms how procurement teams interact with their data. Instead of running SQL queries or navigating complex dashboards, users simply **ask questions in plain English** and get intelligent, data-driven answers.

The system uses a **multi-agent architecture** where a Supervisor Agent classifies user intent and routes to Specialist Agents (Spend Analysis, Risk Assessment, Compliance), each equipped with real-time data tools and powered by **Groq's Llama 3.3 70B LLM**.

## ✨ Key Features

| Feature | Description | Real AI? |
|---------|-------------|----------|
| **💬 AI Chat Assistant** | Ask questions in natural language — "Show me top suppliers by spend" | ✅ Groq LLM |
| **🧠 Multi-Agent System** | Supervisor routes to Spend/Risk/Compliance specialists automatically | ✅ Agent Orchestration |
| **⚡ Real-time Streaming** | AI responses stream word-by-word via SSE | ✅ SSE Events |
| **📊 Intelligent Dashboard** | Stats cards, live charts, top suppliers, risk analysis | ✅ Realtime Data |
| **⚠️ Risk Analysis** | Identify high-risk suppliers with color-coded scores | ✅ Groq LLM |
| **📄 RAG Document Q&A** | Upload contracts, ask questions with context-aware answers | ✅ Retrieval Augmented |
| **🔧 Tool Calling** | LLM calls real Java methods to fetch live data | ✅ Function Calling |

## 🏗️ Architecture

```
┌─────────────┐     ┌───────────────────┐     ┌─────────────────┐
│  React 19   │────▶│  Spring Boot 3    │────▶│  H2/PostgreSQL  │
│  TypeScript │     │  Java 21          │     │  Database       │
│  Tailwind   │◀────│  REST APIs        │◀────│                 │
└─────────────┘     └─────────┬─────────┘     └─────────────────┘
                              │
                     ┌────────▼────────┐
                     │  Groq Llama 3.3 │
                     │  70B (Free)     │
                     └────────┬────────┘
                              │
                     ┌────────▼────────┐
                     │  Multi-Agent    │
                     │  Orchestrator   │
                     │  ┌──────────┐   │
                     │  │Supervisor│   │
                     │  │ Agent    │   │
                     │  └────┬─────┘   │
                     │  ┌────┼────┐    │
                     │  ▼    ▼    ▼    │
                     │ S    R    C    │
                     │ p    i    o    │
                     │ e    s    m    │
                     │ n    k    p    │
                     │ d    │    l    │
                     └───────────┘─────┘
```

## 🚀 Quick Start

### Prerequisites
- **Java 21+** (JDK)
- **Node.js 20+**
- **Docker Desktop** (or use Maven directly)
- **Groq API Key** (free — included in `.env`)

### Run with Docker (One Command — Recommended)

```bash
# Start everything (backend + frontend)
cd procureai-copilot
docker compose up -d --build
```

### Run Locally (Two Terminals)

**Terminal 1 — Backend:**
```bash
cd backend
mvn spring-boot:run
```

**Terminal 2 — Frontend:**
```bash
cd frontend
npm install
npm run dev
```

### Access
| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:3000 | React UI (Dashboard, Chat, Suppliers, RAG) |
| **Backend API** | http://localhost:8080 | REST API endpoints |
| **Health Check** | http://localhost:8080/api/health | Backend status |

## 💬 Try These Queries

Open the AI Chat page and ask:

| Query | What Happens |
|-------|-------------|
| *"Show me top suppliers by spend"* | LLM calls `getTopSuppliers()` → returns ranked list with analysis |
| *"Which suppliers are high risk?"* | LLM calls `getHighRiskSuppliers()` → identifies risks with scores |
| *"How many suppliers do we have?"* | LLM calls `getSupplierCount()` → returns total + high risk count |
| *"Show spend by category"* | LLM calls `getSpendByCategory()` → category breakdown with totals |
| *"Give me contract overview"* | LLM calls `getContractSummary()` + `getExpiringContracts()` |
| *"Hello, what can you do?"* | Assistant explains capabilities with examples |

## 📡 API Endpoints

### Supplier Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/suppliers` | List all suppliers |
| `GET` | `/api/suppliers/{id}` | Get supplier by ID |
| `POST` | `/api/suppliers` | Create supplier |
| `GET` | `/api/suppliers/top` | Top suppliers by spend |
| `GET` | `/api/suppliers/search?q=` | Search by name |
| `GET` | `/api/suppliers/stats` | Dashboard statistics |
| `POST` | `/api/suppliers/seed?count=N` | Generate N random suppliers |

### AI Chat
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/chat` | Send message → AI responds with trace |
| `GET` | `/api/chat/stream?message=` | SSE streaming response |

### RAG Document Q&A
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/rag/ingest` | Upload document |
| `POST` | `/api/rag/ask` | Ask question about uploaded docs |

## 🧪 Testing

```bash
# Test health
curl http://localhost:8080/api/health

# Test AI Chat
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"Show me top suppliers"}'

# Seed test data
curl -X POST http://localhost:8080/api/suppliers/seed?count=100

# Get stats
curl http://localhost:8080/api/suppliers/stats
```

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Java 21, Spring Boot 3.4, Spring Data JPA, H2/PostgreSQL |
| **Frontend** | React 19, TypeScript, Vite 6, Tailwind CSS 4, Recharts |
| **AI/LLM** | Groq Llama 3.3 70B (free, ~1000 req/min), Agent Orchestration |
| **AI Pattern** | Multi-Agent (Supervisor + Specialists), Tool Calling, RAG |
| **Infrastructure** | Docker, Docker Compose, Maven |

## 📊 Interview Talking Points

> *"ProcureAI Copilot demonstrates my ability to build production-grade AI applications with real LLM integration. The multi-agent architecture uses a Supervisor pattern that classifies user intent using keyword detection combined with LLM routing, then delegates to Specialist Agents equipped with real data tools. The entire system runs on Groq's Llama 3.3 70B — a free, blazing-fast inference API — and uses SSE streaming for real-time responses. This project showcases Java 21 with Spring Boot 3, React 19 with TypeScript, and end-to-end AI orchestration."*

## 📝 License

MIT
