package DarwinGame.WorldMap;

import DarwinGame.MapElements.Animal.Animal;

public record MapAnimalContainer(int animalEnergy, Animal animal) implements Comparable<MapAnimalContainer> {

    @Override
    public int compareTo(MapAnimalContainer o) {
        if (o.animal() == null) {
            return 1;
        }
        if (this.animal() == null) {
            return -1;
        }
        if (this.animalEnergy() == o.animalEnergy()) {
            return this.animal().id - o.animal().id;
        }

        return this.animalEnergy() - o.animalEnergy();
    }
}
