package DarwinGame.gui;

import DarwinGame.Simulation.SimulationConfig;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.NumberFormat;


public class App extends Application {
    Stage primaryStage;

    SimulationStage boundedWorldMapSimulationStage;
    SimulationStage unboundedWorldMapSimulationStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        Scene configScene = createConfigScene();



        this.primaryStage.setScene(configScene);
//        primaryStage.setScene(createSimulationScene());
        this.primaryStage.show();
    }

    private Scene createConfigScene() {
        VBox layout = new VBox();
        layout.setAlignment(Pos.TOP_CENTER);
        HBox hBoxGrid = new HBox();
        hBoxGrid.setAlignment(Pos.TOP_CENTER);
        GridPane gridPane = new GridPane();
        hBoxGrid.getChildren().add(gridPane);
        VBox.setMargin(hBoxGrid, new Insets(20, 0, 0, 0));
        layout.getChildren().add(hBoxGrid);
        gridPane.setGridLinesVisible(true);
        gridPane.setHgap(20);
        layout.setSpacing(20);

        var mapHeightTextBox = new NumberTextField(GUIConfig.mapHeightDefault);
        gridPane.addRow(1, new Label("Map height:"), mapHeightTextBox);
        var mapWidthTextBox = new NumberTextField(GUIConfig.mapWidthDefault);
        gridPane.addRow(2, new Label("Map width:"), mapWidthTextBox);
        var jungleHeightTextBox = new NumberTextField(BigDecimal.valueOf(GUIConfig.jungleRatio), NumberFormat.getPercentInstance());
        gridPane.addRow(3, new Label("Jungle ratio:"), jungleHeightTextBox);

        var noOfPrimaryAnimalsTextBox = new NumberTextField(GUIConfig.noOfPrimaryAnimals);
        gridPane.addRow(4, new Label("Number of primary animals"), noOfPrimaryAnimalsTextBox);

        var animalStartEnergyNumberBox = new NumberTextField(GUIConfig.animalStartEnergy);
        gridPane.addRow(5, new Label("Animal start energy:"), animalStartEnergyNumberBox);
        var epochEnergyCostNumberBox = new NumberTextField(GUIConfig.animalMoveEnergyCost);
        gridPane.addRow(6, new Label("Epoch energy cost:"), epochEnergyCostNumberBox);
        var plantEnergyNumberBox = new NumberTextField(GUIConfig.plantEnergy);
        gridPane.addRow(7, new Label("Plant energy:"), plantEnergyNumberBox);

        var unboundedMapEvolutionTypeComboBox = new ComboBox<EvolutionType>();
        unboundedMapEvolutionTypeComboBox.getItems().add(EvolutionType.STANDARD);
        unboundedMapEvolutionTypeComboBox.getItems().add(EvolutionType.MAGICAL);
        unboundedMapEvolutionTypeComboBox.getSelectionModel().selectFirst();
        gridPane.addRow(8, new Label("Unbounded map evolution type"), unboundedMapEvolutionTypeComboBox);

        var boundedMapEvolutionTypeComboBox = new ComboBox<EvolutionType>();
        boundedMapEvolutionTypeComboBox.getItems().add(EvolutionType.STANDARD);
        boundedMapEvolutionTypeComboBox.getItems().add(EvolutionType.MAGICAL);
        boundedMapEvolutionTypeComboBox.getSelectionModel().selectFirst();
        gridPane.addRow(9, new Label("Bounded map evolution type"), boundedMapEvolutionTypeComboBox);


        Button startSimulationButton = new Button("Create simulations");
        startSimulationButton.setOnAction(event -> {
            int mapWidth = mapWidthTextBox.getNumber().intValue();
            int mapHeight = mapHeightTextBox.getNumber().intValue();
            double jungleRatio = jungleHeightTextBox.getNumber().doubleValue();
            var jungleDimensions = GuiHelpers.getJungleWidthAndHeightFromRatio(jungleRatio, mapWidth, mapHeight);

            SimulationConfig.defaultAmountOfEnergyPoints = animalStartEnergyNumberBox.getNumber().intValue();
            SimulationConfig.simulationDayEnergyCost = epochEnergyCostNumberBox.getNumber().intValue();
            SimulationConfig.plantEnergy = plantEnergyNumberBox.getNumber().intValue();
            SimulationConfig.noOfStartingAnimals = noOfPrimaryAnimalsTextBox.getNumber().intValue();


            boundedWorldMapSimulationStage = new SimulationStage(
                    mapWidth, mapHeight,
                    jungleDimensions.getKey(), jungleDimensions.getValue(),
                    true,
                    boundedMapEvolutionTypeComboBox.getSelectionModel().getSelectedItem());
            unboundedWorldMapSimulationStage = new SimulationStage(
                    mapWidth, mapHeight,
                    jungleDimensions.getKey(), jungleDimensions.getValue(),
                    false,
                    unboundedMapEvolutionTypeComboBox.getSelectionModel().getSelectedItem());
            boundedWorldMapSimulationStage.setTitle("Bounded World");
            unboundedWorldMapSimulationStage.setTitle("Unbounded World");
            primaryStage.close();
        });


        layout.getChildren().add(startSimulationButton);
        return new Scene(layout, 500, 600);
    }
}
