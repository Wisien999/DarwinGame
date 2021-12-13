package DarwinGame.MapElements.Animal;

import DarwinGame.Gene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Genotype {
    private List<Gene> genes = new ArrayList<>();
    public static final int supposedLength = 32;

    Genotype(List<Gene> genes) {
        this.genes = genes;
        Collections.sort(this.genes);
    }

    Genotype(List<Gene> genes1, List<Gene> genes2) {
        this(Stream.concat(genes1.stream(), genes2.stream()).collect(Collectors.toList()));
    }

    public List<Gene> getLeftSlice(int n) {
        n = Math.min(this.genes.size(), n);
        return this.genes.subList(0, n);
    }
    public List<Gene> getRightSlice(int n) {
        n = Math.min(this.genes.size(), n);
        return this.genes.subList(this.genes.size() - n, this.genes.size());
    }

    public int getGenotypeLength() {
        return this.genes.size();
    }
}

