package DarwinGame.WorldMap;

import DarwinGame.MapElements.AbstractMovableWorldMapElement;
import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.Vector2d;

import java.util.concurrent.ThreadLocalRandom;

public class BoundedWorldMap extends AbstractWorldMap {
    public BoundedWorldMap(int width, int height, int jungleWidth, int jungleHeight) {
        super(width, height, jungleWidth, jungleHeight);
    }

    public boolean canMoveTo(Vector2d position) {
        return getMapBoundary().isInside(position);
    }

    @Override
    public Vector2d correctMovePosition(Vector2d oldPosition, Vector2d newPosition) {
        if (mapBoundary.isInside(newPosition)) {
            return newPosition;
        }

        return oldPosition;
    }
}
