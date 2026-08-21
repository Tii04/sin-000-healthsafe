package co.wethinkcode.healthsafe;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Ward {
    private String wardId;
    private String wing;
    private String department;
    private int bedsAvailable;

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
            records.get(i)[0] = wardId;
        }
        return records;
    }

    public static List<String[]> cleanWing(List<String[]>records){
        for(int i = 0; i < records.size(); i++){
            String wing = convertTitleCase(records.get(i)[1].strip().replaceAll("\\s+", " "));
            records.get(i)[1] = wing;
        }
        return records;
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

    public static void main(String[] args){
        cleanWardId(readCsv("src/main/resources/wards-outdated.csv"));
    }
}
