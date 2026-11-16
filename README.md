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

Book, author and publisher deletion now use POST forms protected by CSRF and the ADMIN role. The former destructive GET routes are denied. See the [security notes](docs/SECURITY_HARDENING.md) for the access policy.

## Tests and verification

Run the isolated HTTP regression suite without a database:

```text
mvn -B -Dtest=CatalogSecurityWebTest test
```

**Executed locally on 2026-11-16:** JDK 11.0.17, Maven 3.9.0, existing offline dependency cache; 71 tests passed, zero failures/errors/skips, exit code 0. The legacy Java 8 bytecode target and Spring Boot 2.5.5 version are unchanged.

The suite exercises the real security filter chain, MVC controllers and Thymeleaf rendering. Application services and repositories are mocked. It checks anonymous/USER/ADMIN access, CSRF rejection without service calls, rendered form-token round trips, login, registration, logout, denied GET deletion and default-deny routing.

[GitHub Actions](.github/workflows/security-regression.yml) runs that same selected suite with JDK 11. This statement describes the configured workflow; consult its run result for CI evidence.

The original [context-loading test](src/test/java/com/egg/libreria2/Libreria2ApplicationTests.java) remains present. Running unfiltered `mvn test` also selects that test and requires an isolated database/configuration. It was not executed in this change. No full-suite, MySQL integration, browser automation, deployment or coverage percentage is claimed.

## Access policy

- Public GET/HEAD catalog lists, login/registration pages and static assets.
- Registration accepts POST with CSRF and continues to create USER accounts.
- ADMIN is required for catalog creation/editing forms and every catalog write.
- All writes, login and POST logout retain default Spring Security CSRF protection.
- Unlisted requests are denied; internal error dispatches can render the error page.

No default administrator or public role-promotion endpoint is added. An administrator must be provisioned through a separately controlled process; deployment/bootstrap provisioning is not implemented here.

## Known limitations and next changes

- Book quantities still need non-negative and consistency invariants; nullable inputs and missing records need predictable service-level errors.
- Browser-session HTTP rules are tested, but direct service calls do not gain method-level authorization from this change.
- Replace implicit schema updates with migrations, add real MySQL persistence/rollback tests and inspect configuration/history and artifacts for secrets.
- The historical dependency stack still needs a separate supported-version migration and advisory audit.
- This remains a learning application; the changes do not certify production readiness.

See the [security case study](docs/SECURITY_HARDENING.md) and [technical review](docs/TECHNICAL_REVIEW.md).

## Original context

The original README described this as “Interfaz libreria hecha en java 8 Con Springboot” and instructed readers to build and run locally. That scope is preserved; no professional production experience or sole ownership of the whole project is inferred.
