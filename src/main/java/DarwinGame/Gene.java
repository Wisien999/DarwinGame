package DarwinGame;

public record Gene(int value) implements Comparable<Gene> {
    @Override
    public int compareTo(Gene o) {
        return this.value - o.value;
    }
}
