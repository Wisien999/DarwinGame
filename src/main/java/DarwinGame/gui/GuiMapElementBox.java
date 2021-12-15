package DarwinGame.gui;

import DarwinGame.MapElements.AbstractWorldMapElement;
import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.MapElements.Grass;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.FileNotFoundException;

import static DarwinGame.gui.GuiHelpers.generateSolidColorImage;


public class GuiMapElementBox {
    protected AbstractWorldMapElement mapElement;
    protected VBox graphicalElement = new VBox(4);

    public GuiMapElementBox(AbstractWorldMapElement mapElement) {
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



        this.graphicalElement.getChildren().add(imageView);
        this.graphicalElement.setAlignment(Pos.CENTER);
    }

    public VBox getGraphicalElement() {
        return graphicalElement;
    }
}
