package DarwinGame.gui;

import DarwinGame.MapElements.AbstractWorldMapElement;
import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.MapElements.Grass;
import DarwinGame.Statistics.AnimalTracer;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static DarwinGame.gui.GuiHelpers.generateSolidColorImage;


public class GuiMapElement extends StackPane {
    protected AbstractWorldMapElement mapElement;
    private List<IGuiWorldMapElementClickObserver> guiWorldMapElementClickObservers = new ArrayList<>();


    public GuiMapElement(AbstractWorldMapElement mapElement) {
        super();
        this.mapElement = mapElement;
        Image image;
        try {
            image = ResourceLoader.getImage(this.mapElement.getImageResource());
        }
        catch (FileNotFoundException e) {
            double r = 0, g = 0, b = 0;
            if (mapElement instanceof Animal) {
                r = 0.8;
                g = 0.2;
                b = 0.6;
            }
            else if (mapElement instanceof Grass) {
                r = 0;
                g = 1;
                b = 0.1;
            }
            image = generateSolidColorImage(1, 1, r, g, b, 1);
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(18);
        imageView.setFitHeight(18);



        this.getChildren().add(imageView);
        this.setAlignment(Pos.CENTER);
        if (this.mapElement instanceof Animal animal) {
            Label energyLabel = new Label(Integer.toString(animal.getEnergy()));
            energyLabel.setTextFill(Color.WHITE);
            this.getChildren().add(energyLabel);
        }
        this.setOnMouseClicked(this::handleClick);
    }

    private void handleClick(MouseEvent event) {
        this.guiWorldMapElementClickObservers.forEach(observer ->
                observer.guiWorldMapElementClicked(this, event));
    }

    public void addGuiWorldMapElementClickObservers(IGuiWorldMapElementClickObserver observer) {
        this.guiWorldMapElementClickObservers.add(observer);
    }
    public void removeGuiWorldMapElementClickObservers(IGuiWorldMapElementClickObserver observer) {
        this.guiWorldMapElementClickObservers.remove(observer);
    }
}
