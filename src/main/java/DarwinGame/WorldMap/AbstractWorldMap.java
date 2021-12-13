package DarwinGame.WorldMap;

import DarwinGame.IPositionChangeObserver;
import DarwinGame.MapElements.AbstractMovableWorldMapElement;
import DarwinGame.MapElements.AbstractWorldMapElement;
import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.MapElements.Animal.AnimalEnergyComparator;
import DarwinGame.Vector2d;

import java.util.*;

public abstract class AbstractWorldMap implements IPositionChangeObserver {
    protected final Map<Vector2d, AbstractWorldMapElement> map = new LinkedHashMap<>();
    protected final Map<Vector2d, SortedSet<Animal>> animals = new LinkedHashMap<>();
    protected final Boundary jungleBoundary;
    protected final Boundary mapBoundary;


    public AbstractWorldMap(int width, int height, int jungleWidth, int jungleHeight) {
        this.mapBoundary = new Boundary(new Vector2d(0, 0), new Vector2d(width-1, height-1));
        int centerX = width / 2;
        int centerY = height / 2;

        Vector2d jungleLowerLeft = new Vector2d(centerX - jungleWidth / 2, centerY - jungleHeight / 2);

        this.jungleBoundary = new Boundary(jungleLowerLeft,
                jungleLowerLeft.add(new Vector2d(jungleWidth-1, jungleHeight-1)));

    }


    public boolean isOccupied(Vector2d position) {
        return this.map.containsKey(position) || this.animals.containsKey(position);
    }

    public void place(Animal animal) {
        if (animal == null) throw new IllegalArgumentException("null can't be placed on the worldMap");
        if (animal.getPosition() == null) {
            throw new IllegalArgumentException("Object can't be placed on position " + animal.getPosition());
        }

        this.animals.putIfAbsent(animal.getPosition(), new TreeSet<>(new AnimalEnergyComparator()));
        this.animals.get(animal.getPosition()).add(animal);

        animal.addObserver(this);
    }

    @Override
    public boolean positionChanged(AbstractMovableWorldMapElement worldMapElement, Vector2d oldPosition, Vector2d newPosition) {
        if (worldMapElement instanceof Animal) {
            this.animals.get(oldPosition).remove(worldMapElement);
            this.animals.get(newPosition).add((Animal) worldMapElement);

            if (this.animals.get(oldPosition).isEmpty()) {
                this.animals.remove(oldPosition);
            }
        }
        else {
            this.map.remove(oldPosition);
            this.map.put(newPosition, worldMapElement);
        }

        return true;
    }

    public abstract Vector2d correctMovePosition(Vector2d newPosition);

    public abstract Vector2d getLowerLeftDrawLimit();
    public abstract Vector2d getUpperRightDrawLimit();
}
