package DarwinGame.gui;

import DarwinGame.WorldMap.BoundedWorldMap;
import DarwinGame.WorldMap.UnboundedWorldMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;



public class App extends Application {
    GuiWorldMap boundedWorldMapGuiElement;
    GuiWorldMap unboundedWorldMapGuiElement;
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

        var noOfPrimaryAnimalsTextBox = new TextField();
        gridPane.addRow(5, new Label("Number of primary animals"), noOfPrimaryAnimalsTextBox);


        Button startSimulationButton = new Button("Start Simulation");
        startSimulationButton.setOnAction(event -> {
            BoundedWorldMap boundedWorldMap = new BoundedWorldMap(10, 10, 5, 5);
            UnboundedWorldMap unboundedWorldMap = new UnboundedWorldMap(10, 10, 5, 5);

            this.boundedWorldMapGuiElement = new GuiWorldMap(boundedWorldMap);
            this.unboundedWorldMapGuiElement = new GuiWorldMap(unboundedWorldMap);

            this.primaryStage.setScene(createSimulationScene());
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
