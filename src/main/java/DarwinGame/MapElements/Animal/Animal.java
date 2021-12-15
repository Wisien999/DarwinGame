package DarwinGame.MapElements.Animal;

import DarwinGame.*;
import DarwinGame.MapElements.AbstractMovableWorldMapElement;
import DarwinGame.Simulation.SimulationConfig;
import DarwinGame.WorldMap.AbstractWorldMap;
import DarwinGame.gui.PathConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Animal extends AbstractMovableWorldMapElement {
    public final int id = IdGenerator.ID_GENERATOR.getAndIncrement();
    private MapDirection orientation = MapDirection.NORTH;
    private int energy;
    private AnimalStatus status = AnimalStatus.ALIVE;
    private final AbstractWorldMap map;
    private final Genotype genotype;
    private final List<IEnergyChangeObserver> energyObservers = new ArrayList<>();

    public Animal(AbstractWorldMap map, Vector2d initialPosition) {
        super(initialPosition);
        List<MoveDirection> genes = new ArrayList<>();
        for (int i = 0; i < Genotype.supposedLength; i++) {
            double randNum = Math.random()*8;
            genes.add(MoveDirection.valueOf((int) randNum).orElse(MoveDirection.TURN315));
        }

        this.map = map;
        this.energy = SimulationConfig.defaultAmountOfEnergyPoints;
        this.genotype = new Genotype(genes);
        this.addEnergyObserver(map);
    }

    public Animal(AbstractWorldMap map, Vector2d initialPosition, int startingEnergy, Genotype genotype) {
        super(initialPosition);
        this.map = map;
        this.energy = startingEnergy;
        this.genotype = genotype;
        this.addEnergyObserver(map);
    }

    public void move(MoveDirection direction) {
        switch (direction) {
            case FORWARD, BACKWARD -> {
                Vector2d orientationVector = this.orientation.toUnitVector();
                Vector2d newPosition = switch (direction) {
                    case FORWARD -> this.position.add(orientationVector);
                    case BACKWARD -> this.position.subtract(orientationVector);
                    default -> this.position;
                };
                if (this.map.canMoveTo(newPosition)) {
                    newPosition = this.map.correctMovePosition(this.getPosition(), newPosition);
                    this.positionChanged(this.position, newPosition);
                    this.position = newPosition;
                }
            }
            default -> {
                int noOf45turns = direction.numericalValue;
                for (int i = 0; i < noOf45turns; i++) {
                    this.orientation = this.orientation.next();
                }
            }
        }
    }

    public boolean canProcreate(Animal otherAnimal) {
        return otherAnimal.getPosition().equals(this.getPosition()) &&
                this.getEnergy() >= SimulationConfig.minimalAmountOfEnergyToProcreate &&
                otherAnimal.getEnergy() >= SimulationConfig.minimalAmountOfEnergyToProcreate
                ;
    }

    public Animal procreate(Animal otherAnimal) {
        if (!this.canProcreate(otherAnimal)) {
            throw new IllegalArgumentException("These animals can't procreate");
        }

        int noOfOwnGenes = this.getEnergy() / (this.getEnergy() + otherAnimal.getEnergy());
        int noOfOtherGenes = this.genotype.getGenotypeLength() - noOfOwnGenes;

        Genotype newGenotype;
        if (noOfOtherGenes > noOfOwnGenes) {
            if (Math.random() < 0.5) { // genes of stronger animal go to the left
                newGenotype = new Genotype(otherAnimal.getGenotype().getLeftSlice(noOfOtherGenes),
                                            this.getGenotype().getRightSlice(noOfOwnGenes));
            }
            else {
                newGenotype = new Genotype(this.getGenotype().getLeftSlice(noOfOwnGenes),
                                            otherAnimal.getGenotype().getRightSlice(noOfOtherGenes));
            }
        }
        else {
            if (Math.random() < 0.5) { // genes of stronger animal go to the left
                newGenotype = new Genotype(this.getGenotype().getLeftSlice(noOfOtherGenes),
                                            otherAnimal.getGenotype().getRightSlice(noOfOwnGenes));
            }
            else {
                newGenotype = new Genotype(otherAnimal.getGenotype().getLeftSlice(noOfOwnGenes),
                                            this.getGenotype().getRightSlice(noOfOtherGenes));
            }
        }

        int thisEnergyCost = (int) (this.getEnergy() * SimulationConfig.procreationEnergyCostFraction);
        int otherEnergyCost = (int) (otherAnimal.getEnergy() * SimulationConfig.procreationEnergyCostFraction);
        this.setEnergy(this.getEnergy() - thisEnergyCost);
        otherAnimal.setEnergy(otherAnimal.getEnergy() - otherEnergyCost);

        Animal child = new Animal(this.map, this.position, thisEnergyCost + otherEnergyCost, newGenotype);
        this.map.place(child);

        return child;
    }

    public String toString() {
        return "A " + this.orientation.toString() + " " + this.position.toString();
    }

    @Override
    public String getImageResource() {
        String basePath = PathConfig.imageBasePath;
        return basePath + "animal/" + switch (this.getOrientation()) {
            case NORTH -> "0.png";
            case NORTHEAST -> "45.png";
            case EAST -> "90.png";
            case SOUTHEAST -> "135.png";
            case SOUTH -> "180.png";
            case SOUTHWEST -> "225.png";
            case WEST -> "270.png";
            case NORTHWEST -> "315.png";
        };
    }

    public void nextDay() {
        this.setEnergy(this.getEnergy() - SimulationConfig.simulationDayEnergyCost);
    }

    public void makeTurnAction() {
        int index = new Random().nextInt(this.genotype.getGenotypeLength());
        MoveDirection geneToUse = this.genotype.getGeneAt(index);

        this.move(geneToUse);
    }

    public void feed(int energyPoints) {
        this.setEnergy(this.getEnergy() + energyPoints);
    }

    public MapDirection getOrientation() {return orientation;}
    public Vector2d getPosition() {return position;}
    public int getEnergy() {
        return energy;
    }
    public Genotype getGenotype() {
        return this.genotype;
    }

    protected void setEnergy(int energy) {
        for (IEnergyChangeObserver observer : this.energyObservers) {
            observer.energyChanged(this, this.energy, energy);
        }
        this.energy = energy;
        if (this.getEnergy() <= 0) {
            this.status = AnimalStatus.DEAD;
        }
    }

    public AnimalStatus getStatus() {
        return status;
    }

    public void addEnergyObserver(IEnergyChangeObserver observer) {
        this.energyObservers.add(observer);
    }
    public void removeEnergyObserver(IEnergyChangeObserver observer) {
        this.energyObservers.remove(observer);
    }
}
