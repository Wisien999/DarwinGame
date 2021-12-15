package DarwinGame.gui;

import DarwinGame.MapElements.AbstractWorldMapElement;
import DarwinGame.Simulation.SimulationConfig;
import DarwinGame.Simulation.ThreadedSimulationEngine;
import DarwinGame.Vector2d;
import DarwinGame.WorldMap.AbstractWorldMap;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class GuiWorldMap implements IMapRefreshNeededObserver {
    AbstractWorldMap map;

    GridPane mapGrid = new GridPane();

    public GuiWorldMap(AbstractWorldMap map) {
        this.map = map;

        this.mapGrid.getChildren().clear();
        this.mapGrid.setGridLinesVisible(true);
    }

    @Override
    public void refresh(AbstractWorldMap map) {
        Platform.runLater(this::renderGrid);
    }

    void renderGrid() {
        mapGrid.setBackground(new Background(new BackgroundFill(Color.AQUA, CornerRadii.EMPTY, Insets.EMPTY)));
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
                cellBox.setBackground(new Background(new BackgroundFill(Color.KHAKI, CornerRadii.EMPTY, Insets.EMPTY)));
                if (this.map.getJungleBoundary().isInside(position)) {
                    cellBox.setBackground(new Background(new BackgroundFill(Color.FORESTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                }

                if (map.isOccupied(position)) {
                    AbstractWorldMapElement worldMapElement = map.getTopWorldMapElementAt(position);
                    GuiMapElementBox element = new GuiMapElementBox(worldMapElement);
                    VBox graphicalElement = element.getGraphicalElement();
                    cellBox.getChildren().add(graphicalElement);
                }

                GridPane.setHalignment(cellBox, HPos.CENTER);
                this.mapGrid.add(cellBox, position.x() - minX + 1, maxY - position.y() + 1, 1, 1);
            }
        }
    }

    public GridPane getMapGrid() {
        return mapGrid;
    }
}
