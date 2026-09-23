package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AlertLevelServiceApp {

    public static void main(String[] args) {
        AlertLevel currentAlertLevel = new AlertLevel();
        ObjectMapper mapper = new ObjectMapper();

        Javalin app = Javalin.create().start(7032);

        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/alert-level", ctx -> {
            if (currentAlertLevel.getLevel() < 0 || currentAlertLevel.getLevel() > 8){
                ctx.status(400);
                return;
            }
            ctx.json(currentAlertLevel);
        });
        app.put("/alert-level", ctx -> {
           AlertLevel requestedLevel = mapper.readValue(ctx.body(), AlertLevel.class);

           if(requestedLevel.getLevel() < 0 || requestedLevel.getLevel() > 8){
               ctx.status(400);
               return;
           }
           currentAlertLevel.setLevel(requestedLevel.getLevel());
           ctx.json(currentAlertLevel);
        });


        // TODO (Tracks the hospital Emergency Status (0-8, 8 = full Code Blue).)
        // Add domain endpoints for alert-level-service here.
    }
}
