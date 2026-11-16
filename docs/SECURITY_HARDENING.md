# Browser security maintenance

## Problem and implemented behavior

The original application disabled CSRF and exposed book, author and publisher deletion through GET handlers. Several catalog routes lacked an explicit authorization boundary.

The maintenance keeps public catalog reading and registration, reserves catalog management for ADMIN and denies unlisted requests. New registrations remain USER accounts through the existing service; this change adds no public administrator provisioning or elevation path.

| Surface | Access | CSRF |
| --- | --- | --- |
| GET/HEAD `/`, `/listaDeAutores`, `/lista-editoriales` | Public read-only catalog | No state mutation |
| GET/HEAD login, registration and static assets | Public | No state mutation |
| POST `/registro` and `/logincheck` | Public form submission | Required |
| Six catalog create/edit form routes | ADMIN | Forms emit token |
| Nine catalog POST mutation routes | ADMIN | Required |
| POST `/logout` | Session logout through Spring Security | Required |
| Former destructive GET routes and unlisted application routes | Denied | No service mutation |
| Servlet ERROR dispatch | Error rendering allowed | Does not add a normal public write route |

The complete allowlist is in [Security.java](../src/main/java/com/egg/libreria2/Security.java). Deletion URLs keep their names but require POST. Their old links are replaced with forms. Creation forms with plain actions now use Thymeleaf `th:action`, allowing Spring's request-data processor to emit the CSRF field. The logout link is also a POST form.

Role-based template visibility helps the UI match the policy; server-side rules enforce authorization even when a caller constructs a request manually.

## Verification

Command:

```text
mvn -B -Dtest=CatalogSecurityWebTest test
```

Local executed result, 2026-11-16: **71 tests, zero failures/errors/skips**, exit 0, JDK 11.0.17 and Maven 3.9.0, dependencies from an existing offline cache. The unchanged baseline failed the same regression suite before the fix.

The [MockMvc suite](../src/test/java/com/egg/libreria2/CatalogSecurityWebTest.java) uses the actual Spring Security chain, controllers and Thymeleaf templates; services and repositories are mocks. Negative requests assert that services/repositories were not invoked. Browser-form tests extract the rendered hidden token and submit it with the same session; login authenticates through BCrypt and the filter chain against a fake user-details response.

The [CI workflow](../.github/workflows/security-regression.yml) selects that same suite. It supplies an unusable loopback database URL as an additional guard and never starts the application with live providers.

## Limits and tradeoffs

- This is session/form security; JWT is not introduced.
- Public catalog reads preserve the existing browsing use case. Writes become ADMIN-only intentionally.
- No service-level authorization, ownership model, stock-invariant correction or administrator bootstrap was added.
- No database, actual account creation, real servlet-container/browser session, full context test, migration, dependency clearance or deployment is certified.
- Existing form/controller names and most application structure are preserved to keep the change reviewable.
- Legacy dependency versions and other findings remain open.

## Interview presentation

“The maintenance replaced unsafe browser writes with an explicit policy and verified it through real filters and rendered forms. It preserves the original learning application and documents where mocked persistence limits the evidence.”

Discuss why CSRF does not replace role authorization, why hiding a button is insufficient, why GET must be safe, and what separate database tests would be needed.

Reference: [Spring Security 5.5.2 CSRF documentation](https://docs.spring.io/spring-security/site/docs/5.5.2/reference/html5/#servlet-exploits-csrf). The project retains its historical dependency baseline.
