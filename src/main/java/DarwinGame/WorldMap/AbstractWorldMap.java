package DarwinGame.WorldMap;

import DarwinGame.MapElements.Animal.IAnimalLifeObserver;
import DarwinGame.MapElements.Animal.IEnergyObserver;
import DarwinGame.MapElements.Animal.IPositionChangeObserver;
import DarwinGame.MapElements.AbstractMovableWorldMapElement;
import DarwinGame.MapElements.AbstractWorldMapElement;
import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.MapElements.Grass;
import DarwinGame.Statistics.IGrassActionObserver;
import DarwinGame.Vector2d;
import javafx.util.Pair;

import java.util.*;

public abstract class AbstractWorldMap implements IPositionChangeObserver, IEnergyObserver, IAnimalLifeObserver {
    protected final Map<Vector2d, AbstractWorldMapElement> map = new LinkedHashMap<>();
    protected final Map<Vector2d, NavigableSet<MapAnimalContainer>> animals = new LinkedHashMap<>();
    protected final Boundary jungleBoundary;
    protected final Boundary mapBoundary;
    protected final List<IGrassActionObserver> grassActionObservers = new ArrayList<>();


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

        if (!this.animals.containsKey(animal.getPosition())) {
            this.animals.put(animal.getPosition(), new TreeSet<>());
            this.incrementSlotsTaken(animal.getPosition());
        }
        this.animals.get(animal.getPosition()).add(new MapAnimalContainer(animal.getEnergy(), animal));

        animal.addPositionObserver(this);
        animal.addEnergyObserver(this);
        animal.addLifeObserver(this);
    }

    private void incrementSlotsTaken(Vector2d position) {
        if (this.mapBoundary.isInside(position)) {
            this.mapBoundary.incrementSlotsTaken();
        }
        if (this.jungleBoundary.isInside(position)) {
            this.jungleBoundary.incrementSlotsTaken();
        }
    }
    private void decrementSlotsTaken(Vector2d position) {
        if (this.mapBoundary.isInside(position)) {
            this.mapBoundary.decrementSlotsTaken();
        }
        if (this.jungleBoundary.isInside(position)) {
            this.jungleBoundary.decrementSlotsTaken();
        }
    }

    @Override
    public boolean positionChanged(AbstractMovableWorldMapElement worldMapElement, Vector2d oldPosition, Vector2d newPosition) {
        if (worldMapElement instanceof Animal animal) {
            MapAnimalContainer mapAnimalContainer = new MapAnimalContainer(animal.getEnergy(), animal);
            this.animals.get(oldPosition).remove(mapAnimalContainer);
            removeAnimalsEntryIfPossible(oldPosition);
            if (!this.animals.containsKey(newPosition)) {
                this.animals.put(newPosition, new TreeSet<>());
                this.incrementSlotsTaken(newPosition);
            }
            this.animals.get(newPosition).add(mapAnimalContainer);

        }
        else {
            this.map.remove(oldPosition);
            this.decrementSlotsTaken(oldPosition);
            this.map.put(newPosition, worldMapElement);
            this.incrementSlotsTaken(newPosition);
        }

        return true;
    }

    @Override
    public void energyChanged(Animal animal, int oldEnergy, int newEnergy) {
        MapAnimalContainer oldMapAnimalContainer = new MapAnimalContainer(oldEnergy, animal);
        var positionSet = this.animals.get(animal.getPosition());
        positionSet.remove(oldMapAnimalContainer);

        MapAnimalContainer newMapAnimalContainer = new MapAnimalContainer(newEnergy, animal);
        positionSet.add(newMapAnimalContainer);
    }

    private void removeAnimalsEntryIfPossible(Vector2d position) {
        if (this.animals.get(position).isEmpty()) {
            this.animals.remove(position);
            this.decrementSlotsTaken(position);
        }
    }

    public NavigableSet<MapAnimalContainer> getAnimalsAt(Vector2d position) {
        return this.animals.getOrDefault(position, new TreeSet<>());
    }

    public Optional<Pair<Animal, Animal>> getPairOfStrongestAnimalsAt(Vector2d position) {
        NavigableSet<MapAnimalContainer> allAnimals = this.getAnimalsAt(position);

        if (allAnimals.size() >= 2) {
            var iterator = allAnimals.descendingIterator();

            return Optional.of(new Pair<>(iterator.next().animal(), iterator.next().animal()));
        }

        return Optional.empty();
    }

    public void growGrass(int noOfTuftsInJungle, int noOfTuftsInSteppes) {
        int noOfGrownGrassTufts = 0;
        for (int i = 0; i < noOfTuftsInJungle; i++) {
            if (this.jungleBoundary.area() > this.jungleBoundary.getSlotsTaken()) {
                Vector2d grassPosition;
                do {
                    grassPosition = Vector2d.getRandomVectorBetween(
                            this.getJungleBoundary().lowerLeft(),
                            this.getJungleBoundary().upperRight());
                } while (this.isOccupied(grassPosition));

                this.map.put(grassPosition, new Grass(grassPosition));
                this.incrementSlotsTaken(grassPosition);

                noOfGrownGrassTufts++;
            }
        }

        for (int i = 0; i < noOfTuftsInSteppes; i++) {
            if (this.mapBoundary.area() - this.jungleBoundary.area() > this.mapBoundary.getSlotsTaken() - this.jungleBoundary.getSlotsTaken()) {
                Vector2d grassPosition;
                do {
                    grassPosition = Vector2d.getRandomVectorBetween(
                            this.getMapBoundary().lowerLeft(),
                            this.getMapBoundary().upperRight());
                } while (
                        this.getJungleBoundary().isInside(grassPosition) || this.isOccupied(grassPosition));

                this.map.put(grassPosition, new Grass(grassPosition));
                this.incrementSlotsTaken(grassPosition);

                noOfGrownGrassTufts++;
            }
        }

        for (var observer : this.grassActionObservers) {
            observer.grassGrow(noOfGrownGrassTufts);
        }
    }

    public AbstractWorldMapElement getMapElementAt(Vector2d position) {
        return this.map.get(position);
    }

    public void removeMapElementAt(Vector2d position) {
        var obj = this.map.remove(position);
        if (obj != null) {
            this.decrementSlotsTaken(position);

            if (obj instanceof Grass) {
                for (var observer : grassActionObservers) {
                    observer.grassEaten();
                }
            }
        }
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

    @Override
    public void animalBecameParent(Animal parent, Animal child) {

    }

    public abstract boolean canMoveTo(Vector2d position);
    public abstract Vector2d correctMovePosition(Vector2d oldPosition, Vector2d newPosition);
    public Vector2d getLowerLeftDrawLimit() {
        return this.mapBoundary.lowerLeft();
    }
    public Vector2d getUpperRightDrawLimit() {
        return this.mapBoundary.upperRight();
    }


    @Override
    public void animalDied(Animal animal) {
        this.animals.get(animal.getPosition()).remove(new MapAnimalContainer(animal.getEnergy(), animal));
        removeAnimalsEntryIfPossible(animal.getPosition());
    }
    public void addGrassObserver(IGrassActionObserver observer) {
        this.grassActionObservers.add(observer);
    }

    @Override
    public void animalCreated(Animal animal) {

    }

    public void removeGrassObserver(IGrassActionObserver observer) {
        this.grassActionObservers.remove(observer);
    }
}
