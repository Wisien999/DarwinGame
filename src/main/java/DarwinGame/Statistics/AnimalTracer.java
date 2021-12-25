package DarwinGame.Statistics;

import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.MapElements.Animal.IAnimalLifeObserver;
import DarwinGame.Simulation.INextDayObserver;
import jdk.dynalink.linker.LinkerServices;

import java.util.ArrayList;
import java.util.List;

public class AnimalTracer implements IAnimalLifeObserver, INextDayObserver {
    private final Animal tracedAnimal;
    private int deathDay;
    private final List<Animal> children = new ArrayList<>();
    private final List<Animal> descendants = new ArrayList<>();

    private int currentDayNumber;


    public AnimalTracer(Animal tracedAnimal) {
        this.tracedAnimal = tracedAnimal;
    }

    @Override
    public void animalBecameParent(Animal parent, Animal child) {
        if (parent.equals(tracedAnimal)) {
            children.add(child);
        }
        descendants.add(child);


        child.addLifeObserver(this);
    }

    @Override
    public void animalDied(Animal animal) {
        if (animal.equals(tracedAnimal)) {
            deathDay = currentDayNumber;
        }
    }


    @Override
    public void animalCreated(Animal animal) {
    }

    @Override
    public void nextDay(int dayNumber) {
        currentDayNumber = dayNumber;
    }
}
