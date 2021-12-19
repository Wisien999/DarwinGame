package DarwinGame.Simulation;

import DarwinGame.Statistics.SimpleStatisticsHandler;
import DarwinGame.WorldMap.AbstractWorldMap;
import DarwinGame.gui.GuiWorldMap;

public class SimulationController {
    private final SimpleStatisticsHandler simpleStatisticsHandler = new SimpleStatisticsHandler();
    private final SimulationEngine engine;


    private Thread engineThread;

    public SimulationController(AbstractWorldMap map) {
        this.engine = new SimulationEngine(map, simpleStatisticsHandler);
    }

    public void startSimulation() {
        this.engineThread = new Thread(this.engine);
        this.engineThread.start();
    }

    public void stopSimulation() {
        this.engineThread.interrupt();
    }

    public SimpleStatisticsHandler getSimpleStatisticsHandler() {
        return simpleStatisticsHandler;
    }

    public SimulationEngine getEngine() {
        return engine;
    }
}
