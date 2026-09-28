# Full-Stack E-Commerce Platform

**A complete e-commerce application built with Spring Boot and Angular**

This project implements a working end-to-end e-commerce platform, covering the full flow from authentication and product browsing to cart management, checkout, orders, payments, reviews and wishlists.

The goal of the project was not only to build individual features, but to structure a complete application with a clear separation between frontend, backend, persistence and security.

## Main features

- User registration and authentication
- JWT-based security
- Product catalogue and product details
- Categories
- Shopping cart management
- Checkout flow
- Order creation and order history
- Payments and transactions
- Shipping information
- Product reviews
- Wishlist management
- User profile management
- Administrative user views

## Architecture

The application is split into two main parts:

```text
PSW_Ecommerce/
├── Spring/
│   └── Spring/          # Spring Boot backend
└── frontend/            # Angular frontend
```

The backend follows a layered architecture:

```text
HTTP request
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

The domain model includes entities for users, products, categories, carts, orders, order items, payments, transactions, shipping, reviews and wishlists.

## Backend

The backend is implemented with **Java 17 and Spring Boot 3.4**.

### Technologies

- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Spring Security
- JWT authentication
- OAuth2 / resource-server support
- Bean Validation
- OpenAPI / Swagger
- Spring Boot Actuator
- Maven

The backend exposes dedicated controllers and services for:

- products;
- categories;
- users;
- carts;
- orders;
- payments;
- shipping;
- transactions;
- reviews;
- wishlists.

Security is handled through a dedicated JWT authentication filter and token provider.

## Frontend

The frontend is built with **Angular 19** and TypeScript.

It includes dedicated views for:

- login and registration;
- product listing;
- product details;
- cart;
- checkout;
- order history;
- wishlist;
- user profile;
- user management.

Frontend services encapsulate communication with the backend for authentication, products, carts, orders, users and wishlists.

## Testing and code quality

The backend includes tooling for automated testing and coverage:

- JUnit / Spring Boot Test
- Mockito
- JaCoCo

The project also uses DTOs and a controller/service/repository separation to keep API, business logic and persistence responsibilities distinct.

## Running the project

### Backend

A PostgreSQL instance and the corresponding datasource configuration are required.

```bash
cd Spring/Spring
./mvnw spring-boot:run
```

On Windows:

```powershell
cd Spring\Spring
.\mvnw.cmd spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm start
```

## Why this project matters

This repository demonstrates the engineering side of my work: designing a complete application rather than an isolated model or notebook.

It covers frontend development, REST APIs, authentication, persistence, domain modelling, application layering and testing in a single end-to-end system.

## Author

**Paolo Pangallo**  
M.Sc. Computer Engineering — Artificial Intelligence  
University of Calabria
