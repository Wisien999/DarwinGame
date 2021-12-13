package DarwinGame;

import DarwinGame.MapElements.AbstractMovableWorldMapElement;
import DarwinGame.MapElements.Animal.Animal;

public interface IPositionChangeObserver {
    boolean positionChanged(AbstractMovableWorldMapElement worldMapElement, Vector2d oldPosition, Vector2d newPosition);
}
