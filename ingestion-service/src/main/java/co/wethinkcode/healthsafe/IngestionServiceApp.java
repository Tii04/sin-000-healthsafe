package co.wethinkcode.healthsafe;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import io.javalin.Javalin;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class IngestionServiceApp {

    public static void readDataLineByLine(String file){
        try {
            FileReader filereader = new FileReader(file);

            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;

            while ((nextRecord = csvReader.readNext()) != null){
                nextRecord[0].strip().toUpperCase();

            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7030);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO: read and clean src/main/resources/wards-outdated.csv (wards, wings, specialist departments data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.

        readDataLineByLine("src/main/resources/wards-outdated.csv");
    }
}
