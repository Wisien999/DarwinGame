package DarwinGame.gui;

import DarwinGame.Statistics.SimpleStatisticsHandler;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Pair;
import org.checkerframework.framework.qual.IgnoreInWholeProgramInference;

import java.util.Locale;

public class StatisticsBox extends VBox implements IStatisticsObserver {
    private final GridPane simpleStatisticsGrid = new GridPane();
    private final SimpleStatisticsHandler simpleStatisticsHandler;
    XYChart.Series<Number, Number> noOfAliveAnimalsSeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> grassTuftsSeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> averageEnergySeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> averageLifespanSeries = new XYChart.Series<>();

    public StatisticsBox(SimpleStatisticsHandler simpleStatisticsHandler) {
        super();
        this.simpleStatisticsHandler = simpleStatisticsHandler;

//        HBox chartsLayer
        GridPane charts = new GridPane();
        this.getChildren().addAll(simpleStatisticsGrid, charts);


        var aliveAnimalsLineChart = createLineChart("Alive animals", "Number of animals alive", noOfAliveAnimalsSeries);
        var grassTuftsLineChart = createLineChart("Grass tufts", "Number of grass tufts on the map", grassTuftsSeries);
        var averageEnergyLineChart = createLineChart("Average energy", "Average energy level of currently alive animals", averageEnergySeries);
        var averageLifespanLineChart = createLineChart("Average lifespan", "Average lifespan of dead animals in days", averageLifespanSeries);

        charts.add(aliveAnimalsLineChart, 1, 1);
        charts.add(grassTuftsLineChart, 2, 1);
        charts.add(averageEnergyLineChart, 1, 2);
        charts.add(averageLifespanLineChart, 2, 2);

    }

    private XYChart<Number, Number> createLineChart(String title, String yTitle, XYChart.Series<Number, Number> dataSeries) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);
        xAxis.setLabel("Simulation day");
        yAxis.setLabel(yTitle);
        //creating the chart
        final LineChart<Number,Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setAnimated(true);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);

        chart.getData().add(dataSeries);

        return chart;
    }


    private void renderStatistics() {
        simpleStatisticsGrid.getChildren().clear();
        this.simpleStatisticsGrid.addRow(1, new Label("Number of animals alive"),
                new Label(Integer.toString(simpleStatisticsHandler.getNoOfAliveAnimals())));
        this.simpleStatisticsGrid.addRow(2, new Label("Number of dead animals"),
                new Label(Integer.toString(simpleStatisticsHandler.getNoOfDeadAnimals())));
        this.simpleStatisticsGrid.addRow(3, new Label("Number of grass tufts"),
                new Label(Integer.toString(simpleStatisticsHandler.getNoOfGrassTufts())));
        this.simpleStatisticsGrid.addRow(4, new Label("Average energy of alive animals"),
                new Label(String.format(Locale.ENGLISH, "%.2f", simpleStatisticsHandler.getAverageEnergy())));
        this.simpleStatisticsGrid.addRow(5, new Label("Average life span of dead animals"),
                new Label(String.format(Locale.ENGLISH, "%.2f", simpleStatisticsHandler.getAverageLifeSpan())));
        var dominantGenotype = simpleStatisticsHandler.getDominantGenotype();
        dominantGenotype.ifPresent(genotype -> this.simpleStatisticsGrid.addRow(6, new Label("Dominant of genotypes"),
                new Label(genotype.toString())));

        int currentDayNumber = simpleStatisticsHandler.getCurrentDayNumber();

        if (noOfAliveAnimalsSeries.getData().size() > StatisticsDisplayConfig.CHART_WINDOW_SIZE) {
            noOfAliveAnimalsSeries.getData().remove(0);
        }
        this.noOfAliveAnimalsSeries.getData().add(new XYChart.Data<>(currentDayNumber,
                simpleStatisticsHandler.getNoOfAliveAnimals()));
        if (grassTuftsSeries.getData().size() > StatisticsDisplayConfig.CHART_WINDOW_SIZE) {
            grassTuftsSeries.getData().remove(0);
        }
        this.grassTuftsSeries.getData().add(new XYChart.Data<>(currentDayNumber,
                simpleStatisticsHandler.getNoOfGrassTufts()));
        if (averageEnergySeries.getData().size() > StatisticsDisplayConfig.CHART_WINDOW_SIZE) {
            averageEnergySeries.getData().remove(0);
        }
        this.averageEnergySeries.getData().add(new XYChart.Data<>(currentDayNumber,
                simpleStatisticsHandler.getAverageEnergy()));
        if (averageLifespanSeries.getData().size() > StatisticsDisplayConfig.CHART_WINDOW_SIZE) {
            averageLifespanSeries.getData().remove(0);
        }
        this.averageLifespanSeries.getData().add(new XYChart.Data<>(currentDayNumber,
                simpleStatisticsHandler.getAverageLifeSpan()));
    }

    @Override
    public void refreshStatistic() {
        Platform.runLater(this::renderStatistics);
    }
}
