package DarwinGame.Statistics;

import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.MapElements.Animal.IAnimalLifeObserver;
import DarwinGame.Simulation.INextDayObserver;


public class AnimalTracer implements IAnimalLifeObserver, INextDayObserver {
    private final Animal tracedAnimal;
    private int deathDay = -1;
    private int noOfChildren = 0;
    private int noOfDescendants = 0;

    private int currentDayNumber;


    public AnimalTracer(Animal tracedAnimal) {
        this.tracedAnimal = tracedAnimal;
        tracedAnimal.addLifeObserver(this);
    }

    @Override
    public void animalBecameParent(Animal parent, Animal child) {
        if (parent.equals(tracedAnimal)) {
            noOfChildren++;
        }
        noOfDescendants++;


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

    public int getDeathDay() {
        return deathDay;
    }
    public int getNoOfChildren() {
        return noOfChildren;
    }
    public int getNoOfDescendants() {
        return noOfDescendants;
    }
}
