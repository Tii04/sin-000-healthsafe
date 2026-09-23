# StaffingServiceApp

## Overview

Provides on-call schedules for doctors based on ward and status.

Part of the [HealthSafe](../README.md) project. Independent Maven module, no
parent pom.

MQ: this service publishes to the ActiveMQ topic `staffing-events-topic` — see [`../common/`](../common). Broker URL and topic name come from the common `co.wethinkcode.healthsafe.mq.MqConfig` class alongside it in this module.

REST: calls `ward-service` (`../ward-service`) to validate the ward and
`alert-level-service` (`../alert-level-service`) to read the current Emergency
Status before computing a schedule — see [Integration contracts](../README.md#integration-contracts)
in the root README for the endpoint shapes.

## Assumption Decisions
The specification does not define the staffing calculation. Therefore, this implementation assumes that the number of doctors required increases with the emergency level: levels 0-2 require one doctor, 3-5 two, 6-7 three and level 8 requires four.

## Project structure

```
staffing-service/
├── pom.xml
└── src/main/java/co/wethinkcode/healthsafe/
    ├── StaffingServiceApp.java
    └── mq/
        └── MqConfig.java
```

## Build

```
mvn package
```

## Run

```
java -jar target/staffing-service.jar
```

Listens on port `7033`.

## Test

No automated tests yet. Manually verify it's up:

```
curl http://localhost:7033/health   # -> OK
```

To add real tests, add JUnit 5 + the Surefire plugin to `pom.xml`, put tests under
`src/test/java/co/wethinkcode/healthsafe/`, and run `mvn test`.
