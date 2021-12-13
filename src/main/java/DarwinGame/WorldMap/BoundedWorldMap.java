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
        return position != null;
    }

    @Override
    public void place(Animal animal) {
        super.place(animal);

        this.map.put(animal.getPosition(), animal);
    }

    @Override
    public boolean positionChanged(AbstractMovableWorldMapElement worldMapElement, Vector2d oldPosition, Vector2d newPosition) {
        return false;
    }

    @Override
    public Vector2d correctMovePosition(Vector2d newPosition) {
        if (mapBoundary.isInside(newPosition)) {
            return newPosition;
        }

        int x = newPosition.x();
        int y = newPosition.y();
        int mapX = mapBoundary.upperRight().x();
        int mapY = mapBoundary.upperRight().y();

        x = Math.max(0, x);
        x = Math.min(mapX, x);
        y = Math.max(0, y);
        y = Math.min(mapY, y);

        return new Vector2d(x, y);
    }

    @Override
    public Vector2d getLowerLeftDrawLimit() {
        return this.mapBoundary.lowerLeft();
    }

    @Override
    public Vector2d getUpperRightDrawLimit() {
        return this.mapBoundary.upperRight();
    }
}
