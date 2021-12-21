package DarwinGame.gui;

import DarwinGame.Statistics.SimpleStatisticsHandler;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class StatisticsBox extends VBox implements IStatisticsObserver {
    private final GridPane simpleStatisticsGrid = new GridPane();
    private final SimpleStatisticsHandler simpleStatisticsHandler;
    XYChart.Series<Number, Number> noOfAliveAnimalsSeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> grassTuftsSeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> averageEnergySeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> averageLifespanSeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> averageChildrenCountSeries = new XYChart.Series<>();

    private final Button saveToFileButton = new Button();

    public StatisticsBox(SimpleStatisticsHandler simpleStatisticsHandler, Stage parentStage) {
        super();
        this.simpleStatisticsHandler = simpleStatisticsHandler;

        saveToFileButton.setText("save statistics to file");
        saveToFileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();

            //Set extension filter for text files
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);

            //Show save file dialog
            File file = fileChooser.showSaveDialog(parentStage);

            if (file != null) {
                if (!file.getName().endsWith(".csv")) {
                    file = new File(file.getAbsolutePath() + ".csv");
                }
                saveStatisticsToFile(file);
            }
        });

        GridPane charts = new GridPane();
        ScrollPane chartsContainer = new ScrollPane();
        chartsContainer.setContent(charts);
        this.getChildren().addAll(saveToFileButton, simpleStatisticsGrid, chartsContainer);

        var aliveAnimalsLineChart = createLineChart("Alive animals", "Number of animals alive", noOfAliveAnimalsSeries);
        var grassTuftsLineChart = createLineChart("Grass tufts", "Number of grass tufts on the map", grassTuftsSeries);
        var averageEnergyLineChart = createLineChart("Average energy", "Average energy level of currently alive animals", averageEnergySeries);
        var averageLifespanLineChart = createLineChart("Average lifespan", "Average lifespan of dead animals in days", averageLifespanSeries);
        var averageChildrenCountChart = createLineChart("Average children count", "Average number of children of alive animal", averageChildrenCountSeries);

        charts.add(aliveAnimalsLineChart, 1, 1);
        charts.add(grassTuftsLineChart, 2, 1);
        charts.add(averageEnergyLineChart, 1, 2);
        charts.add(averageLifespanLineChart, 2, 2);
        charts.add(averageChildrenCountChart, 1, 3);

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
        this.simpleStatisticsGrid.addRow(6, new Label("Average number of children"),
                new Label(String.format(Locale.ENGLISH, "%.2f", simpleStatisticsHandler.getAverageNoOfChildren())));
        var dominantGenotype = simpleStatisticsHandler.getDominantGenotype();
        dominantGenotype.ifPresent(genotype -> this.simpleStatisticsGrid.addRow(7, new Label("Dominant of genotypes"),
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
        if (averageChildrenCountSeries.getData().size() > StatisticsDisplayConfig.CHART_WINDOW_SIZE) {
            averageChildrenCountSeries.getData().remove(0);
        }
        this.averageChildrenCountSeries.getData().add(new XYChart.Data<>(currentDayNumber,
                simpleStatisticsHandler.getAverageNoOfChildren()));
    }


    @Override
    public void refreshStatistic() {
        Platform.runLater(this::renderStatistics);
    }


    private void saveStatisticsToFile(File file) {
        System.out.println(file.getAbsolutePath());
        try {
            FileWriter out = new FileWriter(file);
//            var builder = CSVFormat.DEFAULT.builder().setHeader("Day number", "Number of alive animals", "Number of grass tufts", "Average energy of alive animals", "Average lifespan of dead animals", "Average number of children of alive animals");
            var builder = CSVFormat.EXCEL.builder().setHeader("Day number", "Number of alive animals", "Number of grass tufts", "Average energy of alive animals", "Average lifespan of dead animals", "Average number of children of alive animals");
            CSVPrinter printer = new CSVPrinter(out, builder.build());
//            CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT);
            for (int i = 0; i < this.simpleStatisticsHandler.getCurrentDayNumber(); i++) {
                printer.printRecord(
                        i,
                        simpleStatisticsHandler.getAliveAnimalsHistory().get(i),
                        simpleStatisticsHandler.getGrassTuftsHistory().get(i),
                        simpleStatisticsHandler.getAverageEnergyHistory().get(i),
                        simpleStatisticsHandler.getAverageLifeSpanHistory().get(i),
                        simpleStatisticsHandler.getAverageNoOfChildrenHistory().get(i)
                );
            }

            printer.printRecord(
                    "Average",
                    simpleStatisticsHandler.getAliveAnimalsHistory().stream().mapToDouble(a -> a).average().orElse(0),
                    simpleStatisticsHandler.getGrassTuftsHistory().stream().mapToDouble(a -> a).average().orElse(0),
                    simpleStatisticsHandler.getAverageEnergyHistory().stream().mapToDouble(a -> a).average().orElse(0),
                    simpleStatisticsHandler.getAverageLifeSpanHistory().stream().mapToDouble(a -> a).average().orElse(0),
                    simpleStatisticsHandler.getAverageNoOfChildrenHistory().stream().mapToDouble(a -> a).average().orElse(0)
            );

            printer.flush();
            printer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
