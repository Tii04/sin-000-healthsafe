package co.wethinkcode.healthsafe;

import com.opencsv.CSVReader;
import io.javalin.Javalin;

import java.io.FileReader;
import java.util.*;

public class IngestionServiceApp {

    public static List<String[]> readCsv(String file){
        List<String[]> readRecords = new ArrayList<>();

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
                for (int i = 0; i < nextRecord.length; i++) {
                    nextRecord[i] = nextRecord[i].strip();
                }
                readRecords.add(nextRecord);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return readRecords;
    }

    public static List<String[]> cleanWardId(List<String[]> records){
        for(int i = 1; i <= records.size() -1; i++){
            String wardId = records.get(i)[0].strip().toUpperCase();
            records.get(i)[0] = cleanMissingValues(wardId);
        }
        return records;
    }

    public static List<String[]> cleanWing(List<String[]>records){
        for(int i = 1; i <= records.size() -1; i++){
            String wing = convertTitleCase(records.get(i)[1].strip().replaceAll("\\s+", " "));
            records.get(i)[1] = cleanMissingValues(wing);
        }
        return records;
    }
    public static List<String[]> cleanDepartment(List<String[]>records){
        for (int i = 1; i <= records.size() -1; i++) {
            String department = convertTitleCase(records.get(i)[2].strip().replaceAll("\\s+", " "));
            records.get(i)[2] = cleanMissingValues(department);
        }
        return records;
    }

    public static List<String[]> cleanBedsAvailable(List<String[]> records){
        for (int i = 1; i <= records.size() -1; i++) {
            String bedsAvailable = String.valueOf(records.get(i)[3].strip().replaceAll("\\s+", " "));
            Integer cleanedBeds = cleanBedsAvailable(bedsAvailable);
            records.get(i)[3] = cleanedBeds == null ? null : String.valueOf(cleanedBeds);
        }
        return records;
    }

    private static String cleanMissingValues(String recordValue){
        if (recordValue == null) {
            return null;
        }

        String value = recordValue.strip();

        if (value.isEmpty()
                || value.equalsIgnoreCase("N/A")
                || value.equalsIgnoreCase("TBD")
                || value.equals("-")
                || value.equalsIgnoreCase("NaN")
                || value.equalsIgnoreCase("unknown")) {
            return null;
        }
        return value;
    }

    private static Integer cleanBedsAvailable(String recordValue) {
        String value = cleanMissingValues(recordValue);

        if (value == null) {
            return null;
        }
        if (value.equalsIgnoreCase("full")) {
            return 0;
        }

        try {
            int beds = Integer.parseInt(value);

            if (beds < 0 || beds > 1000) {
                return null;
            }
            return beds;

        } catch (NumberFormatException e) {
            return null;
        }
    }
    private static String convertTitleCase(String text){
        if (text == null || text.isEmpty()) return text;

        StringBuilder converted = new StringBuilder();
        boolean convertNext = true;
        for (char ch : text.toCharArray()){
            if (Character.isSpaceChar(ch)) convertNext = true;
            else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }
        return converted.toString();
    }

    public static List<String[]> handleDuplicates(List<String[]> records){
        Set<String> seenWardIds = new HashSet<>();
        List<String[]> uniqueRecords = new ArrayList<>();

        for (String[] record : records){
            String wardId = record[0];

            if (seenWardIds.contains(wardId)){
                continue;
            }
            seenWardIds.add(wardId);
            uniqueRecords.add(record);
        }
        return uniqueRecords;
    }
    public static List<Ward> createWards(List<String[]> records){
        List<Ward> wards = new ArrayList<>();
        for (int i = 1; i <= records.size() -1; i++){
            String wardId = records.get(i)[0];
            String wing = records.get(i)[1];
            String department = records.get(i)[2];
            Integer bedsAvailable = records.get(i)[3] == null ? null : Integer.valueOf(records.get(i)[3]);

            Ward ward = new Ward(wardId, wing, department, bedsAvailable);

            wards.add(ward);
        }
        return wards;
    }

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7030);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO: read and clean src/main/resources/wards-outdated.csv (wards, wings, specialist departments data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.
        List<String[]> records = readCsv("src/main/resources/wards-outdated.csv");

        records = cleanWardId(records);
        records = cleanWing(records);
        records = cleanDepartment(records);
        records = cleanBedsAvailable(records);
        records = handleDuplicates(records);

        List<Ward> wards = createWards(records);

        for (Ward ward : wards){
            System.out.println(ward.toString());
        }

    }
}
