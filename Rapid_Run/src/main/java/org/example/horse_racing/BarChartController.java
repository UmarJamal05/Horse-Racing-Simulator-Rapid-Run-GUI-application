package org.example.horse_racing;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.util.List;
import java.util.Map;

public class BarChartController {
    @FXML
    private BarChart<String, Number> barChart;

    public void setData(List<Map<String, String>> winningHorses) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Race Time");

        for (int i = 0; i < 3 && i < winningHorses.size(); i++) {
            Map<String, String> horse = winningHorses.get(i);
            int raceTime = Integer.parseInt(horse.get("race_time"));
            String horseName = horse.get("name");
            series.getData().add(new XYChart.Data<>(horseName, raceTime));
        }

        barChart.getData().add(series);
    }
}