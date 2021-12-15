package DarwinGame.WorldMap;

import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.Vector2d;

import java.util.concurrent.ThreadLocalRandom;

public class UnboundedWorldMap extends AbstractWorldMap {
    public UnboundedWorldMap(int width, int height, int jungleWidth, int jungleHeight) {
        super(width, height, jungleWidth, jungleHeight);
    }

    public boolean canMoveTo(Vector2d position) {
        return position != null;
    }

    @Override
    public Vector2d correctMovePosition(Vector2d oldPosition, Vector2d newPosition) {
        int x = newPosition.x() >= 0 ? newPosition.x() : newPosition.x() + this.mapBoundary.upperRight.x() + 1;
        int y = newPosition.y() >= 0 ? newPosition.y() : newPosition.y() + this.mapBoundary.upperRight.y() + 1;
        return new Vector2d(x % (this.mapBoundary.upperRight().x() + 1),
                            y % (this.mapBoundary.upperRight().y() + 1));
    }
}
