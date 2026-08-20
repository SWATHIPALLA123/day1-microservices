# Microservices Learning

This repository contains my daily learning work and POC implementations related to Microservices and Spring Boot.

## Daily Progress

### Day 1 – User & Order Services
- Created User Service
- Created Order Service
- Implemented REST APIs
- Configured service ports
- Tested APIs using Postman

### Day 2 – Microservices Communication
- Worked on communication between Order Service and User Service
- Configured User Service base URL
- Tested service-to-service communication
- Used configuration properties for service URLs

### Day 3 – API Gateway
- Created a separate API Gateway using Spring Cloud Gateway
- Configured Gateway on port `8080`
- Configured User Service route:
  - `/api/users/**` → User Service `8081`
- Configured Order Service route:
  - `/api/orders/**` → Order Service `8082`
- Tested User Service through Gateway using Postman
- Tested Order Service through Gateway
- Learned Service Discovery concepts
- Learned configuration management
- Understood microservice data ownership
- Designed Client → Gateway → Services architecture

## Technologies Used

- Java 21
- Spring Boot
- Spring Cloud Gateway
- Maven
- REST APIs
- Postman
- Git & GitHub

## Architecture

```text
                 Client / Postman
                        |
                        v
                API Gateway :8080
                   /          \
                  v            v
          User Service     Order Service
             :8081             :8082
