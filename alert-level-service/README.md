# AlertLevelServiceApp

## Overview

Tracks the hospital Emergency Status (0-8, 8 = full Code Blue).

Part of the [HealthSafe](../README.md) project. Independent Maven module, no
parent pom.

REST: called by `staffing-service` (`../staffing-service`) to read the current
status when computing on-call schedules — see [Integration contracts](../README.md#integration-contracts)
in the root README for the endpoint shapes.

## Project structure

```
alert-level-service/
.
├── README.md
├── dependency-reduced-pom.xml
├── pom.xml
├── src
│   └── main
│       └── java
│           └── co
│               └── wethinkcode
│                   └── healthsafe
│                       ├── AlertLevel.java
│                       └── AlertLevelServiceApp.java
```

## Build

```
mvn package
```

## Run

```
java -jar target/alert-level-service.jar
```

Listens on port `7032`.

## Test

No automated tests yet. Manually verify it's up:

```
curl http://localhost:7032/health   # -> OK
```

To add real tests, add JUnit 5 + the Surefire plugin to `pom.xml`, put tests under
`src/test/java/co/wethinkcode/healthsafe/`, and run `mvn test`.
