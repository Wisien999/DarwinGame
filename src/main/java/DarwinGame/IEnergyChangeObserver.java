package DarwinGame;

import DarwinGame.MapElements.Animal.Animal;

public interface IEnergyChangeObserver {
    void energyChanged(Animal animal, int oldEnergy, int newEnergy);
}
