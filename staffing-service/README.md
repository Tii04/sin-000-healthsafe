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

## Assumption / Tradeoffs
The specification does not define the staffing calculation. Therefore, this implementation assumes that the number of doctors required increases with the emergency level: levels 0-2 require one doctor, 3-5 two, 6-7 three and level 8 requires four.

#### Staffing Schedule Updates
The publication of staffing schedule events is triggered when the /staffing/{id} endpoint is called. When a request is received, the Staffing Service retrieves the current alert level, recalculates the staffing schedule, and compares it with the previously recorded schedule. An event is published to the ActiveMQ topic only if the schedule has changed, or if no previous schedule exists for that ward.

The trade-off is that schedule changes are not detected or published immediately when the underlying alert level changes. Instead, changes are detected the next time the relevant staffing endpoint is called. This approach avoids the additional complexity of scheduled polling or event-driven notifications from the Alert Level Service, at the cost of potentially delayed schedule updates.

## Project structure

```
staffing-service/
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
│                       ├── StaffingSchedule.java
│                       ├── StaffingServiceApp.java
│                       ├── Ward.java
│                       ├── healthsafe.iml
│                       └── mq
│                           ├── MqConfig.java
│                           └── StaffingEventPublisher.java
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
