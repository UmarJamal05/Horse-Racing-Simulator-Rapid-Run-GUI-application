package org.example.horse_racing;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;

public class HorseController {
    @FXML
    private TableView<Map<String, String>> horseTableView;
    @FXML
    private TextField idField, nameField, jockeyField, ageField, breedField, raceRecordField, groupField;
    @FXML
    private TextArea outputTextArea;
    @FXML
    private RadioButton groupARadioButton, groupBRadioButton, groupCRadioButton, groupDRadioButton;
    @FXML
    private Button addButton, updateButton, deleteButton, viewButton, saveButton, selectButton, simulateButton, visualizeButton;
    @FXML
    private VBox addUpdateVBox;
    @FXML
    private HBox buttonsHBox;
    @FXML
    private ImageView imageView;
    @FXML
    private Button browseButton;
    @FXML
    private TabPane tabPane;
    @FXML
    private ToggleGroup groupToggleGroup;
    @FXML
    private Button exitButton;

    @FXML
    private void handleExitButtonAction(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private ObservableList<Map<String, String>> horses = FXCollections.observableArrayList();

    public void setHorses(ObservableList<Map<String, String>> horses) {
        this.horses = horses;
    }
    public void addHorse(String id, String name, String jockey, String age, String breed, String raceRecord, String group, int seconds) {
        Map<String, String> horse = Map.of("id", id, "name", name, "jockey", jockey, "age", age, "breed", breed, "race_record", raceRecord, "group", group, "seconds", String.valueOf(seconds));
        horses.add(horse);
    }

    private List<Map<String, String>> winningHorses = new ArrayList<>();
    private Map<String, Map<String, String>> selectedHorses = new HashMap<>();
    private boolean raceSimulated = false;

    public void initialize() {
        groupToggleGroup = new ToggleGroup();

        // The ToggleGroup is linked with RadioButtons
        groupARadioButton.setToggleGroup(groupToggleGroup);
        groupBRadioButton.setToggleGroup(groupToggleGroup);
        groupCRadioButton.setToggleGroup(groupToggleGroup);
        groupDRadioButton.setToggleGroup(groupToggleGroup);

        horseTableView.setItems(horses);

        // The items of horseTableView are set to the horses list

        // The ID column
        TableColumn<Map<String, String>, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("id")));
        horseTableView.getColumns().add(idColumn);

        // The Name column
        TableColumn<Map<String, String>, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("name")));
        horseTableView.getColumns().add(nameColumn);

        // The Jockey column
        TableColumn<Map<String, String>, String> jockeyColumn = new TableColumn<>("Jockey");
        jockeyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("jockey")));
        horseTableView.getColumns().add(jockeyColumn);

        // The Age column
        TableColumn<Map<String, String>, String> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("age")));
        horseTableView.getColumns().add(ageColumn);

        // The Breed column
        TableColumn<Map<String, String>, String> breedColumn = new TableColumn<>("Breed");
        breedColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("breed")));
        horseTableView.getColumns().add(breedColumn);

        // The Race record column
        TableColumn<Map<String, String>, String> raceRecordColumn = new TableColumn<>("Race Record");
        raceRecordColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("race_record")));
        horseTableView.getColumns().add(raceRecordColumn);

        // The Group column
        TableColumn<Map<String, String>, String> groupColumn = new TableColumn<>("Group");
        groupColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("group")));
        horseTableView.getColumns().add(groupColumn);

        // The Seconds column
        TableColumn<Map<String, String>, String> secondsColumn = new TableColumn<>("Seconds");
        secondsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("seconds")));
        horseTableView.getColumns().add(secondsColumn);

        // The Image column
        TableColumn<Map<String, String>, ImageView> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(cellData -> {
            String imagePath = cellData.getValue().get("image");
            ImageView thumbnailView = new ImageView();
            thumbnailView.setFitWidth(50);
            thumbnailView.setFitHeight(50);
            if (!imagePath.isEmpty()) {
                Image image = new Image(imagePath, 50, 50, true, true);
                thumbnailView.setImage(image);
            }
            return new SimpleObjectProperty<>(thumbnailView);
        });
        horseTableView.getColumns().add(imageColumn);

        // Action event handlers are set for buttons
        addButton.setOnAction(event -> addHorse());
        updateButton.setOnAction(event -> updateHorse());
        deleteButton.setOnAction(event -> deleteHorse());
        viewButton.setOnAction(event -> viewHorses());
        saveButton.setOnAction(event -> saveToTextFile());
        selectButton.setOnAction(event -> selectFourHorses());
        simulateButton.setOnAction(event -> simulateRace());
        visualizeButton.setOnAction(event -> visualizeWinningHorses());
        exitButton.setOnAction(this::handleExitButtonAction);
    }

    private void clearAddUpdateFields() {
        idField.clear();
        nameField.clear();
        jockeyField.clear();
        ageField.clear();
        breedField.clear();
        raceRecordField.clear();
        groupToggleGroup.selectToggle(null);
        imageView.setImage(null);
    }

    private void addHorse() {
        try {
            // Input values are got from text fields
            String id = idField.getText();
            String name = nameField.getText();
            String jockey = jockeyField.getText();
            String age = ageField.getText();
            String breed = breedField.getText();
            String raceRecord = raceRecordField.getText();
            String group = "";

            if (id.isEmpty() || name.isEmpty() || jockey.isEmpty() || age.isEmpty() || breed.isEmpty() || raceRecord.isEmpty()) {
                outputTextArea.setText("Please fill in all fields.");
                return;
            }

            if (!id.matches("\\d+") || Integer.parseInt(id) <= 0) {
                outputTextArea.setText("Please enter a valid positive integer for the ID.");
                return;
            }

            if (!age.matches("\\d+") || Integer.parseInt(age) < 1 || Integer.parseInt(age) > 30) {
                outputTextArea.setText("Invalid age. Please enter a valid positive integer for age (1-30). Because a horse may live usually upto 30 years.");
                return;
            }

            // Get selected group from radio buttons
            if (groupToggleGroup.getSelectedToggle() != null) {
                group = ((RadioButton) groupToggleGroup.getSelectedToggle()).getText();
            } else {
                outputTextArea.setText("Please select a group.");
                return;
            }

            if (horses.stream().anyMatch(horse -> horse.get("id").equalsIgnoreCase(id))) {
                outputTextArea.setText("This ID already exists. Please use a different ID.");
                return;
            }

            int seconds = new Random().nextInt(90) + 1;

            // Image path is got from ImageView
            Image image = imageView.getImage();
            String imagePath = (image != null) ? image.getUrl() : "";

            Map<String, String> horse = new HashMap<>();
            horse.put("id", id.toUpperCase());
            horse.put("name", name);
            horse.put("jockey", jockey);
            horse.put("age", age);
            horse.put("breed", breed);
            horse.put("race_record", raceRecord);
            horse.put("group", group);
            horse.put("seconds", String.valueOf(seconds));
            horse.put("image", imagePath);

            horses.add(horse);
            outputTextArea.setText("Horse added successfully.");
            clearAddUpdateFields();
            saveToTextFile();
        } catch (Exception e) {
            outputTextArea.setText("Error: " + e.getMessage());
        }
    }

    private void updateHorse() {
        try {
            String id = idField.getText();
            String name = nameField.getText();
            String jockey = jockeyField.getText();
            String age = ageField.getText();
            String breed = breedField.getText();
            String raceRecord = raceRecordField.getText();
            String group = "";

            if (id.isEmpty() || name.isEmpty() || jockey.isEmpty() || age.isEmpty() || breed.isEmpty() || raceRecord.isEmpty()) {
                outputTextArea.setText("Please fill in all fields.");
                return;
            }

            if (groupToggleGroup.getSelectedToggle() != null) {
                group = ((RadioButton) groupToggleGroup.getSelectedToggle()).getText();
            } else {
                outputTextArea.setText("Please select a group.");
                return;
            }

            if (!age.matches("\\d+") || Integer.parseInt(age) < 1 || Integer.parseInt(age) > 30) {
                outputTextArea.setText("Invalid age. Please enter a valid positive integer for age (1-30). Because a horse may live usually upto 30 years.");
                return;
            }

            // Search a horse in the list with the specific id
            Optional<Map<String, String>> optionalHorse = horses.stream()
                    .filter(horse -> horse.get("id").equalsIgnoreCase(id))
                    .findFirst();

            Image image = imageView.getImage();
            String imagePath = (image != null) ? image.getUrl() : "";

            if (optionalHorse.isPresent()) {
                Map<String, String> horse = optionalHorse.get();
                horse.put("name", name);
                horse.put("jockey", jockey);
                horse.put("age", age);
                horse.put("breed", breed);
                horse.put("race_record", raceRecord);
                horse.put("group", group);
                horse.put("image", imagePath);

                outputTextArea.setText("Horse updated successfully.");
                clearAddUpdateFields();
                horseTableView.refresh();
                saveToTextFile();
            } else {
                outputTextArea.setText("Horse not found with the provided ID.");
                clearAddUpdateFields();
            }
        } catch (Exception e) {
            outputTextArea.setText("Error: " + e.getMessage());
        }
    }

    private void deleteHorse() {
        try {
            String id = idField.getText();

            if (id.isEmpty()) {
                outputTextArea.setText("Please enter the ID of the horse to delete.");
                return;
            }

            Optional<Map<String, String>> optionalHorse = horses.stream()
                    .filter(horse -> horse.get("id").equals(id))
                    .findFirst();

            if (optionalHorse.isPresent()) {
                horses.remove(optionalHorse.get());
                outputTextArea.setText("Horse deleted successfully.");
                clearAddUpdateFields();
                saveToTextFile();
            } else {
                outputTextArea.setText("Horse not found with the provided ID.");
                clearAddUpdateFields();
            }
        } catch (Exception e) {
            outputTextArea.setText("Error: " + e.getMessage());
        }
    }

    private void viewHorses() {
        TableView<Map<String, String>> tableView = new TableView<>();

        // The table columns are configured
        TableColumn<Map<String, String>, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("id")));

        TableColumn<Map<String, String>, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("name")));

        TableColumn<Map<String, String>, String> jockeyColumn = new TableColumn<>("Jockey");
        jockeyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("jockey")));

        TableColumn<Map<String, String>, String> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("age")));

        TableColumn<Map<String, String>, String> breedColumn = new TableColumn<>("Breed");
        breedColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("breed")));

        TableColumn<Map<String, String>, String> raceRecordColumn = new TableColumn<>("Race Record");
        raceRecordColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("race_record")));

        TableColumn<Map<String, String>, String> groupColumn = new TableColumn<>("Group");
        groupColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("group")));

        TableColumn<Map<String, String>, ImageView> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(cellData -> {
            String imagePath = cellData.getValue().get("image");
            ImageView thumbnailView = new ImageView();
            thumbnailView.setFitWidth(50);
            thumbnailView.setFitHeight(50);
            if (!imagePath.isEmpty()) {
                Image image = new Image(imagePath, 50, 50, true, true);
                thumbnailView.setImage(image);
            }
            return new SimpleObjectProperty<>(thumbnailView);
        });

        // Add the columns to the table
        tableView.getColumns().addAll(idColumn, nameColumn, jockeyColumn, ageColumn, breedColumn, raceRecordColumn, groupColumn, imageColumn);

        ObservableList<Map<String, String>> sortedHorses = FXCollections.observableArrayList(horses);
        sortedHorses.sort(Comparator.comparing(horse -> horse.get("id")));
        tableView.setItems(sortedHorses);

        Tab tab = new Tab("Horse Details");
        tab.setContent(tableView);

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    private void saveToTextFile() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("horse_details.txt"));

            writer.println("=============================================================================================");
            writer.println("                                    HORSE DETAILS TABLE");
            writer.println("=============================================================================================\n");

            bubbleSort2(horses);

            // Horses are grouped by their group attribute
            Map<String, List<Map<String, String>>> groupedHorses = new HashMap<>();
            for (Map<String, String> horse : horses) {
                String group = horse.get("group");
                groupedHorses.computeIfAbsent(group, k -> new ArrayList<>()).add(horse);
            }

            // Details of each group is written to the text file
            for (Map.Entry<String, List<Map<String, String>>> entry : groupedHorses.entrySet()) {
                String group = entry.getKey();
                List<Map<String, String>> horsesInGroup = entry.getValue();

                // Print group header
                writer.printf("%38s%s\n", "", "=".repeat(14));
                writer.printf("%41sGroup %s\n", "", group);
                writer.printf("%38s%s\n\n", "", "=".repeat(14));

                // Print table header
                writer.printf("%-10s %-20s %-20s %-5s %-15s %-20s %-7s\n",
                        "ID", "Name", "Jockey", "Age", "Breed", "Race Record", "Seconds");
                writer.println("─".repeat(104));

                // Details for each horse is written in the group
                for (Map<String, String> horse : horsesInGroup) {
                    writer.printf("%-10s %-20s %-20s %-5s %-15s %-20s %-7s\n",
                            horse.get("id"), horse.get("name"), horse.get("jockey"),
                            horse.get("age"), horse.get("breed"), horse.get("race_record"),
                            horse.get("seconds"));
                    writer.println("─".repeat(104));
                }

                writer.println();
            }

            writer.close();
            outputTextArea.appendText("\nHorse details saved to text file.");
            raceSimulated = true;
        } catch (IOException e) {
            outputTextArea.setText("Error: " + e.getMessage());
        }
    }

    private void bubbleSort2(List<Map<String, String>> horses) {
        int n = horses.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                // The group is compared first
                String currentGroup = horses.get(j).get("group");
                String nextGroup = horses.get(j + 1).get("group");
                int groupComparison = currentGroup.compareTo(nextGroup);

                if (groupComparison > 0 || (groupComparison == 0 &&
                        Integer.parseInt(horses.get(j).get("id")) > Integer.parseInt(horses.get(j + 1).get("id")))) {
                    // Horses are swapped if either group is greater or if group is the same but current ID is greater than next ID
                    Map<String, String> temp = horses.get(j);
                    horses.set(j, horses.get(j + 1));
                    horses.set(j + 1, temp);
                }
            }
        }
    }

    private void selectFourHorses() {
        if (!raceSimulated) {
            outputTextArea.setText("Please run Save to File first to save the text file.");
            return;
        }

        Map<String, List<Map<String, String>>> groupedHorses = new HashMap<>();
        for (Map<String, String> horse : horses) {
            String group = horse.get("group");
            groupedHorses.computeIfAbsent(group, k -> new ArrayList<>()).add(horse);
        }

        // Groups with at least one horse if filtered
        Set<String> groupsWithHorses = groupedHorses.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        // Empty groups are determined
        Set<String> emptyGroups = Set.of("A", "B", "C", "D").stream()
                .filter(group -> !groupsWithHorses.contains(group))
                .collect(Collectors.toSet());

        // Horses are selected for race
        if (groupsWithHorses.size() >= 4) {
            selectedHorses = new HashMap<>();
            for (Map.Entry<String, List<Map<String, String>>> entry : groupedHorses.entrySet()) {
                List<Map<String, String>> horsesInGroup = entry.getValue();
                if (!horsesInGroup.isEmpty()) {
                    selectedHorses.put(entry.getKey(), horsesInGroup.get(new Random().nextInt(horsesInGroup.size())));
                }
            }

            displaySelectedHorses();
        } else {
            outputTextArea.setText("Can't select horses. Each group must have at least one horse.");
            if (!emptyGroups.isEmpty()) {
                StringBuilder sb = new StringBuilder("Groups without horses: ");
                for (String group : emptyGroups) {
                    sb.append(group).append(", ");
                }
                sb.setLength(sb.length() - 2);
                outputTextArea.appendText("\n" + sb.toString());
                outputTextArea.appendText("\nPlease run Add Horse to add horse details.");
            }
        }
    }

    private void displaySelectedHorses() {
        outputTextArea.clear();
        outputTextArea.appendText("Selected Horses from Each Group\n\n");
        outputTextArea.appendText(String.format("%-7s %-10s %-20s %-20s %-5s %-15s %-20s\n", "Group", "ID", "Horse Name", "Jockey", "Age", "Breed", "Race Record"));
        outputTextArea.appendText("-".repeat(103) + "\n");

        List<Map<String, String>> selectedHorsesList = new ArrayList<>(selectedHorses.values());

        selectedHorsesList.sort(Comparator.comparing(horse -> horse.get("group")));

        for (Map<String, String> horse : selectedHorsesList) {
            outputTextArea.appendText(String.format("  %-5s %-10s %-20s %-20s %-5s %-15s %-20s\n",
                    horse.get("group"), horse.get("id"), horse.get("name"), horse.get("jockey"),
                    horse.get("age"), horse.get("breed"), horse.get("race_record")));
        }
    }

    private void simulateRace() {
        if (selectedHorses.isEmpty()) {
            outputTextArea.setText("Please run Select Horses to select horses first.");
            return;
        }

        outputTextArea.clear();
        outputTextArea.appendText("Winning Horses\n\n");

        // A shuffled list of horses is created from the selected horses map
        List<Map<String, String>> shuffledHorses = new ArrayList<>(selectedHorses.values());
        Collections.shuffle(shuffledHorses);

        for (Map<String, String> horse : shuffledHorses) {
            horse.put("race_time", String.valueOf(new Random().nextInt(91)));
        }

        // Sort horses by race time
        bubbleSortByRaceTime(shuffledHorses);
        winningHorses = shuffledHorses;

        outputTextArea.appendText(String.format("%-6s %-7s %-7s %-20s %-3s\n", "Rank", "Group", "ID", "Horse Name", "Time"));
        outputTextArea.appendText("-".repeat(48) + "\n");

        String[] rankSuffixes = {"st", "nd", "rd"};
        for (int i = 0; i < 3 && i < shuffledHorses.size(); i++) {
            Map<String, String> horse = shuffledHorses.get(i);
            String rank = (i + 1) + (i < 3 ? rankSuffixes[i] : "th");
            outputTextArea.appendText(String.format("%-8s %-5s %-7s %-20s %-3ss\n",
                    rank, horse.get("group"), horse.get("id"), horse.get("name"), horse.get("race_time")));
        }
    }

    // To sort by race time
    private void bubbleSortByRaceTime(List<Map<String, String>> horses) {
        int n = horses.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                int currentRaceTime = Integer.parseInt(horses.get(j).get("race_time"));
                int nextRaceTime = Integer.parseInt(horses.get(j + 1).get("race_time"));

                if (currentRaceTime > nextRaceTime) {
                    // Swap horses if current race time is greater than next race time
                    Map<String, String> temp = horses.get(j);
                    horses.set(j, horses.get(j + 1));
                    horses.set(j + 1, temp);
                }
            }
        }
    }

    // Method to browse and select horse image
    @FXML
    private void browseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Horse Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(browseButton.getScene().getWindow());
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    private void visualizeWinningHorses() {
        if (winningHorses.isEmpty()) {
            outputTextArea.setText("Please run Simulate Race to simulate the race first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BarChart.fxml"));
            VBox barChartRoot = loader.load();
            BarChartController barChartController = loader.getController();
            barChartController.setData(winningHorses);

            Tab barChartTab = new Tab("Bar Chart");
            barChartTab.setContent(barChartRoot);

            TabPane tabPane = (TabPane) buttonsHBox.getScene().getWindow().getScene().getRoot();
            tabPane.getTabs().add(barChartTab);
            tabPane.getSelectionModel().select(barChartTab);
        } catch (IOException e) {
            outputTextArea.setText("Error: " + e.getMessage());
        }
    }
}