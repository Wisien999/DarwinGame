package DarwinGame;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public record Vector2d(int x, int y) {
    public static Vector2d getRandomVectorBetween(Vector2d lowerLeft, Vector2d upperRight) {
        int x = ThreadLocalRandom.current().nextInt(lowerLeft.x(), upperRight.x() + 1);
        int y = ThreadLocalRandom.current().nextInt(lowerLeft.y(), upperRight.y() + 1);

        return new Vector2d(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public boolean precedes(Vector2d other) {
        return this.x <= other.x && this.y <= other.y;
    }

    public boolean follows(Vector2d other) {
        return this.x >= other.x && this.y >= other.y;
    }

    public Vector2d upperRight(Vector2d other) {
        final int rightX = Math.max(this.x, other.x);
        final int upperY = Math.max(this.y, other.y);

        return new Vector2d(rightX, upperY);
    }

    public Vector2d lowerLeft(Vector2d other) {
        final int leftX = Math.min(this.x, other.x);
        final int lowerY = Math.min(this.y, other.y);

        return new Vector2d(leftX, lowerY);
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(this.x + other.x, this.y + other.y);
    }
    public Vector2d subtract(Vector2d other) {
        return new Vector2d(this.x - other.x, this.y - other.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2d vector2d = (Vector2d) o;
        return x == vector2d.x && y == vector2d.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    public Vector2d opposite() {
        return new Vector2d(-this.x, -this.y);
    }
}
