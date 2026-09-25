package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.wethinkcode.healthsafe.mq.StaffingEventPublisher;
import io.javalin.Javalin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StaffingServiceApp {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, StaffingSchedule> previousSchedules = new HashMap<>();
    private static final StaffingEventPublisher publisher;

    static {
        try {
            publisher = new StaffingEventPublisher();
        } catch (Exception e){
            throw new RuntimeException("Failed to initialise StaffingEventPublisher", e);
        }
    }

    public static Optional<Ward> getWardFromWardService(String id){

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:7031/wards/" + id))
                .GET()
                .build();
        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Ward ward = MAPPER.readValue(response.body(), Ward.class);
                return Optional.of(ward);
            }
            if (response.statusCode() == 404) {
                return Optional.empty();
            }

            throw new RuntimeException(
                    "Ward service returned status: " + response.statusCode()
            );
        } catch (IOException | InterruptedException e){
            throw new RuntimeException("Could not communicate with ward service", e);
        }
    }

    public static AlertLevel getAlertLevelFromAlertLevelService(){

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:7032/alert-level"))
                .GET()
                .build();
        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200){
                throw new RuntimeException("Alert level service returned status: " + response.statusCode());
            }
            return MAPPER.readValue(response.body(), AlertLevel.class);
        } catch (IOException | InterruptedException e){
            throw new RuntimeException("Could not communicate with alert level service ", e);
        }
    }

    public static StaffingSchedule calculateSchedule(Ward ward, AlertLevel alertLevel){
        int level = alertLevel.getLevel();
        int doctorsRequired;

        if (level <= 2) doctorsRequired = 1;
        else if (level <= 5) {
            doctorsRequired = 2;
        } else if (level <= 7) {
            doctorsRequired = 3;
        } else {
            doctorsRequired = 4;
        }

        return new StaffingSchedule(ward.getWardId(), ward.getDepartment(), level, doctorsRequired);
    }

    public static void compareSchedules(StaffingSchedule prevSchedule, StaffingSchedule currSchedule) throws Exception{
        if (prevSchedule == null){
            publisher.publish(currSchedule);
        } else if (!prevSchedule.equals(currSchedule)){
            publisher.publish(currSchedule);
        }
        
        previousSchedules.put(currSchedule.getWardId(), currSchedule);
    }

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7033);
        

        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/staffing/{id}", ctx -> {
            String wardId = ctx.pathParam("id");
            Optional<Ward> ward = getWardFromWardService(wardId);

            if (ward.isEmpty()){
                ctx.status(404);
                return;
            }

            AlertLevel alertLevel = getAlertLevelFromAlertLevelService();
            StaffingSchedule currentSchedule = calculateSchedule(ward.get(), alertLevel);
            StaffingSchedule prevSchedule = previousSchedules.get(currentSchedule.getWardId());

            compareSchedules(prevSchedule, currentSchedule);
            ctx.json(currentSchedule);
        });


        // TODO (Provides on-call schedules for doctors based on ward and status.)
        // Add domain endpoints for staffing-service here.
    }
}

// MQ TODO: publishes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
