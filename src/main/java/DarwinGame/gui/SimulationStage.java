package DarwinGame.gui;

import DarwinGame.Simulation.SimulationController;
import DarwinGame.WorldMap.BoundedWorldMap;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.Locale;

public class SimulationStage extends Stage {
    GuiWorldMap worldMapGuiElement;

    private final StatisticsBox statisticsBox;

    private final SimulationController simulationController;

    public SimulationStage(int mapWidth, int mapHeight, int jungleWidth, int jungleHeight) {
        BoundedWorldMap worldMap = new BoundedWorldMap(mapWidth, mapHeight, jungleWidth, jungleHeight);
        worldMapGuiElement = new GuiWorldMap(worldMap);
        this.simulationController = new SimulationController(worldMap);
        this.simulationController.getEngine().addMapRefreshNeededObserver(worldMapGuiElement);
        statisticsBox = new StatisticsBox(simulationController.getSimpleStatisticsHandler());
        this.simulationController.getSimpleStatisticsHandler().addStatisticsObserver(statisticsBox);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox layout = new HBox();
        VBox rightBox = new VBox();
        HBox controls = new HBox();
        rightBox.getChildren().addAll(controls, statisticsBox);


        this.setOnCloseRequest(e -> this.simulationController.stopSimulation());

        Button startButton = new Button("Start simulation");
        startButton.setOnAction(e -> {
            startSimulationButtonFire(startButton);
        });

        controls.getChildren().addAll(startButton);

        layout.getChildren().addAll(worldMapGuiElement.getMapBox(), spacer, rightBox);

        this.setScene(new Scene(layout, 1000, 600));
        this.show();
    }




    private void startSimulationButtonFire(Button button) {
        simulationController.startSimulation();

        button.setText("Stop simulation");
        button.setOnAction(e -> {
            stopSimulationButtonFire(button);
        });
    }


    private void stopSimulationButtonFire(Button button) {
        simulationController.stopSimulation();

        button.setText("Start simulation");
        button.setOnAction(e -> {
            startSimulationButtonFire(button);
        });
    }


}
