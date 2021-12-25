package DarwinGame.MapElements.Animal;

import DarwinGame.MapElements.AbstractMovableWorldMapElement;
import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.Vector2d;

public interface IPositionChangeObserver {
    boolean positionChanged(AbstractMovableWorldMapElement worldMapElement, Vector2d oldPosition, Vector2d newPosition);
}
