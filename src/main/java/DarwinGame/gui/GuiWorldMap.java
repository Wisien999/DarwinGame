package DarwinGame.gui;

import DarwinGame.MapElements.AbstractWorldMapElement;
import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.Vector2d;
import DarwinGame.WorldMap.AbstractWorldMap;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GuiWorldMap implements IMapRefreshNeededObserver {
    AbstractWorldMap map;

    private final GridPane mapGrid = new GridPane();
    private final VBox mapBox = new VBox();

    private Vector2d displayedUpperRight;
    private Vector2d displayedLowerLeft;

    private final Stage parentStage;


    public GuiWorldMap(AbstractWorldMap map, Stage parentStage) {
        this.parentStage = parentStage;
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

    public void highlightCells(Set<Vector2d> positions) {

        for (Node node : mapGrid.getChildren()) {
            int gridX = GridPane.getColumnIndex(node);
            int gridY= GridPane.getRowIndex(node);

            if (positions.contains(getRealPosition(new Vector2d(gridX, gridY)))) {
                StackPane cell = (StackPane) node;

                cell.setBorder(new Border(new BorderStroke(
                        Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(2d), BorderWidths.DEFAULT)));
            }

        }
    }

    private Vector2d getRealPosition(Vector2d gridPosition) {
        int x = gridPosition.x() - 1 + displayedLowerLeft.x();
        int y = displayedUpperRight.y() - gridPosition.y() + 1;

        return new Vector2d(x, y);
    }

    private Vector2d getGridPosition(Vector2d mapPosition) {
        int x = mapPosition.x() - displayedLowerLeft.x() + 1;
        int y = displayedUpperRight.y() - mapPosition.y() + 1;

        return new Vector2d(x, y);
    }

    protected void renderGrid() {
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();

        displayedUpperRight = map.getUpperRightDrawLimit();
        displayedLowerLeft = map.getLowerLeftDrawLimit();

        int minY = displayedLowerLeft.y();
        int minX = displayedLowerLeft.x();
        int maxY = displayedUpperRight.y();
        int maxX = displayedUpperRight.x();

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
                    GuiMapElement element = new GuiMapElement(worldMapElement);
                    element.addGuiWorldMapElementClickObservers((IGuiWorldMapElementClickObserver) parentStage);
                    cellBox.getChildren().add(element);
                }

                GridPane.setHalignment(cellBox, HPos.CENTER);
                Vector2d gridPosition = getGridPosition(position);
                this.mapGrid.add(cellBox, gridPosition.x(), gridPosition.y(), 1, 1);
            }
        }
    }

}
