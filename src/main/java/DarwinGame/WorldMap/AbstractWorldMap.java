package DarwinGame.WorldMap;

import DarwinGame.IEnergyChangeObserver;
import DarwinGame.IPositionChangeObserver;
import DarwinGame.MapElements.AbstractMovableWorldMapElement;
import DarwinGame.MapElements.AbstractWorldMapElement;
import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.Vector2d;

import java.util.*;

public abstract class AbstractWorldMap implements IPositionChangeObserver, IEnergyChangeObserver {
    protected final Map<Vector2d, AbstractWorldMapElement> map = new LinkedHashMap<>();
    protected final Map<Vector2d, SortedSet<MapAnimalContainer>> animals = new LinkedHashMap<>();
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

        this.animals.putIfAbsent(animal.getPosition(), new TreeSet<>());
        this.animals.get(animal.getPosition()).add(new MapAnimalContainer(animal.getEnergy(), animal));

        animal.addObserver(this);
    }

    @Override
    public boolean positionChanged(AbstractMovableWorldMapElement worldMapElement, Vector2d oldPosition, Vector2d newPosition) {
        if (worldMapElement instanceof Animal animal) {
            System.out.println(worldMapElement.getPosition());
            MapAnimalContainer mapAnimalContainer = new MapAnimalContainer(animal.getEnergy(), animal);
            this.animals.get(oldPosition).remove(mapAnimalContainer);
            this.animals.putIfAbsent(newPosition, new TreeSet<>());
            this.animals.get(newPosition).add(mapAnimalContainer);

            removeAnimalsEntryIfPossible(oldPosition);
        }
        else {
            this.map.remove(oldPosition);
            this.map.put(newPosition, worldMapElement);
        }

        return true;
    }

    @Override
    public void energyChanged(Animal animal, int oldEnergy, int newEnergy) {
        MapAnimalContainer oldMapAnimalContainer = new MapAnimalContainer(oldEnergy, animal);
        var positionSet = this.animals.get(animal.getPosition());
        positionSet.remove(oldMapAnimalContainer);

        if (newEnergy > 0) {
            MapAnimalContainer newMapAnimalContainer = new MapAnimalContainer(newEnergy, animal);
            positionSet.add(newMapAnimalContainer);
        }
        removeAnimalsEntryIfPossible(animal.getPosition());
    }

    private void removeAnimalsEntryIfPossible(Vector2d position) {
        if (this.animals.get(position).isEmpty()) {
            this.animals.remove(position);
        }
    }

    public SortedSet<MapAnimalContainer> getAnimalsAt(Vector2d position) {
        return this.animals.getOrDefault(position, new TreeSet<>());
    }

    public AbstractWorldMapElement getMapElementAt(Vector2d position) {
        return this.map.get(position);
    }

    public void removeMapElementAt(Vector2d position) {
        this.map.remove(position);
    }
    public AbstractWorldMapElement getTopWorldMapElementAt(Vector2d position) {
        var animals = this.getAnimalsAt(position);
        if (!animals.isEmpty()) {
            return animals.last().animal();
        }
        return this.map.get(position);
    }

    public Boundary getJungleBoundary() {
        return jungleBoundary;
    }
    public Boundary getMapBoundary() {
        return mapBoundary;
    }

    public abstract boolean canMoveTo(Vector2d position);
    public abstract Vector2d correctMovePosition(Vector2d oldPosition, Vector2d newPosition);

    public Vector2d getLowerLeftDrawLimit() {
        return this.mapBoundary.lowerLeft();
    }
    public Vector2d getUpperRightDrawLimit() {
        return this.mapBoundary.upperRight();
    }


}
