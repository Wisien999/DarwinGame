package DarwinGame.MapElements.Animal;

import java.util.Comparator;

public class AnimalEnergyComparator implements Comparator<Animal> {
    @Override
    public int compare(Animal o1, Animal o2) {
        return o1.getEnergy() - o2.getEnergy();
    }
}
