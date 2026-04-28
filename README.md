# rag-chatbot
Q&amp;A Chatbot using Spring AI

# RAG Chatbot with Java Spring Boot + Spring AI + Ollama + pgvector

A production-oriented, beginner-friendly **RAG (Retrieval-Augmented Generation) Q&A chatbot** built with **Java Spring Boot** and **Spring AI**.

This project answers:

1. **General/public questions** using LLM knowledge.
2. **Private/internal questions** using your uploaded documents (PDF/TXT/DOCX) via RAG.

---

## Table of Contents

- [What this project is](#what-this-project-is)
- [Why this project matters](#why-this-project-matters)
- [Core concepts covered](#core-concepts-covered)
- [High-level architecture](#high-level-architecture)
- [Tech stack](#tech-stack)
- [Project features by phase](#project-features-by-phase)
- [Project structure](#project-structure)
- [How request flow works](#how-request-flow-works)
- [Local setup guide (step-by-step)](#local-setup-guide-step-by-step)
- [Configuration](#configuration)
- [How to run](#how-to-run)
- [API endpoints](#api-endpoints)
- [Testing with curl](#testing-with-curl)
- [Common issues and fixes](#common-issues-and-fixes)
- [Security considerations](#security-considerations)
- [Production improvements roadmap](#production-improvements-roadmap)
- [Resume-ready bullet points](#resume-ready-bullet-points)
- [Interview Q&A](#interview-qa)
- [License](#license)

---

## What this project is

This is a **Q&A chatbot backend** that combines:

- **LLM generation** (for natural-language answers),
- **document retrieval** (for factual grounding from private data),
- **vector similarity search** (for semantic matching).

It uses a **retrieval-first strategy**:

- If relevant chunks are found in uploaded documents → answer in **PRIVATE_RAG** mode.
- If no relevant chunks are found → fallback to **GENERAL_LLM** mode.

---

## Why this project matters

Normal chatbots can hallucinate and cannot reliably answer internal/company-specific questions.

RAG solves this by:

- retrieving relevant internal knowledge,
- injecting it as context into prompts,
- generating grounded answers with source traceability.

This mirrors how many real enterprise AI assistants are implemented.

---

## Core concepts covered

This project teaches practical and production-oriented understanding of:

- Spring AI fundamentals
- LLM integration in Spring Boot
- Embeddings
- Chunking and chunk overlap
- Vector databases (`pgvector`)
- Semantic search
- RAG pipeline design
- Prompt engineering
- Document ingestion
- API design for AI applications
- Private-vs-public fallback strategy

---

## High-level architecture

```text
User
  │
  ├── Upload Document (PDF/TXT/DOCX)
  │       │
  │       ├── Parse text
  │       ├── Chunk text
  │       ├── Generate embeddings
  │       └── Store vectors + metadata in PostgreSQL (pgvector)
  │
  └── Ask Question
          │
          ├── Embed question
          ├── Similarity search top-K chunks
          ├── If chunks found -> build RAG prompt with context
          │                     -> call Ollama chat model
          │                     -> return answer + sources (PRIVATE_RAG)
          │
          └── If no chunks -> ask LLM directly (GENERAL_LLM)
```

---

## Tech stack

### Language & Framework
- Java 21
- Spring Boot 3.x
- Spring AI 1.x

### AI/ML Runtime
- Ollama (local model runtime)
- Chat model: `llama3.1:8b`
- Embedding model: `nomic-embed-text`

### Storage
- PostgreSQL 16
- pgvector extension

### Document Parsing
- Apache PDFBox (PDF)
- Apache POI (DOCX)
- UTF-8 parser (TXT)

### DevOps
- Docker Compose (for PostgreSQL + pgvector)

---

## Project features by phase

### Phase 1
- Spring Boot + Spring AI setup
- Ollama integration
- `/api/chat` basic chat endpoint

### Phase 2
- `/api/documents/upload` endpoint
- Parse PDF/TXT/DOCX
- Chunking
- Embedding generation
- Vector storage in pgvector

### Phase 3
- `/api/rag/ask` endpoint
- Similarity retrieval
- Prompt with retrieved context
- Return answer + source chunks

### Phase 4
- Private vs public routing strategy
- Always retrieve first
- Fallback to general LLM if no relevant chunks

---

## Project structure

```text
src/main/java/com/example/ragchatbot
├── RagChatbotApplication.java
├── controller
│   ├── ChatController.java
│   ├── DocumentController.java
│   └── RagController.java
├── service
│   ├── ChatService.java
│   ├── DocumentParserService.java
│   ├── IngestionService.java
│   ├── RetrievalService.java
│   └── RagService.java
├── model/dto
│   ├── ChatRequest.java
│   ├── ChatResponse.java
│   ├── UploadResponse.java
│   ├── RagAskRequest.java
│   ├── RagAskResponse.java
│   └── SourceDto.java
├── util
    ├── PromptBuilder.java
    └── GeneralPromptBuilder.java

```

---

## How request flow works

### A) Document ingestion flow
1. Upload file (`/api/documents/upload`)
2. Detect file type and extract text
3. Split text into chunks (with overlap)
4. Add metadata (source filename, chunk index)
5. Generate embeddings per chunk
6. Save vectors + metadata into pgvector

### B) RAG Q&A flow
1. Ask question (`/api/rag/ask`)
2. Convert question into embedding (internally)
3. Retrieve semantically similar chunks
4. If chunks exist:
   - build context-grounded prompt
   - call LLM
   - return `PRIVATE_RAG` response + sources
5. Else:
   - call LLM directly
   - return `GENERAL_LLM` response

---

## Local setup guide (step-by-step)

## 1) Prerequisites

Install:

- **Java 21**
- **Maven 3.9+**
- **Docker Desktop**
- **Ollama**

Verify:

```bash
java -version
mvn -version
docker --version
ollama --version
```

## 2) Clone repository

```bash
git clone <your-repo-url>
cd rag-chatbot
```

## 3) Start pgvector database

Ensure `docker-compose.yml` exists in project root, then run:

```bash
docker compose up -d
```

Check container:

```bash
docker ps
```

## 4) Pull Ollama models

```bash
ollama pull llama3.1:8b
ollama pull nomic-embed-text
```

(Optional quick test)
```bash
ollama run llama3.1:8b
```

## 5) Configure application properties

Ensure `src/main/resources/application.yml` has correct values for:

- datasource URL/username/password
- Ollama base URL (`http://localhost:11434`)
- chat and embedding model names
- pgvector settings

## 6) Run application

```bash
mvn spring-boot:run
```

App starts at:
- `http://localhost:8080`

---

## Configuration

Example `application.yml`:

```yaml
server:
  port: 8080

spring:
  application:
    name: rag-chatbot

  datasource:
    url: jdbc:postgresql://localhost:5432/ragdb
    username: raguser
    password: ragpass
    driver-class-name: org.postgresql.Driver

  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama3.1:8b
          temperature: 0.2
      embedding:
        options:
          model: nomic-embed-text

    vectorstore:
      pgvector:
        initialize-schema: true
        index-type: hnsw
        distance-type: cosine_distance
        dimensions: 768
```

---

## How to run

1. Start Docker
2. Start pgvector (`docker compose up -d`)
3. Ensure Ollama is installed and models are pulled
4. Run Spring Boot app (`mvn spring-boot:run`)
5. Test endpoints

---

## API endpoints

### 1) Basic chat
- **POST** `/api/chat`
- Purpose: general LLM response (non-RAG endpoint)

Request:
```json
{
  "question": "Explain Spring dependency injection"
}
```

Response:
```json
{
  "answer": "..."
}
```

---

### 2) Upload document
- **POST** `/api/documents/upload`
- Content-Type: `multipart/form-data`
- Part key: `file`
- Supported: `.pdf`, `.txt`, `.docx`

Response:
```json
{
  "fileName": "handbook.pdf",
  "chunksStored": 12,
  "message": "Document ingested successfully"
}
```

---

### 3) RAG ask
- **POST** `/api/rag/ask`
- Purpose: retrieval-first Q&A with fallback

Request:
```json
{
  "question": "What are platform team responsibilities in my documents?"
}
```

Response (private mode):
```json
{
  "answer": "...",
  "answerMode": "PRIVATE_RAG",
  "sources": [
    {
      "source": "handbook.pdf",
      "chunkIndex": 3,
      "preview": "..."
    }
  ],
  "retrievedChunks": 2
}
```

Response (fallback mode):
```json
{
  "answer": "...",
  "answerMode": "GENERAL_LLM",
  "sources": [],
  "retrievedChunks": 0
}
```

---

## Testing with curl

### Basic chat
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question":"What is Spring Boot?"}'
```

### Upload document
```bash
curl -X POST http://localhost:8080/api/documents/upload \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/absolute/path/to/your-file.pdf"
```

### RAG ask
```bash
curl -X POST http://localhost:8080/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"question":"Summarize onboarding policy from my uploaded docs"}'
```

---

## Common issues and fixes

1. **Connection refused to Ollama**
   - Ensure Ollama is installed and running.
   - Verify `spring.ai.ollama.base-url`.

2. **Model not found**
   - Run `ollama pull llama3.1:8b` and `ollama pull nomic-embed-text`.

3. **Database connection error**
   - Ensure Docker container is running.
   - Check DB credentials in `application.yml`.

4. **No chunks retrieved in RAG**
   - Threshold may be too strict.
   - Document text extraction may be empty.
   - Re-check chunking parameters.

5. **Scanned PDF returns empty text**
   - PDF has no text layer.
   - Add OCR pipeline in future enhancement.

---

## Security considerations

For private/internal documents, consider:

- Authentication and authorization (JWT/OAuth2)
- User/tenant-level document access controls
- Metadata filters during retrieval (`ownerId`, `tenantId`)
- Encryption at rest and in transit
- Audit logs for document upload/access
- Prompt injection defense and output filtering
- PII redaction policies

---

