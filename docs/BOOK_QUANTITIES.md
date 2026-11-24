# Book copy invariants

Both write paths require non-negative totals, borrowed and available copies. Borrowed plus available equals the total. Missing IDs return a domain error and cannot create or mutate records. The catalog authorization and CSRF suite remains in CI.

Run `mvn -B -Dtest=LibroPersistenceIT test` with Java 17 and Docker. The test creates a disposable MySQL container and an isolated schema. It verifies persisted quantities, invalid writes, and missing book IDs. It never uses the development database.

These checks apply to service writes. Historical values and manual SQL outside the application are outside this increment.
