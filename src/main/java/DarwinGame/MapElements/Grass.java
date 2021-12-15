package DarwinGame.MapElements;

import DarwinGame.gui.PathConfig;
import DarwinGame.Vector2d;

public class Grass extends AbstractWorldMapElement {
    public Grass(Vector2d position) {
        super(position);
    }

    @Override
    public String toString() {
        return "T " + this.position.toString();
    }

    @Override
    public String getImageResource() {
        return PathConfig.imageBasePath + "grass.png";
    }
}
