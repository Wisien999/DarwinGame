package DarwinGame;

import DarwinGame.MapElements.Animal.Animal;

public interface IAnimalDeathObserver {
    void animalDied(Animal animal);
}
