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
    public void place(Animal animal) {
        super.place(animal);

        this.map.put(animal.getPosition(), animal);
    }

    @Override
    public Vector2d correctMovePosition(Vector2d oldPosition, Vector2d newPosition) {
        return new Vector2d(newPosition.x() % this.getUpperRightDrawLimit().x(),
                newPosition.y() % this.getUpperRightDrawLimit().y());
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
