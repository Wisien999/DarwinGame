package DarwinGame.MapElements;

import DarwinGame.MapElements.Animal.IPositionChangeObserver;
import DarwinGame.MoveDirection;
import DarwinGame.Vector2d;

import java.util.ArrayList;
import java.util.List;

abstract public class AbstractMovableWorldMapElement extends AbstractWorldMapElement {
    private final List<IPositionChangeObserver> positionObservers = new ArrayList<>();

    public AbstractMovableWorldMapElement(Vector2d position) {
        super(position);
    }

    public abstract void move(MoveDirection direction);

    public void addPositionObserver(IPositionChangeObserver observer) {
        this.positionObservers.add(observer);
    }
    public void removePositionObserver(IPositionChangeObserver observer) {
        this.positionObservers.remove(observer);
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        for (IPositionChangeObserver observer : this.positionObservers) {
            observer.positionChanged(this, oldPosition, newPosition);
        }
    }

}
