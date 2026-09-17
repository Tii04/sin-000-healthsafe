package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestIngestionServiceApp {

    List<String[]> records = IngestionServiceApp.readCsv("src/main/resources/wards-outdated.csv");
    List<Ward> ward = IngestionServiceApp.createWards(records);

    @Test
    public void wardIdCapitalised(){

        assertEquals("W-07", ward.get(6).getWardId());
    }

    @Test
    public void wingStartCased(){

        assertEquals("East Wing", ward.get(4).getWing());
    }

    @Test
    public void departmentStartCased(){

        assertEquals("Cardiology", ward.get(6).getDepartment());
    }

    @Test
    public void normalisationDepartmentOfDifferentSpelling(){
        String misspelledWord = "PAEDIATRICS";
        List<String[]> cleanedRecords = IngestionServiceApp.cleanAll(records);

        cleanedRecords.get(16)[2] = misspelledWord;
        IngestionServiceApp.cleanDepartment(cleanedRecords);
        assertEquals("Pediatrics", cleanedRecords.get(16)[2]);
    }

    @Test
    public void invalidBedAmountReturnsNull(){
        String availableBeds = "-6";

        records.get(11)[3] = availableBeds;

        IngestionServiceApp.cleanBedsAvailable(records);
        assertNull(records.get(11)[3]);
    }
}
