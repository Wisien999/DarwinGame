package DarwinGame.Statistics;

import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.MapElements.Animal.Genotype;

import java.util.*;

public class GenotypeCountComparator implements Comparator<Genotype> {
    private final Map<Genotype, Set<Animal>> genotypeAnimals;

    public GenotypeCountComparator(Map<Genotype, Set<Animal>> genotypeAnimals) {
        this.genotypeAnimals = genotypeAnimals;
    }

    @Override
    public int compare(Genotype o1, Genotype o2) {
        return genotypeAnimals.getOrDefault(o1, new HashSet<>()).size() - genotypeAnimals.getOrDefault(o2, new HashSet<>()).size();
    }
}
