package DarwinGame.MapElements.Animal;

import DarwinGame.*;
import DarwinGame.MapElements.AbstractMovableWorldMapElement;
import DarwinGame.Simulation.SimulationConfig;
import DarwinGame.gui.PathConfig;
import DarwinGame.WorldMap.IWorldMap;

import java.util.ArrayList;
import java.util.List;


public class Animal extends AbstractMovableWorldMapElement {
    private MapDirection orientation = MapDirection.NORTH;
    private int energy;
    private final IWorldMap map;
    private final Genotype genotype;

    public Animal(IWorldMap map, Vector2d initialPosition) {
        super(initialPosition);
        List<Gene> genes = new ArrayList<Gene>();
        for (int i = 0; i < Genotype.supposedLength; i++) {
            double randNum = Math.random()*8;
            if (randNum >= 8.0) {
                randNum = 7.0;
            }
            genes.add(new Gene((int) randNum));
        }

        this.map = map;
        this.energy = SimulationConfig.defaultAmountOfEnergyPoints;
        this.genotype = new Genotype(genes);
    }

    public Animal(IWorldMap map, Vector2d initialPosition, int startingEnergy, Genotype genotype) {
        super(initialPosition);
        this.map = map;
        this.energy = startingEnergy;
        this.genotype = genotype;
    }

    public void move(MoveDirection direction) {
        Vector2d orientationVector = this.orientation.toUnitVector();


        switch (direction) {
            case LEFT -> this.orientation = this.orientation.previous();
            case RIGHT -> this.orientation = this.orientation.next();
            default -> {
                Vector2d newPosition = switch (direction) {
                    case FORWARD -> this.position.add(orientationVector);
                    case BACKWARD -> this.position.add(orientationVector.opposite());
                    default -> this.position;
                };
                if (this.map.canMoveTo(newPosition)) {
                    this.positionChanged(this.position, newPosition);
                    this.position = newPosition;
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

    public void procreate(Animal otherAnimal) {
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
    }

    public String toStringRepresentation() {
        return "A " + this.orientation.toString() + " " + this.position.toString();
    }

    public String toString() {
        return this.orientation.toString();
    }

    @Override
    public String getImageResource() {
        String basePath = PathConfig.imageBasePath;
        return basePath + switch (this.getOrientation()) {
            case NORTH -> "up.png";
            case EAST -> "right.png";
            case SOUTH -> "down.png";
            case WEST -> "left.png";
            default -> "animal.png";
        };
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
        this.energy = energy;
    }
}
