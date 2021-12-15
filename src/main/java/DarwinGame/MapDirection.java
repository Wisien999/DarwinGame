package DarwinGame;

import java.util.List;
import java.util.Random;

public enum MapDirection {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;

    private static final List<MapDirection> values = List.of(values());
    private static final Random random = new Random();

    public static MapDirection randomMapDirection()  {
        return values.get(random.nextInt(values.size()));
    }

    @Override
    public String toString() {
        String firstLetter = this.name().substring(0, 1);
        if (this.name().length() >= 5) {
            return firstLetter + this.name().charAt(4);
        }
        return firstLetter;
    }

    public MapDirection next() {
        return switch (this) {
            case NORTH -> MapDirection.NORTHEAST;
            case NORTHEAST -> MapDirection.EAST;
            case EAST -> MapDirection.SOUTHEAST;
            case SOUTHEAST -> MapDirection.SOUTH;
            case SOUTH -> MapDirection.SOUTHWEST;
            case SOUTHWEST -> MapDirection.WEST;
            case WEST -> MapDirection.NORTHWEST;
            case NORTHWEST -> MapDirection.NORTH;
        };
    }
    public MapDirection previous() {
        return switch (this) {
            case NORTH -> MapDirection.NORTHWEST;
            case NORTHEAST -> MapDirection.NORTH;
            case EAST -> MapDirection.NORTHEAST;
            case SOUTHEAST -> MapDirection.EAST;
            case SOUTH -> MapDirection.SOUTHEAST;
            case SOUTHWEST -> MapDirection.SOUTH;
            case WEST -> MapDirection.SOUTHWEST;
            case NORTHWEST -> MapDirection.WEST;
        };
    }
    public Vector2d toUnitVector() {
        return switch (this) {
            case NORTH -> new Vector2d(0, 1);
            case NORTHEAST -> new Vector2d(1, 1);
            case EAST -> new Vector2d(1, 0);
            case SOUTHEAST -> new Vector2d(1, -1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTHWEST -> new Vector2d(-1, -1);
            case WEST -> new Vector2d(-1, 0);
            case NORTHWEST -> new Vector2d(-1, 1);
        };
    }
}
