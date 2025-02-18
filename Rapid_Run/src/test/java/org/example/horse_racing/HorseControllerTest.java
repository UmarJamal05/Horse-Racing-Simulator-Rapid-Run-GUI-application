package org.example.horse_racing;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class HorseControllerTest {

    private HorseController horseController;
    private ObservableList<Map<String, String>> horses;

    @BeforeEach
    public void setUp() {
        horseController = new HorseController();
        horses = FXCollections.observableArrayList();
        horseController.setHorses(horses);
    }

    @Test
    public void testAddHorse_ValidInput_HorseAdded() {

        String id = "123";
        String name = "Silver";
        String jockey = "Peter";
        String age = "5";
        String breed = "Thoroughbred";
        String raceRecord = "5 wins, 0 losses";
        String group = "Group A";
        int seconds = 10;

        horseController.addHorse(id, name, jockey, age, breed, raceRecord, group, seconds);

        assertTrue(horses.contains(Map.of("id", id, "name", name, "jockey", jockey, "age", age, "breed", breed, "race_record", raceRecord, "group", group, "seconds", String.valueOf(seconds))));
    }
}