package co.wethinkcode.healthsafe;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class WardServiceApp {

    public WardServiceApp() throws IOException{
    }

    public static List<Ward> getWardsFromIngestionService() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

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

    public static void main(String[] args) throws IOException, InterruptedException {
        Javalin app = Javalin.create().start(7031);

        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/wards", ctx -> {
            List<Ward> wards = getWardsFromIngestionService();
            ctx.json(wards);
        });
        app.get("/ward/{id}", ctx -> {
            List<Ward> wards = getWardsFromIngestionService();
            String wardId = ctx.pathParam("id");
            wards.stream().filter(ward -> ward.getWardId().equalsIgnoreCase(wardId)).findFirst().ifPresentOrElse(ctx ::json, () -> ctx.status(404));
        });
        app.get("/departments", ctx -> {
            List<Ward> wards = getWardsFromIngestionService();
            List<String> departments = wards.stream().map(Ward::getDepartment).distinct().toList();
            ctx.json(departments);
        });

        // TODO (Provides lists of wards and departments.)
        // Add domain endpoints for ward-service here.
    }
}

// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// MQ TODO: publishes to ActiveMQ queue MqConfig.QUEUE when it detects an equipment failure on one of its wards.
