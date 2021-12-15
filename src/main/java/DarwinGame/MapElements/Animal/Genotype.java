package DarwinGame.MapElements.Animal;

import DarwinGame.MoveDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Genotype {
    private List<MoveDirection> genes;
    public static final int supposedLength = 32;

    Genotype(List<MoveDirection> genes) {
        this.genes = genes;
        Collections.sort(this.genes);
    }

    Genotype(List<MoveDirection> genes1, List<MoveDirection> genes2) {
        this(Stream.concat(genes1.stream(), genes2.stream()).collect(Collectors.toList()));
    }

    public List<MoveDirection> getLeftSlice(int n) {
        n = Math.min(this.genes.size(), n);
        return this.genes.subList(0, n);
    }
    public List<MoveDirection> getRightSlice(int n) {
        n = Math.min(this.genes.size(), n);
        return this.genes.subList(this.genes.size() - n, this.genes.size());
    }

    public int getGenotypeLength() {
        return this.genes.size();
    }
    public MoveDirection getGeneAt(int index) {
        return this.genes.get(index);
    }
}

