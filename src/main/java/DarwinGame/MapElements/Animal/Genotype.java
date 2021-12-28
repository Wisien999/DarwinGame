package DarwinGame.MapElements.Animal;

import DarwinGame.MoveDirection;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Genotype implements Comparable<Genotype> {
    private final List<MoveDirection> genes;
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

    @Override
    public int compareTo(@NotNull Genotype o) {
        for (int i = 0; i < Genotype.supposedLength; i++) {
            if (genes.get(i).compareTo(o.genes.get(i)) != 0) {
                return genes.get(i).compareTo(o.genes.get(i));
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder(32);
        genes.forEach(gene -> stringBuilder.append(gene.numericalValue));
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genotype genotype = (Genotype) o;
        return genes.equals(genotype.genes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genes);
    }
}

