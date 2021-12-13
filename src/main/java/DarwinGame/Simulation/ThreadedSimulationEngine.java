package DarwinGame.Simulation;

import DarwinGame.MapElements.Animal.Animal;
import DarwinGame.MoveDirection;
import DarwinGame.Vector2d;
import DarwinGame.WorldMap.IWorldMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ThreadedSimulationEngine implements IEngine, Runnable {
    private final IWorldMap map;
    private final List<Animal> animals = new ArrayList<>();

    private final int moveDelay;

    public ThreadedSimulationEngine(IWorldMap map, List<Vector2d> initialPositions, int moveDelay) {
        this.map = map;
        this.moveDelay = moveDelay;


        List<Animal> animals = initialPositions.stream()
                .map(pos -> new Animal(this.map, pos))
                .collect(Collectors.toList());


        for (Animal animal : animals) {
            this.map.place(animal);
            this.animals.add(animal);
        }
    }

    @Override
    public void run() {
        while(true) {
            makeSimulationStep();
            try {
                //noinspection BusyWait
                Thread.sleep(moveDelay);
            } catch (InterruptedException e) {
                System.err.println("Interruption while waiting for animal move!");
            }
        }
    }

    private void makeSimulationStep() {
//        MoveDirection moveDirection = this.movesIterator.next();
//        this.animals.get(i % this.animals.size()).move(moveDirection);
    }
}
