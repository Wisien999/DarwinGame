package DarwinGame.WorldMap;

import DarwinGame.MapElements.Animal.Animal;

public record MapAnimalContainer(int animalEnergy, Animal animal) implements Comparable<MapAnimalContainer> {

    @Override
    public int compareTo(MapAnimalContainer o) {
        return this.animalEnergy() - o.animalEnergy();
    }
}
