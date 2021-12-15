package DarwinGame.gui;

import DarwinGame.WorldMap.AbstractWorldMap;

public interface IMapRefreshNeededObserver {
    void refresh(AbstractWorldMap map);
}
