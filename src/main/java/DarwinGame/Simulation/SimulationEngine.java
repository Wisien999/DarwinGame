package DarwinGame.Simulation;

import DarwinGame.IAnimalLifeObserver;
import DarwinGame.MapElements.AbstractWorldMapElement;
import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.MapElements.Animal.AnimalStatus;
import DarwinGame.MapElements.Grass;
import DarwinGame.Statistics.SimpleStatisticsHandler;
import DarwinGame.Vector2d;
import DarwinGame.WorldMap.AbstractWorldMap;
import DarwinGame.WorldMap.Boundary;
import DarwinGame.gui.IMapRefreshNeededObserver;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SimulationEngine implements IEngine, Runnable {
    private final AbstractWorldMap map;
    private final List<Animal> animals = new ArrayList<>();
    private final List<Animal> deadAnimals = new ArrayList<>();
    private final List<IMapRefreshNeededObserver> mapRefreshNeededObservers = new ArrayList<>();
    private SimpleStatisticsHandler simpleStatisticsHandler;
    private final List<IAnimalLifeObserver> animalLifeObservers = new ArrayList<>();
    private final List<INextDayObserver> nextDayObservers = new ArrayList<>();
    private int dayNumber = 0;

    public SimulationEngine(AbstractWorldMap map) {
        this(map, SimulationConfig.noOfStartingAnimals);
    }

    public SimulationEngine(AbstractWorldMap map, SimpleStatisticsHandler statisticsHandler) {
        this(map, SimulationConfig.noOfStartingAnimals);

        simpleStatisticsHandler = statisticsHandler;

        for (Animal animal : this.animals) {
            animal.addLifeObserver(simpleStatisticsHandler);
            animal.addEnergyObserver(simpleStatisticsHandler);
        }
        this.map.addGrassObserver(simpleStatisticsHandler);
        this.addAnimalLifeObserver(statisticsHandler);
        for (Animal animal : this.animals) {
            animalCreated(animal);
        }
    }

    public SimulationEngine(AbstractWorldMap map, int noOfStartingAnimals) {
        this.map = map;

        Boundary mapBoundary = this.map.getMapBoundary();

        for (int i = 0; i < noOfStartingAnimals; i++) {
            Vector2d animalPosition;
            do {
                int x = ThreadLocalRandom.current().nextInt(mapBoundary.lowerLeft().x(), mapBoundary.upperRight().x() + 1);
                int y = ThreadLocalRandom.current().nextInt(mapBoundary.lowerLeft().y(), mapBoundary.upperRight().y() + 1);

                animalPosition = new Vector2d(x, y);
            } while (this.map.isOccupied(animalPosition));

            Animal animal = new Animal(this.map, animalPosition);
            this.map.place(animal);
            this.animals.add(animal);
        }
    }

    @Override
    public void run() {
        while(true) {
            makeSimulationStep();
            this.nextDay();
            this.mapRefreshNeeded();
            try {
                //noinspection BusyWait
                Thread.sleep(SimulationConfig.simulationMoveDelay);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void lowerEnergyLevelsAndRemoveDeadAnimals() {
        List<Animal> newDeadAnimals = new ArrayList<>();
        for (Animal animal : this.animals) {
            animal.nextDay();
            if (animal.getStatus() == AnimalStatus.DEAD) {
                newDeadAnimals.add(animal);
            }
        }

        this.animals.removeAll(newDeadAnimals);
        this.deadAnimals.addAll(newDeadAnimals);

    }

    private void feedAnimals() {
        Set<Vector2d> eatenGrassPosition = new HashSet<>();
        for (Animal animal : this.animals) {
            AbstractWorldMapElement worldMapElement = this.map.getMapElementAt(animal.getPosition());
            if (worldMapElement instanceof Grass) {
                eatenGrassPosition.add(worldMapElement.getPosition());

                var animals = this.map.getAnimalsAt(animal.getPosition());
                var theStrongestAnimal = animals.last();
                if (animal.getEnergy() != theStrongestAnimal.animalEnergy()) {
                    continue;
                }
                int noOfTheStrongestAnimals = animals.tailSet(theStrongestAnimal).size();
                animal.feed(SimulationConfig.plantEnergy / noOfTheStrongestAnimals);
            }
        }

        for (Vector2d grassPosition : eatenGrassPosition) {
            this.map.removeMapElementAt(grassPosition);
        }
    }

    private void makeAnimalsProcreate() {
        Set<Vector2d> usedPositions = new HashSet<>();
        List<Animal> children = new ArrayList<>();

        for (Animal animal : this.animals) {
            if (usedPositions.contains(animal.getPosition())) {
                continue;
            }

            usedPositions.add(animal.getPosition());

            var topPair = this.map.getPairOfStrongestAnimalsAt(animal.getPosition());
            topPair.ifPresent(animalPair -> {
                var animal1 = animalPair.getKey();
                var animal2 = animalPair.getValue();

                if (animal1.canProcreate(animal2)) {
                    Animal child = animal1.procreate(animal2);
                    children.add(child);
                }
            });
        }

        for (Animal child : children) {
            animalCreated(child);
            child.addLifeObserver(simpleStatisticsHandler);
            this.map.place(child);
        }
        this.animals.addAll(children);
    }

    private void makeTurnAction() {
        for (Animal animal : this.animals) {
            animal.makeTurnAction();
        }
    }

    private void makeSimulationStep() {
        lowerEnergyLevelsAndRemoveDeadAnimals();
        makeTurnAction();
        feedAnimals();
        makeAnimalsProcreate();
        this.map.growGrass(1, 1);
    }

    public void addMapRefreshNeededObserver(IMapRefreshNeededObserver observer) {
        this.mapRefreshNeededObservers.add(observer);
    }
    public void removeMapRefreshNeededObservers(IMapRefreshNeededObserver observer) {
        this.mapRefreshNeededObservers.remove(observer);
    }

    private void mapRefreshNeeded() {
        for (var observer : this.mapRefreshNeededObservers) {
            observer.refresh(this.map);
        }
    }

    public void addAnimalLifeObserver(IAnimalLifeObserver observer) {
        this.animalLifeObservers.add(observer);
    }
    public void removeAnimalLifeObserver(IAnimalLifeObserver observer) {
        this.animalLifeObservers.remove(observer);
    }
    public void addNextDayObserver(INextDayObserver observer) {
        this.nextDayObservers.add(observer);
    }
    public void removeNextDayObserver(INextDayObserver observer) {
        this.nextDayObservers.remove(observer);
    }
    private void animalCreated(Animal animal) {
        for (var observer : animalLifeObservers) {
            observer.animalCreated(animal);
        }
    }
    private void nextDay() {
        dayNumber++;
        for (var observer : this.nextDayObservers) {
            observer.nextDay(dayNumber);
        }
    }
}
