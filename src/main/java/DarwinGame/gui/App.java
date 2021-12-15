package DarwinGame.gui;

import DarwinGame.Simulation.SimulationConfig;
import DarwinGame.Simulation.ThreadedSimulationEngine;
import DarwinGame.WorldMap.BoundedWorldMap;
import DarwinGame.WorldMap.UnboundedWorldMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class App extends Application {
    GuiWorldMap boundedWorldMapGuiElement;
    GuiWorldMap unboundedWorldMapGuiElement;
    ThreadedSimulationEngine boundedWorldEngine;
    ThreadedSimulationEngine unboundedWorldEngine;
    Thread boundedWorldEngineThread;
    Thread unboundedWorldEngineThread;
    Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        Scene configScene = createConfigScene();



        this.primaryStage.setScene(configScene);
//        primaryStage.setScene(createSimulationScene());
        this.primaryStage.show();
    }

    private Scene createConfigScene() {
        VBox layout = new VBox();
        GridPane gridPane = new GridPane();
        layout.getChildren().add(gridPane);
        gridPane.setGridLinesVisible(true);
        gridPane.setHgap(20);
        layout.setSpacing(20);

        var mapHeightTextBox = new TextField();
        gridPane.addRow(1, new Label("Map height:"), mapHeightTextBox);
        var mapWidthTextBox = new TextField();
        gridPane.addRow(2, new Label("Map width:"), mapWidthTextBox);
        var jungleHeightTextBox = new TextField();
        gridPane.addRow(3, new Label("Jungle height:"), jungleHeightTextBox);
        var jungleWidthTextBox = new TextField();
        gridPane.addRow(4, new Label("Jungle width:"), jungleWidthTextBox);


        Button startSimulationButton = new Button("Start Simulation");
        startSimulationButton.setOnAction(event -> {
            BoundedWorldMap boundedWorldMap = new BoundedWorldMap(10, 10, 5, 5);
            UnboundedWorldMap unboundedWorldMap = new UnboundedWorldMap(10, 10, 5, 5);

            this.boundedWorldEngine = new ThreadedSimulationEngine(boundedWorldMap, SimulationConfig.noOfStartingAnimals);
            this.unboundedWorldEngine = new ThreadedSimulationEngine(unboundedWorldMap, SimulationConfig.noOfStartingAnimals);

            this.boundedWorldMapGuiElement = new GuiWorldMap(boundedWorldMap);
            this.unboundedWorldMapGuiElement = new GuiWorldMap(unboundedWorldMap);

            this.boundedWorldEngine.addMapRefreshNeededObserver(this.boundedWorldMapGuiElement);
            this.unboundedWorldEngine.addMapRefreshNeededObserver(this.unboundedWorldMapGuiElement);


            this.primaryStage.setScene(createSimulationScene());

            this.boundedWorldEngineThread = new Thread(this.boundedWorldEngine);
            this.unboundedWorldEngineThread = new Thread(this.unboundedWorldEngine);

            this.boundedWorldEngineThread.start();
            this.unboundedWorldEngineThread.start();

        });
        layout.getChildren().add(startSimulationButton);
        return new Scene(layout, 500, 600);
    }

    private Scene createSimulationScene() {
        VBox layout = new VBox();


        HBox mapGrids = new HBox();
        layout.getChildren().add(mapGrids);

        var spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        mapGrids.getChildren().addAll(this.boundedWorldMapGuiElement.getMapGrid(), spacer, this.unboundedWorldMapGuiElement.getMapGrid());


        return new Scene(layout, 1000, 600);
    }

}
