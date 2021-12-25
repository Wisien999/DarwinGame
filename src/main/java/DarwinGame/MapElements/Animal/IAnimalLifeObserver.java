package DarwinGame.MapElements.Animal;

import DarwinGame.MapElements.Animal.Animal;

public interface IAnimalLifeObserver {
    void animalCreated(Animal animal);
    void animalDied(Animal animal);
    void animalBecameParent(Animal parent, Animal child);
}
