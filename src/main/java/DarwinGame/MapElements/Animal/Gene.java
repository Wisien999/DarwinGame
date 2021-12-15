package DarwinGame.MapElements.Animal;

import java.util.Arrays;
import java.util.Optional;

public enum Gene implements Comparable<Gene> {
    FORWARD(0),
    TURN45(1),
    TURN90(2),
    TURN135(3),
    BACKWARD(4),
    TURN225(5),
    TURN270(6),
    TURN315(7);

    final int numericalValue;
    Gene(int value) {
        this.numericalValue = value;
    }

    public static Optional<Gene> valueOf(int value) {
        return Arrays.stream(values())
                .filter(legNo -> legNo.numericalValue == value)
                .findFirst();
    }
}
