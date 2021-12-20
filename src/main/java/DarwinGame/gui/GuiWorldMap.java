package DarwinGame.gui;

import DarwinGame.MapElements.AbstractWorldMapElement;
import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.MapElements.Animal.Genotype;
import DarwinGame.Simulation.SimulationController;
import DarwinGame.Vector2d;
import DarwinGame.WorldMap.AbstractWorldMap;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class GuiWorldMap implements IMapRefreshNeededObserver {
    AbstractWorldMap map;

    private final GridPane mapGrid = new GridPane();
    private final VBox mapBox = new VBox();


    public GuiWorldMap(AbstractWorldMap map) {
        this.map = map;
        this.mapGrid.setGridLinesVisible(true);

        mapGrid.setBackground(new Background(new BackgroundFill(Color.AQUA, CornerRadii.EMPTY, Insets.EMPTY)));
        mapBox.getChildren().addAll(mapGrid);

        renderGrid();
    }


    public VBox getMapBox() {
        return mapBox;
    }

    @Override
    public void refresh(AbstractWorldMap map) {
        Platform.runLater(this::renderGrid);
    }

    void renderGrid() {
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();

        int minY = map.getLowerLeftDrawLimit().y();
        int minX = map.getLowerLeftDrawLimit().x();
        int maxY = map.getUpperRightDrawLimit().y();
        int maxX = map.getUpperRightDrawLimit().x();

        this.mapGrid.getChildren().clear();
        mapGrid.setGridLinesVisible(true);

        Label xyLabel = new Label("y\\x");
        GridPane.setHalignment(xyLabel, HPos.CENTER);
        this.mapGrid.getColumnConstraints().add(new ColumnConstraints(GUIConfig.mapGridCellWidth));
        this.mapGrid.getRowConstraints().add(new RowConstraints(GUIConfig.mapGridCellHeight));
        this.mapGrid.add(xyLabel, 0, 0, 1, 1);


        for (int i = minY; i <= maxY; i++) {
            Label label = new Label(Integer.toString(i));
            this.mapGrid.add(label, 0, maxY - i + 1, 1, 1);
            this.mapGrid.getRowConstraints().add(new RowConstraints(GUIConfig.mapGridCellHeight));
            GridPane.setHalignment(label, HPos.CENTER);
        }
        for (int i = minX; i <= maxX; i++) {
            Label label = new Label(Integer.toString(i));
            this.mapGrid.add(label, i-minX+1, 0, 1, 1);
            this.mapGrid.getColumnConstraints().add(new ColumnConstraints(GUIConfig.mapGridCellWidth));
            GridPane.setHalignment(label, HPos.CENTER);
        }

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Vector2d position = new Vector2d(x, y);
                StackPane cellBox = new StackPane();
                cellBox.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
                if (this.map.getJungleBoundary().isInside(position)) {
                    cellBox.setBackground(new Background(new BackgroundFill(Color.FORESTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                }

                if (map.isOccupied(position)) {
                    AbstractWorldMapElement worldMapElement = map.getTopWorldMapElementAt(position);
                    GuiMapElementBox element = new GuiMapElementBox(worldMapElement);
                    VBox graphicalElement = element.getGraphicalElement();
                    cellBox.getChildren().add(graphicalElement);
                    if (element.mapElement instanceof Animal animal) {
                        Label energyLabel = new Label(Integer.toString(animal.getEnergy()));
                        energyLabel.setTextFill(Color.WHITE);
                        cellBox.getChildren().add(energyLabel);
                    }
                }

                GridPane.setHalignment(cellBox, HPos.CENTER);
                this.mapGrid.add(cellBox, position.x() - minX + 1, maxY - position.y() + 1, 1, 1);
            }
        }
    }

}
