package org.example.horse_racing;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {
    public void welcome(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HorseApplication.class.getResource("HorseRacingSimulator.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent)fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}