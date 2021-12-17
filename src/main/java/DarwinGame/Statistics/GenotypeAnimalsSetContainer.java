package DarwinGame.Statistics;

import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.MapElements.Animal.Genotype;

import java.util.List;

public class GenotypeAnimalsSetContainer implements Comparable<GenotypeAnimalsSetContainer> {
    public Genotype genotype;
    public List<Animal> animals;


    @Override
    public int compareTo(GenotypeAnimalsSetContainer o) {
        if (animals.size() == o.animals.size()) {

        }

        return animals.size() - o.animals.size();
    }
}
