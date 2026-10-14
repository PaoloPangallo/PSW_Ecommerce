# 🛒 E-Commerce — portfolio overview

This project demonstrates the engineering of a complete web application: frontend, backend, persistence and security. It is not an AI product, and that distinction is intentional — it complements the AI-focused repositories with software engineering fundamentals.

## Architecture

```mermaid
flowchart LR
    UI["Angular 19<br/>TypeScript UI"] --> API["Spring Boot 3.4<br/>REST controllers"]
    API --> S["Services<br/>Business logic"]
    S --> R["Spring Data JPA<br/>Repositories"]
    R --> DB[("PostgreSQL")]
    API --> AUTH["Spring Security<br/>JWT"]
    style UI fill:#ffe4eb,stroke:#d34b75,color:#522438
    style API fill:#e5f9eb,stroke:#5baf6f,color:#25482c
    style S fill:#e8e7ff,stroke:#7f78cc,color:#2f2b50
    style R fill:#e2f7ff,stroke:#3695b7,color:#244756
    style DB fill:#e4ecff,stroke:#4f78c9,color:#223b63
    style AUTH fill:#fff2db,stroke:#d69c37,color:#523b1c
```

## Capabilities

- Product catalogue, categories and product details
- User registration and authentication
- Shopping carts, checkout, orders and order history
- Payment/transaction and shipping-related domain functionality
- Reviews, wishlists and user/admin views

The diagram is an overview. Security properties and payment integrations need their own review before any production use.

## Read the implementation

- [Backend](../Spring/Spring/) — Java 17, Spring Boot 3.4, JPA, Security and REST controllers
- [Frontend](../frontend/) — Angular 19 and TypeScript
- [Setup and testing](../README.md#running-the-project) — environment, commands and test tooling

## Quality checks

The [portfolio CI workflow](../.github/workflows/portfolio-ci.yml) compiles the Java backend and builds the Angular frontend. It does not run database-backed end-to-end tests or certify security correctness.
