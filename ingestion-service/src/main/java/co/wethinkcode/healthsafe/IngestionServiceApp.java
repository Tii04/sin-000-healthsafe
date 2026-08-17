package co.wethinkcode.healthsafe;

import com.opencsv.CSVReader;
import io.javalin.Javalin;

import java.io.FileReader;

public class IngestionServiceApp {
    public static void readDataLineByLine(String file)
    {

        try {

            // Create an object of filereader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(file);

            // create csvReader object passing
            // file reader as a parameter
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;

            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                nextRecord[0] = nextRecord[0].toUpperCase();
                System.out.println(nextRecord[0]);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
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
