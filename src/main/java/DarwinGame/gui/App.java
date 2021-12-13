package DarwinGame.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });


        GridPane layout = new GridPane();
        layout.setHgap(5);
        layout.setVgap(5);
        layout.setGridLinesVisible(true);
        Scene configScene = new Scene(layout, 500, 500);

        TextField mapWidthField = new TextField();
        layout.addRow(0, new Label("Map width"), mapWidthField);


        primaryStage.setScene(configScene);
        primaryStage.show();
    }
}
