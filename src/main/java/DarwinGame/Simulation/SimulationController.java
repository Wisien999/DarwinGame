package DarwinGame.Simulation;

import DarwinGame.WorldMap.AbstractWorldMap;

public class SimulationController {
    private AbstractWorldMap map;
    private SimulationEngine engine;
    private Thread engineThread;

    public SimulationController(AbstractWorldMap map) {
        this.map = map;
        this.engine = new SimulationEngine(map);
    }

    public void startSimulation() {
        this.engineThread = new Thread(this.engine);
        this.engineThread.start();
    }

    public void stopSimulation() {
        this.engineThread.interrupt();
    }

    public SimulationEngine getEngine() {
        return engine;
    }
}
