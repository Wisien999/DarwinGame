package DarwinGame.Statistics;

import DarwinGame.IEnergyObserver;
import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.IAnimalLifeObserver;
import DarwinGame.MapElements.Animal.Genotype;
import DarwinGame.Simulation.INextDayObserver;
import DarwinGame.gui.IStatisticsObserver;
import com.google.common.collect.*;

import java.util.*;

public class SimpleStatisticsHandler implements IAnimalLifeObserver, IGrassActionObserver, IEnergyObserver, INextDayObserver {
    protected int noOfAliveAnimals = 0;
    protected int noOfGrassTufts = 0;
    protected double averageLifeSpan = 0;
    protected int noOfDeadAnimals = 0;
    protected double averageEnergy = 0;
    protected Map<Animal, Integer> animalChildrenCounter = new HashMap<>();
    protected double averageNoOfChildren = 0;
    protected Map<Genotype, Set<Animal>> genotypesAnimals = new HashMap<>();
    SortedMultiset<Genotype> sortedGenotypes = TreeMultiset.create(new GenotypeCountComparator(genotypesAnimals));


    protected List<IStatisticsObserver> statisticsObservers = new ArrayList<>();
    private int currentDayNumber;

    @Override
    public void animalDied(Animal animal) {
        var genotypeAnimals = genotypesAnimals.get(animal.getGenotype());
        genotypeAnimals.remove(animal);
        if (genotypeAnimals.isEmpty()) {
            genotypesAnimals.remove(animal.getGenotype());
        }
        sortedGenotypes.remove(animal.getGenotype());
        double energySum = averageEnergy * noOfAliveAnimals;
        energySum -= animal.getEnergy();

        int noOfChildren = animalChildrenCounter.getOrDefault(animal, 0);
        double noOfChildrenSum = averageNoOfChildren * noOfAliveAnimals;
        noOfChildrenSum -= noOfChildren;

        noOfAliveAnimals--;
        averageNoOfChildren = noOfChildrenSum / noOfAliveAnimals;
        averageEnergy = energySum / noOfAliveAnimals;

        double lifeSpanSum = averageLifeSpan * noOfDeadAnimals;
        noOfDeadAnimals++;
        lifeSpanSum += animal.getLifeSpan();
        this.averageLifeSpan = lifeSpanSum / noOfDeadAnimals;
    }

    @Override
    public void animalSuccessfulProcreation(Animal parent1, Animal parent2) {
        int oldNoOfChildren1 = animalChildrenCounter.getOrDefault(parent1, 0);
        int oldNoOfChildren2 = animalChildrenCounter.getOrDefault(parent2, 0);

        double noOfChildrenSum = averageNoOfChildren * noOfAliveAnimals;
        noOfChildrenSum += 2;
        averageNoOfChildren = noOfChildrenSum / noOfAliveAnimals;
    }

    @Override
    public void energyChanged(Animal animal, int oldEnergy, int newEnergy) {
        if (newEnergy <= 0) {
            return;
        }

        double energySum = averageEnergy * noOfAliveAnimals;
        energySum -= oldEnergy;
        energySum += newEnergy;
        this.averageEnergy = energySum / noOfAliveAnimals;
    }

    @Override
    public void animalCreated(Animal animal) {
        System.out.println("ANIMAL CREATED");
        genotypesAnimals.putIfAbsent(animal.getGenotype(), new HashSet<>());
        genotypesAnimals.get(animal.getGenotype()).add(animal);
        sortedGenotypes.add(animal.getGenotype());

        double energySum = averageEnergy * noOfAliveAnimals;
        double noOfChildrenSum = averageNoOfChildren * noOfAliveAnimals;

        energySum += animal.getEnergy();

        noOfAliveAnimals++;
        averageEnergy = energySum / noOfAliveAnimals;
        averageNoOfChildren = noOfChildrenSum / noOfAliveAnimals;
    }

    @Override
    public void grassEaten() {
        noOfGrassTufts--;
    }

    @Override
    public void grassGrow(int noOfTufts) {
        noOfGrassTufts += noOfTufts;
    }

    private void statisticsChanged() {
        for (var observer : statisticsObservers) {
            observer.refreshStatistic();
        }
    }

    public int getNoOfAliveAnimals() {
        return noOfAliveAnimals;
    }
    public int getNoOfGrassTufts() {
        return noOfGrassTufts;
    }
    public double getAverageLifeSpan() {
        return averageLifeSpan;
    }
    public int getNoOfDeadAnimals() {
        return noOfDeadAnimals;
    }
    public double getAverageEnergy() {
        return averageEnergy;
    }
    public Optional<Genotype> getDominantGenotype() {
        var entry = this.sortedGenotypes.lastEntry();
        if (entry == null) {
            return Optional.empty();
        }
        return Optional.of(entry.getElement());
    }
    public double getAverageNoOfChildren() {
        return averageNoOfChildren;
    }
    public int getCurrentDayNumber() {
        return currentDayNumber;
    }

    public void addStatisticsObserver(IStatisticsObserver observer) {
        this.statisticsObservers.add(observer);
    }
    public void removeStatisticsObserver(IStatisticsObserver observer) {
        this.statisticsObservers.remove(observer);
    }


    @Override
    public void nextDay(int dayNumber) {
        currentDayNumber = dayNumber;
        statisticsChanged();
    }
}
