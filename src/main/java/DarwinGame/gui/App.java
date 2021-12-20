package DarwinGame.gui;

import DarwinGame.Simulation.SimulationConfig;
import DarwinGame.WorldMap.BoundedWorldMap;
import DarwinGame.WorldMap.UnboundedWorldMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import java.math.BigDecimal;
import java.text.NumberFormat;


public class App extends Application {
    GuiWorldMap boundedWorldMapGuiElement;
    GuiWorldMap unboundedWorldMapGuiElement;
    Stage primaryStage;

    SimulationStage boundedWorldMapSimulationStage;
    SimulationStage unboundedWorldMapSimulationStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setOnCloseRequest(e -> {
//            Platform.exit();
//            System.exit(0);
        });

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


        Button startSimulationButton = new Button("Go to the simulations view");
        startSimulationButton.setOnAction(event -> {
            int mapWidth = mapWidthTextBox.getNumber().intValue();
            int mapHeight = mapHeightTextBox.getNumber().intValue();
            double jungleRatio = jungleHeightTextBox.getNumber().doubleValue();
            var jungleDimensions = GuiHelpers.getJungleWidthAndHeightFromRatio(jungleRatio, mapWidth, mapHeight);

            SimulationConfig.defaultAmountOfEnergyPoints = animalStartEnergyNumberBox.getNumber().intValue();
            SimulationConfig.simulationDayEnergyCost = epochEnergyCostNumberBox.getNumber().intValue();
            SimulationConfig.plantEnergy = plantEnergyNumberBox.getNumber().intValue();
            SimulationConfig.noOfStartingAnimals = noOfPrimaryAnimalsTextBox.getNumber().intValue();


            boundedWorldMapSimulationStage = new SimulationStage(mapWidth, mapHeight, jungleDimensions.getKey(), jungleDimensions.getValue());
            unboundedWorldMapSimulationStage = new SimulationStage(mapWidth, mapHeight, jungleDimensions.getKey(), jungleDimensions.getValue());



//            BoundedWorldMap boundedWorldMap = new BoundedWorldMap(mapWidth, mapHeight, jungleDimensions.getKey(), jungleDimensions.getValue());
//            UnboundedWorldMap unboundedWorldMap = new UnboundedWorldMap(mapWidth, mapHeight, jungleDimensions.getKey(), jungleDimensions.getValue());
//            this.boundedWorldMapGuiElement = new GuiWorldMap(boundedWorldMap);
//            this.unboundedWorldMapGuiElement = new GuiWorldMap(unboundedWorldMap);
//
//            this.primaryStage.setScene(createSimulationScene());
        });


        layout.getChildren().add(startSimulationButton);
        return new Scene(layout, 500, 600);
    }


    private Scene createSimulationScene() {
        VBox layout = new VBox();


        HBox simulationsGrids = new HBox();
        layout.getChildren().addAll(simulationsGrids);

        var spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setMargin(this.unboundedWorldMapGuiElement.getMapBox(), new Insets(10, 10, 0, 10));
        HBox.setMargin(this.boundedWorldMapGuiElement.getMapBox(), new Insets(10, 10, 0, 10));

        simulationsGrids.getChildren().addAll(this.unboundedWorldMapGuiElement.getMapBox(), spacer, this.boundedWorldMapGuiElement.getMapBox());


        return new Scene(layout, 1000, 600);
    }

}
