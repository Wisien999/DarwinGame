package DarwinGame.Simulation;

import DarwinGame.MapElements.Animal.IAnimalLifeObserver;
import DarwinGame.MapElements.AbstractWorldMapElement;
import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.MapElements.Animal.AnimalStatus;
import DarwinGame.MapElements.Grass;
import DarwinGame.Statistics.SimpleStatisticsHandler;
import DarwinGame.Vector2d;
import DarwinGame.WorldMap.AbstractWorldMap;
import DarwinGame.WorldMap.Boundary;
import DarwinGame.WorldMap.MapAnimalContainer;
import DarwinGame.gui.EvolutionType;
import DarwinGame.gui.IMapRefreshNeededObserver;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SimulationEngine implements IEngine, Runnable {
    private final AbstractWorldMap map;
    private final List<Animal> animals = new ArrayList<>();
    private final List<IMapRefreshNeededObserver> mapRefreshNeededObservers = new ArrayList<>();
    private SimpleStatisticsHandler simpleStatisticsHandler;
    private final List<IAnimalLifeObserver> animalLifeObservers = new ArrayList<>();
    private final List<INextDayObserver> nextDayObservers = new ArrayList<>();
    private final List<ISimulationEventObserver> simulationEventObservers = new ArrayList<>();
    private int dayNumber = 0;
    private int magicalRescuesLeft;
    private int moveDelay = SimulationConfig.simulationMoveDelay;


    public SimulationEngine(AbstractWorldMap map, SimpleStatisticsHandler statisticsHandler, EvolutionType evolutionType) {
        this(map, SimulationConfig.noOfStartingAnimals, evolutionType);

        simpleStatisticsHandler = statisticsHandler;

        for (Animal animal : this.animals) {
            animal.addLifeObserver(simpleStatisticsHandler);
            animal.addEnergyObserver(simpleStatisticsHandler);
        }
        this.map.addGrassObserver(simpleStatisticsHandler);
        this.addAnimalLifeObserver(simpleStatisticsHandler);
        for (Animal animal : this.animals) {
            animalCreated(animal);
        }
    }

    public SimulationEngine(AbstractWorldMap map, int noOfStartingAnimals, EvolutionType evolutionType) {
        if (evolutionType.equals(EvolutionType.MAGICAL)) {
             magicalRescuesLeft = SimulationConfig.maxMagicalRescue;
        }

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


    void duplicateAnimals() {
        List<Animal> newAnimals = new ArrayList<>();

        for (var animal : animals) {
            var genotype = animal.getGenotype();
            var newAnimalPosition = this.map.getRandomFreePosition();
            newAnimalPosition.ifPresent(pos -> newAnimals.add(
                    new Animal(this.map, pos, SimulationConfig.defaultAmountOfEnergyPoints, genotype)
            ));
        }

        for (var newAnimal : newAnimals) {
            this.map.place(newAnimal);
            this.animals.add(newAnimal);

            if (simpleStatisticsHandler != null) {
                newAnimal.addLifeObserver(simpleStatisticsHandler);
                newAnimal.addEnergyObserver(simpleStatisticsHandler);
            }
            animalCreated(newAnimal);
        }
    }

    public void setMoveDelay(int moveDelay) {
        if (moveDelay <= 0) {
            return;
        }

        this.moveDelay = moveDelay;
    }

    @Override
    public void run() {
        while(true) {
            makeSimulationStep();
            this.nextDay();
            this.mapRefreshNeeded();
            try {
                //noinspection BusyWait
                Thread.sleep(this.moveDelay);
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
                var limitAnimal = new MapAnimalContainer(theStrongestAnimal.animalEnergy(), null);
                int noOfTheStrongestAnimals = animals.tailSet(limitAnimal).size();
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
            child.addEnergyObserver(simpleStatisticsHandler);
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
        if (animals.size() == SimulationConfig.magicalRescueAnimalCountActivator && magicalRescuesLeft > 0) {
            duplicateAnimals();
            magicalRescuesLeft--;

            for (var observer : simulationEventObservers) {
                observer.magicalRescueHappened(magicalRescuesLeft);
            }
        }
        lowerEnergyLevelsAndRemoveDeadAnimals();
        makeTurnAction();
        feedAnimals();
        makeAnimalsProcreate();
        this.map.growGrass(1, 1);
    }

    public void addMapRefreshNeededObserver(IMapRefreshNeededObserver observer) {
        this.mapRefreshNeededObservers.add(observer);
    }

    private void mapRefreshNeeded() {
        for (var observer : this.mapRefreshNeededObservers) {
            observer.refresh(this.map);
        }
    }

    public void addAnimalLifeObserver(IAnimalLifeObserver observer) {
        this.animalLifeObservers.add(observer);
    }
    public void addNextDayObserver(INextDayObserver observer) {
        this.nextDayObservers.add(observer);
    }
    public void removeNextDayObserver(INextDayObserver observer) {
        this.nextDayObservers.remove(observer);
    }
    public void addSimulationEventObserver(ISimulationEventObserver observer) {
        this.simulationEventObservers.add(observer);
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

    public int getMagicalRescuesLeft() {
        return magicalRescuesLeft;
    }
}
