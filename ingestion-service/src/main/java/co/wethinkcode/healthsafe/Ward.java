package co.wethinkcode.healthsafe;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.*;

public class Ward {
    private String wardId;
    private String wing;
    private String department;
    private Integer bedsAvailable;

    public Ward(String wardId, String wing, String department, Integer bedsAvailable){
        this.wardId = wardId;
        this.wing = wing;
        this.department = department;
        this.bedsAvailable = bedsAvailable;
    }

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
        for(int i = 0; i < records.size(); i++){
            String wardId = records.get(i)[0].strip().toUpperCase();
            records.get(i)[0] = cleanMissingValues(wardId);
        }
        return records;
    }

    public static List<String[]> cleanWing(List<String[]>records){
        for(int i = 0; i < records.size(); i++){
            String wing = convertTitleCase(records.get(i)[1].strip().replaceAll("\\s+", " "));
            records.get(i)[1] = cleanMissingValues(wing);
        }
        return records;
    }
     public static List<String[]> cleanDepartment(List<String[]>records){
         for (String[] record : records) {
             String department = convertTitleCase(record[2].strip().replaceAll("\\s+", " "));
             record[2] = cleanMissingValues(department);
         }
         return records;
     }

     public static List<String[]> cleanBedsAvailable(List<String[]> records){
         for (String[] record : records) {
             String bedsAvailable = String.valueOf(record[3].strip().replaceAll("\\s+", " "));
             record[3] = String.valueOf(cleanBedsAvailable(bedsAvailable));
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

    public static void main(String[] args){
        List<String[]> records = readCsv("src/main/resources/wards-outdated.csv");

        records = cleanWardId(records);
        records = cleanWing(records);
        records = cleanDepartment(records);
        records = cleanBedsAvailable(records);
        records = handleDuplicates(records);

        for (String[] record : records){
            System.out.println(Arrays.toString(record));
        }
    }
}
