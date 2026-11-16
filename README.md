# Java Library — legacy Spring MVC application

A library catalog application built with Java 8, Spring Boot and server-rendered Thymeleaf pages. It records books, authors and publishers and includes user registration and form-login code.

**Status:** historical learning project. The package structure and original README place it in an educational context; individual authorship and course requirements have not been independently established. This repository is retained as evidence of earlier Java/Spring practice. It is not a production-ready service.

[Resumen en español](README.es.md) · [Technical review](docs/TECHNICAL_REVIEW.md)

## Implemented structure

| Area | Source |
| --- | --- |
| MVC pages and form handlers | [controllers](src/main/java/com/egg/libreria2/controller) |
| Book, author, publisher and user operations | [services](src/main/java/com/egg/libreria2/services) |
| JPA entities and repositories | [entities](src/main/java/com/egg/libreria2/entidades), [repositories](src/main/java/com/egg/libreria2/repositorios) |
| Form login and BCrypt configuration | [Security.java](src/main/java/com/egg/libreria2/Security.java) |
| Server-rendered views | [templates](src/main/resources/templates) |

The application uses a layered structure within one Spring Boot process. It is not a microservice architecture or a versioned REST API.

## Historical stack

The [Maven manifest](pom.xml) declares Java 8 and Spring Boot 2.5.5, with Spring MVC, Spring Data JPA, Spring Security, Thymeleaf, MySQL Connector/J and Spring Boot Test. These are the checked-in versions, not a current upgrade recommendation.

## Local evaluation

Use a disposable local MySQL database and non-production credentials. Do not expose the application to the Internet before the security work below is verified.

1. Install a compatible JDK and Maven; the repository also contains Maven wrapper scripts.
2. Create an empty local database and a user scoped to that database.
3. Override the checked-in datasource settings through environment variables. In PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:mysql://127.0.0.1:3306/library_demo'
$env:SPRING_DATASOURCE_USERNAME = 'library_demo'
# Set SPRING_DATASOURCE_PASSWORD privately in your shell.
mvn spring-boot:run
```

4. Open <http://localhost:8080/>. The application includes a login page at `/login`.

These startup instructions are derived from the source configuration and have **not** been validated against a running MySQL instance in this documentation pass. Hibernate is configured with `ddl-auto: update`; it can modify the selected schema. There are no versioned Flyway migrations.

## Browser routes

These are MVC routes returning views or redirects, not a documented JSON API:

| Method | Route | Purpose in source |
| --- | --- | --- |
| GET | `/` | List books |
| GET | `/nuevoLibro` | Book creation form |
| POST | `/cargar` | Create a book |
| POST | `/save` | Save book edits |
| GET | `/listaDeAutores` | List authors |
| GET | `/lista-editoriales` | List publishers |

The legacy `GET /disponibilidad-libro` handler calls a deletion operation. It is intentionally not presented as a safe API example.

## Tests and verification

The repository contains one [Spring context-loading test](src/test/java/com/egg/libreria2/Libreria2ApplicationTests.java). With a disposable database configured, the intended command is:

```text
mvn test
```

This documentation change did not execute that test, a build, browser flows or database integration tests. No passing CI, coverage percentage or deployment is claimed.

## Known limitations and next changes

- CSRF is disabled and the HTTP configuration lacks a comprehensive explicit authorization policy.
- A GET request can mutate data. Replace it with an authorized state-changing operation and enable CSRF protection for browser forms.
- Add authorization tests for anonymous users and roles before any public demo.
- Book quantities need explicit non-negative and consistency invariants; nullable inputs and missing records need predictable errors.
- Replace implicit schema updates with migrations; audit dependencies and configuration/history without publishing secrets.
- Add isolated service and persistence tests before claiming reproducibility.

See the [review and interview notes](docs/TECHNICAL_REVIEW.md) for evidence and a small proposed remediation sequence.

## Original context

The original README described this as “Interfaz libreria hecha en java 8 Con Springboot” and instructed readers to build and run locally. That scope is preserved; no professional production experience or sole ownership of the whole project is inferred.
