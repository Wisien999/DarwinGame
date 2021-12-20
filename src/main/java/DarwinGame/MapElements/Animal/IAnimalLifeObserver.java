package DarwinGame.MapElements.Animal;

import DarwinGame.MapElements.Animal.Animal;

public interface IAnimalLifeObserver {
    void animalCreated(Animal animal);
    void animalDied(Animal animal);
    void animalSuccessfulProcreation(Animal parent1, Animal parent2);
}
