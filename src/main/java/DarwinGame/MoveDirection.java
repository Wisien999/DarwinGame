package DarwinGame;

import java.util.Arrays;
import java.util.Optional;

public enum MoveDirection {
    FORWARD(0),
    TURN45(1),
    TURN90(2),
    TURN135(3),
    BACKWARD(4),
    TURN225(5),
    TURN270(6),
    TURN315(7);

    public final int numericalValue;
    MoveDirection(int value) {
        this.numericalValue = value;
    }

    public static Optional<MoveDirection> valueOf(int value) {
        return Arrays.stream(values())
                .filter(moveDirection -> moveDirection.numericalValue == value)
                .findFirst();
    }
}
