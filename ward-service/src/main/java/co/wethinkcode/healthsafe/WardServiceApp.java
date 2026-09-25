package co.wethinkcode.healthsafe;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.wethinkcode.healthsafe.mq.EquipmentFailurePublisher;
import io.javalin.Javalin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class WardServiceApp {
    private static final ObjectMapper mapper = new ObjectMapper();

    public WardServiceApp() throws IOException{
    }

    public static List<Ward> getWardsFromIngestionService() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:7030/wards"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Ward> wards = mapper.readValue(response.body(), new TypeReference<>() {
            });
            return wards;
        } catch (IOException | InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private static boolean isBlank(String value){
        return value == null || value.trim().isEmpty();
    }

    public static void main(String[] args) throws Exception {
        EquipmentFailurePublisher publisher = new EquipmentFailurePublisher();
        Javalin app = Javalin.create().start(7031);

        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/wards", ctx -> {
            List<Ward> wards = getWardsFromIngestionService();
            ctx.json(wards);
        });
        app.get("/wards/{id}", ctx -> {
            List<Ward> wards = getWardsFromIngestionService();
            String wardId = ctx.pathParam("id");
            wards.stream().filter(ward -> ward.getWardId().equalsIgnoreCase(wardId)).findFirst().ifPresentOrElse(ctx ::json, () -> ctx.status(404));
        });
        app.get("/departments", ctx -> {
            List<Ward> wards = getWardsFromIngestionService();
            List<String> departments = wards.stream().map(Ward::getDepartment).distinct().toList();
            ctx.json(departments);
        });
        app.post("/equipment-failures", ctx -> {
            EquipmentFailure request = mapper.readValue(ctx.body(), EquipmentFailure.class);
            List<Ward> wards = getWardsFromIngestionService();
            boolean wardExists;

            if (isBlank(request.getWardId())
            || isBlank(request.getEquipment())
            || isBlank(request.getStatus())
            || isBlank(request.getFailureType())) {
                ctx.status(400);
                ctx.result("Required fields must not be blank");
                return;
            }

            if (!request.getStatus().equalsIgnoreCase("FAILED")) {
                ctx.status(400);
                ctx.result("Equipment failure status must be FAILED");
                return;
            }
            wardExists = wards.stream().anyMatch(ward -> ward.getWardId().equalsIgnoreCase(request.getWardId()));

            if (!wardExists){
                ctx.status(404);
                ctx.result("Ward does not exist.");
                return;
            }

            EquipmentFailure failure = new EquipmentFailure(
                request.getWardId(),
                request.getEquipment(),
                request.getStatus(),
                request.getFailureType(),
                OffsetDateTime.now()
            );
            publisher.publish(failure);
            ctx.status(202);
            ctx.json(failure);
        });

        // TODO (Provides lists of wards and departments.)
        // Add domain endpoints for ward-service here.
    }
}

// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// MQ TODO: publishes to ActiveMQ queue MqConfig.QUEUE when it detects an equipment failure on one of its wards.
