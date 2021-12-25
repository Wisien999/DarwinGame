package DarwinGame.Simulation;

import DarwinGame.Statistics.SimpleStatisticsHandler;
import DarwinGame.WorldMap.AbstractWorldMap;
import DarwinGame.gui.GuiWorldMap;

import java.util.ArrayList;
import java.util.List;

public class SimulationController {
    private final SimpleStatisticsHandler simpleStatisticsHandler = new SimpleStatisticsHandler();
    private final SimulationEngine engine;
    private final List<ISimulationObserver> simulationObservers = new ArrayList<>();

    private Thread engineThread;


    public SimulationController(AbstractWorldMap map) {
        this.engine = new SimulationEngine(map, simpleStatisticsHandler);
        this.engine.addNextDayObserver(simpleStatisticsHandler);
    }

    public void startSimulation() {
        this.engineThread = new Thread(this.engine);
        this.engineThread.start();

        for (var observer : simulationObservers) {
            observer.simulationStarted();
        }
    }

    public void stopSimulation() {
        if (!this.isRunning()) {
            return;
        }
        this.engineThread.interrupt();

        for (var observer : simulationObservers) {
            observer.simulationStopped();
        }
    }

    public SimpleStatisticsHandler getSimpleStatisticsHandler() {
        return simpleStatisticsHandler;
    }

    public SimulationEngine getEngine() {
        return engine;
    }
    public boolean isRunning() {
        if (this.engineThread == null) {
            return false;
        }
        return this.engineThread.isAlive();
    }

    public void addSimulationObserver(ISimulationObserver observer) {
        this.simulationObservers.add(observer);
    }
    public void removeSimulationObserver(ISimulationObserver observer) {
        this.simulationObservers.remove(observer);
    }
}
