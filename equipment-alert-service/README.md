# EquipmentAlertServiceApp

## Overview

Uses a Queue to guarantee delivery of critical medical equipment failure alerts.

Part of the [HealthSafe](../README.md) project — its alerting service.
Independent Maven module, no parent pom.

Mechanism: ActiveMQ Queue (guaranteed delivery)

Queue: `equipment-failure-queue`

- Producer: `ward-service` (`../ward-service`) — publishes when it detects an
  equipment failure on one of its wards.
- Consumer: this service.

Broker URL and queue name are shared via a common `co.wethinkcode.healthsafe.mq.MqConfig`
class (`BROKER_URL`, `QUEUE`). It's identical in every participating service's own
source tree — each service here is an independent Maven project with no shared
parent pom, so the common package is duplicated rather than imported from one place.

A queue (not a topic) is used deliberately: each failure alert must be delivered to
exactly one consumer and processed at least once, even if this service is briefly
down — unlike the broadcast `staffing-events-topic` in [`../common/`](../common),
where every subscriber gets every message.

## Assumptions/ Tradeoffs
#### Equipment Failure Event Assumption
The specification does not define a structure for the equipment failure alert. Therefore, the following structure is assumed for the equipment failure event: the ward ID, the equipment associated with the failure, the current status of the equipment, the type of failure, and the timestamp indicating when the failure occurred.


## Project structure

```
equipment-alert-service/
├── README.md
├── dependency-reduced-pom.xml
├── pom.xml
├── src
│   └── main
│       └── java
│           └── co
│               └── wethinkcode
│                   └── healthsafe
│                       ├── EquipmentAlertServiceApp.java
│                       ├── EquipmentFailure.java
│                       └── mq
│                           ├── EquipmentFailureConsumer.java
│                           ├── EquipmentFailurePublisher.java
│                           └── MqConfig.java
```

## Build

```
mvn package
```

## Run

```
java -jar target/equipment-alert-service.jar
```

Listens on port `7034`.

## Test

No automated tests yet. Manually verify it's up:

```
curl http://localhost:7034/health   # -> OK
```

To add real tests, add JUnit 5 + the Surefire plugin to `pom.xml`, put tests under
`src/test/java/co/wethinkcode/healthsafe/`, and run `mvn test`.
