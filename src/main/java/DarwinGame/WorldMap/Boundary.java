package DarwinGame.WorldMap;

import DarwinGame.Vector2d;

public record Boundary(Vector2d lowerLeft, Vector2d upperRight) {
    public boolean isInside(Vector2d position) {
        return position != null && position.precedes(this.upperRight) && position.follows(this.lowerLeft);
    }
}
