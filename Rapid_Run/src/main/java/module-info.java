module org.example.horse_racing {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.horse_racing to javafx.fxml;
    exports org.example.horse_racing;
}