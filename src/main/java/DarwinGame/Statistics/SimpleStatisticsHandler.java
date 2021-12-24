package DarwinGame.Statistics;

import DarwinGame.IEnergyObserver;
import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.MapElements.Animal.IAnimalLifeObserver;
import DarwinGame.MapElements.Animal.Genotype;
import DarwinGame.Simulation.INextDayObserver;
import DarwinGame.Vector2d;
import DarwinGame.gui.IStatisticsObserver;
import com.sun.javafx.sg.prism.DirtyHint;

import java.net.InterfaceAddress;
import java.util.*;

public class SimpleStatisticsHandler implements IAnimalLifeObserver, IGrassActionObserver, IEnergyObserver, INextDayObserver {
    protected int noOfAliveAnimals = 0;
    protected int noOfGrassTufts = 0;
    protected double averageLifeSpan = 0;
    protected int noOfDeadAnimals = 0;
    protected double averageEnergy = 0;
    protected Map<Animal, Integer> animalChildrenCounter = new HashMap<>();
    protected double averageNoOfChildren = 0;
    protected Map<Genotype, HashSet<Animal>> genotypesAnimals = new HashMap<>();
    protected SortedMap<Integer, HashSet<Genotype>> sortedGenotypes = new TreeMap<>();

    protected List<Integer> aliveAnimalsHistory = new ArrayList<>();
    protected List<Integer> grassTuftsHistory = new ArrayList<>();
    protected List<Double> averageLifeSpanHistory = new ArrayList<>();
    protected List<Double> averageEnergyHistory = new ArrayList<>();
    protected List<Double> averageNoOfChildrenHistory = new ArrayList<>();

    protected List<IStatisticsObserver> statisticsObservers = new ArrayList<>();
    private int currentDayNumber;

    @Override
    public void animalDied(Animal animal) {
        Integer animalGenotypeCount = genotypesAnimals.get(animal.getGenotype()).size();
        var countSet = sortedGenotypes.get(animalGenotypeCount);
        countSet.remove(animal.getGenotype());
        if (countSet.isEmpty()) {
            sortedGenotypes.remove(animalGenotypeCount);
        }
        animalGenotypeCount--;
        if (animalGenotypeCount > 0) {
            sortedGenotypes.putIfAbsent(animalGenotypeCount, new HashSet<>());
            sortedGenotypes.get(animalGenotypeCount).add(animal.getGenotype());
        }

        var genotypeAnimals = genotypesAnimals.get(animal.getGenotype());
        genotypeAnimals.remove(animal);
        if (genotypeAnimals.isEmpty()) {
            genotypesAnimals.remove(animal.getGenotype());
        }
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
        double energySum = averageEnergy * noOfAliveAnimals;
        energySum -= oldEnergy;
        energySum += newEnergy;
        this.averageEnergy = energySum / noOfAliveAnimals;
    }

    @Override
    public void animalCreated(Animal animal) {
        Integer animalGenotypeCount = 0;
        if (genotypesAnimals.containsKey(animal.getGenotype())) {
            animalGenotypeCount = genotypesAnimals.get(animal.getGenotype()).size();
            sortedGenotypes.getOrDefault(animalGenotypeCount, new HashSet<>()).remove(animal.getGenotype());
            if (sortedGenotypes.getOrDefault(animalGenotypeCount, new HashSet<>()).isEmpty()) {
                sortedGenotypes.remove(animalGenotypeCount);
            }
        }

        genotypesAnimals.putIfAbsent(animal.getGenotype(), new HashSet<>());
        genotypesAnimals.get(animal.getGenotype()).add(animal);


        animalGenotypeCount++;
        sortedGenotypes.putIfAbsent(animalGenotypeCount, new HashSet<>());
        sortedGenotypes.get(animalGenotypeCount).add(animal.getGenotype());


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
        if (sortedGenotypes.isEmpty()) {
            return Optional.empty();
        }
        Integer maxGenotypeCount = sortedGenotypes.lastKey();
        return sortedGenotypes.get(maxGenotypeCount).stream().findFirst();
    }
    public double getAverageNoOfChildren() {
        return averageNoOfChildren;
    }
    public int getCurrentDayNumber() {
        return currentDayNumber;
    }
    public Set<Animal> getAnimalsOfDominantGenotype() {
        if (sortedGenotypes.isEmpty()) {
            return new HashSet<>();
        }
        Integer maxGenotypeCount = sortedGenotypes.lastKey();
        var dominantGenotype = sortedGenotypes.get(maxGenotypeCount).stream().findFirst();
        if (dominantGenotype.isEmpty()) {
            return new HashSet<>();
        }
        return genotypesAnimals.get(dominantGenotype.get());
    }

    public void addStatisticsObserver(IStatisticsObserver observer) {
        this.statisticsObservers.add(observer);
    }
    public void removeStatisticsObserver(IStatisticsObserver observer) {
        this.statisticsObservers.remove(observer);
    }


    @Override
    public void nextDay(int dayNumber) {
        averageEnergyHistory.add(averageEnergy);
        averageNoOfChildrenHistory.add(averageNoOfChildren);
        averageLifeSpanHistory.add(averageLifeSpan);
        aliveAnimalsHistory.add(noOfAliveAnimals);
        grassTuftsHistory.add(noOfGrassTufts);

        currentDayNumber = dayNumber;
        statisticsChanged();
    }

    public List<Integer> getAliveAnimalsHistory() {
        return aliveAnimalsHistory;
    }
    public List<Integer> getGrassTuftsHistory() {
        return grassTuftsHistory;
    }
    public List<Double> getAverageLifeSpanHistory() {
        return averageLifeSpanHistory;
    }
    public List<Double> getAverageEnergyHistory() {
        return averageEnergyHistory;
    }
    public List<Double> getAverageNoOfChildrenHistory() {
        return averageNoOfChildrenHistory;
    }
}
