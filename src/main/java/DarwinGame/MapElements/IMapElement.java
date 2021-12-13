package DarwinGame.MapElements;


import DarwinGame.Vector2d;

public interface IMapElement {
    Vector2d getPosition();
    String toString();
    String getImageResource();
    public String toStringRepresentation();
}
