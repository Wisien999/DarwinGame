package DarwinGame.MapElements.Animal;

import DarwinGame.MapElements.Animal.Animal;

public interface IEnergyObserver {
    void energyChanged(Animal animal, int oldEnergy, int newEnergy);
}
