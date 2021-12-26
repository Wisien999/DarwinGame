package DarwinGame.gui;

import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.Simulation.ISimulationEventObserver;
import DarwinGame.Simulation.SimulationConfig;
import DarwinGame.Simulation.SimulationController;
import DarwinGame.Statistics.AnimalTracer;
import DarwinGame.Vector2d;
import DarwinGame.WorldMap.AbstractWorldMap;
import DarwinGame.WorldMap.BoundedWorldMap;
import DarwinGame.WorldMap.UnboundedWorldMap;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Set;


public class SimulationStage extends Stage implements IGuiWorldMapElementClickObserver, ISimulationEventObserver {
    private final GuiWorldMap worldMapGuiElement;
    private final StatisticsBox statisticsBox;

    private final Popup popup = new Popup();

    private final SimulationController simulationController;


    public SimulationStage(int mapWidth, int mapHeight, int jungleWidth, int jungleHeight, boolean bounded,
                           EvolutionType evolutionType) {
        AbstractWorldMap worldMap;
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        if (bounded) {
            worldMap = new BoundedWorldMap(mapWidth, mapHeight, jungleWidth, jungleHeight);

            this.setX(bounds.getMinX() + bounds.getWidth()/2);
        }
        else {
            worldMap = new UnboundedWorldMap(mapWidth, mapHeight, jungleWidth, jungleHeight);

            this.setX(bounds.getMinX());
        }


        worldMapGuiElement = new GuiWorldMap(worldMap, this);
        this.simulationController = new SimulationController(worldMap, evolutionType);
        this.simulationController.getEngine().addMapRefreshNeededObserver(worldMapGuiElement);

        simulationController.getEngine().addSimulationEventObserver(this);


        statisticsBox = new StatisticsBox(simulationController.getSimpleStatisticsHandler(), this);
        this.simulationController.getSimpleStatisticsHandler().addStatisticsObserver(statisticsBox);
        this.simulationController.addSimulationObserver(statisticsBox);

        Slider timeSlider = new Slider(10, 5000, SimulationConfig.simulationMoveDelay);
        timeSlider.setShowTickLabels(true);
        timeSlider.setShowTickMarks(true);
        timeSlider.setMajorTickUnit(50);
        timeSlider.setMinorTickCount(5);
        timeSlider.setBlockIncrement(50);
        timeSlider.valueProperty().addListener(e -> {
            double newDelay = timeSlider.getValue();

            this.simulationController.getEngine().setMoveDelay((int) newDelay);
        });
        HBox.setHgrow(timeSlider, Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox.setHgrow(statisticsBox, Priority.ALWAYS);

        HBox layout = new HBox();
        VBox rightBox = new VBox();
        HBox simulationControls = new HBox();
        rightBox.getChildren().addAll(simulationControls, statisticsBox);

        this.setOnCloseRequest(e -> this.simulationController.stopSimulation());

        Button startButton = new Button("Start simulation");
        startButton.setOnAction(e -> startSimulationButtonFire(startButton));

        simulationControls.getChildren().addAll(startButton, timeSlider);

        layout.getChildren().addAll(worldMapGuiElement.getMapBox(), spacer, rightBox);

        this.setY(bounds.getMinY());
        this.setScene(new Scene(layout, bounds.getWidth()/2, bounds.getHeight()));
        this.show();
    }


    private void startSimulationButtonFire(Button button) {
        simulationController.startSimulation();

        button.setText("Stop simulation");
        button.setOnAction(e -> stopSimulationButtonFire(button));
    }


    private void stopSimulationButtonFire(Button button) {
        simulationController.stopSimulation();

        button.setText("Start simulation");
        button.setOnAction(e -> startSimulationButtonFire(button));
    }

    public void showPopUp(String title, String message) {
        VBox layout = new VBox();
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font(25d));
        Label genotype = new Label(message);

        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
        layout.getChildren().addAll(titleLabel, genotype);

        popup.setAutoHide(true);
        popup.getContent().clear();
        popup.getContent().add(layout);
        popup.show(this);
    }

    public void highlightGuiWorldMapCells(Set<Vector2d> worldMapPositions) {
        worldMapGuiElement.highlightCells(worldMapPositions);
    }

    @Override
    public void guiWorldMapElementClicked(GuiMapElement guiMapElement, MouseEvent event) {
        if (popup.isShowing()) {
            return;
        }

        if (guiMapElement.mapElement instanceof Animal animal) {
            if (event.getButton() == MouseButton.PRIMARY) {
                showPopUp("Genotype of the clicked animal", animal.getGenotype().toString());
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                AnimalTracer animalTracer = new AnimalTracer(animal);
                simulationController.getEngine().addNextDayObserver(animalTracer);
                statisticsBox.setAnimalTracer(animalTracer);
            }
        }

    }

    @Override
    public void magicalRescueHappened(int magicalRescuesLeft) {
        Platform.runLater(() ->
            showPopUp("Magical rescue happened!",
                    "Magical rescues left: " + simulationController.getEngine().getMagicalRescuesLeft())
        );
    }
}
