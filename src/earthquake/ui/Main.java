package earthquake.ui;

import earthquake.controllers.ApplicationController;
import earthquake.controllers.ShakingBuildingController;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
    	final double sceneWidth = 1400;
        final double sceneHeight = 700;

        Pair<Pane, Pane> animated = generateAnimatedPane();

	    Button playButton = new Button("Запуск");
	    Button pauseButton = new Button("Пауза");
	    Button stopButton = new Button("Стоп");
	    /*
	    Button normalSpeedButton = new Button("Скорость 1x");
	    Button tenthSpeedButton = new Button("Скорость 0.1x");
	    Button halfSpeedButton = new Button("Скорость 0.5x");
	    Button doubleSpeedButton = new Button("Скорость 2x");
	    Button tenSpeedButton = new Button("Скорость 10x");
	    */
	    Button openFileButton = new Button("Загрузить данные");

	    HBox hbox = new HBox();
	    hbox.setSpacing(3);

	    hbox.setBackground(new Background(new BackgroundFill(Color.FIREBRICK, null, null)));

	    hbox.getChildren().addAll(playButton, pauseButton, stopButton);
	    //hbox.getChildren().addAll(tenthSpeedButton, halfSpeedButton, normalSpeedButton, doubleSpeedButton, tenSpeedButton);
	    hbox.getChildren().addAll(openFileButton);
	    animated.getKey().setBackground(new Background(new BackgroundFill(Color.PINK, null, null)));

	    BorderPane borderPaneLayout = new BorderPane();
	    borderPaneLayout.setCenter(animated.getKey());
	    borderPaneLayout.setBottom(hbox);
	    borderPaneLayout.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, null, null)));

	    //Creating a Scene by passing the group object, height and width
        Scene scene = new Scene(borderPaneLayout, sceneWidth, sceneHeight);

        //setting color to the scene
        scene.setFill(Color.TURQUOISE);

        //Setting the title to Stage.
        primaryStage.setTitle("Землятресения");

        //Adding the scene to Stage
        primaryStage.setScene(scene);

        //Displaying the contents of the stage
        primaryStage.show();

	    ShakingBuildingController shakingController = new ShakingBuildingController(animated.getValue());

	    applicationController = new ApplicationController(shakingController);
	    playButton.setOnAction(event -> applicationController.startShaking());
	    pauseButton.setOnAction(event -> applicationController.pauseShaking());
	    stopButton.setOnAction(event -> applicationController.stopShaking());

	    /*
	    tenthSpeedButton.setOnAction(event -> applicationController.shakingSpeed(0.1));
	    halfSpeedButton.setOnAction(event -> applicationController.shakingSpeed(0.5));
	    normalSpeedButton.setOnAction(event -> applicationController.shakingSpeed(1));
	    doubleSpeedButton.setOnAction(event -> applicationController.shakingSpeed(2));
	    tenSpeedButton.setOnAction(event -> applicationController.shakingSpeed(10));
	    */

	    openFileButton.setOnAction(event -> {
		    FileChooser fileChooser = new FileChooser();
		    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Файлы CSV", "*.csv"));
		    fileChooser.setTitle("Открыть файл");

		    File dataFile = fileChooser.showOpenDialog(primaryStage);
		    applicationController.openDataFile(dataFile);
	    });
    }

    static Pair<Pane, Rectangle> generateBox() {
        final double w = 300;
        final double h = 500;
        Rectangle rect = new Rectangle(-w/2, -h, w, h);
	    rect.setFill(Color.LIGHTGRAY);
	    rect.setStroke(Color.DARKGRAY);
	    rect.setStrokeWidth(2);

	    Polygon centerMarkk = new Polygon();
	    centerMarkk.getPoints().addAll(new Double[]{
			    0.0, 0.0,
			    5.0, -20.0,
			    -5.0, -20.0 });

	    Pane result = new Pane();
        result.getChildren().addAll(rect, centerMarkk);
        return new Pair<>(result, rect);
    }

    static Group generateFloor() {
        final double w = 700;
        final double h = 5;
        Rectangle rect = new Rectangle(-w/2, 0, w, h);
	    rect.setFill(Color.LIGHTGRAY);
	    rect.setStroke(Color.DARKGRAY);
	    rect.setStrokeWidth(2);

	    return new Group(rect);
    }

    static Pair<Pane, Pane> generateAnimatedPane() {
        Pane centeredPane = new Pane();
	    centeredPane.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
	    Group g = new Group();
	    Pair<Pane, Rectangle> z = generateBox();
	    g.getChildren().addAll(z.getKey(), generateFloor());
	    centeredPane.getChildren().add(g);
	    g.setAutoSizeChildren(false);

        Polygon centerMarkk = new Polygon();
	    centerMarkk.getPoints().addAll(new Double[]{
			    0.0, 0.0,
			    5.0, 20.0,
			    -5.0, 20.0 });
	    g.getChildren().add(centerMarkk);


	    centeredPane.widthProperty().addListener((__, oldWidth, newWidth) -> {
		    g.setTranslateX(newWidth.doubleValue() / 2);
	    });

	    centeredPane.heightProperty().addListener((__, oldHeight, newHeight) -> {
		    g.setTranslateY(newHeight.doubleValue() / 8 * 7);
	    });

	    VBox vbox = new VBox();
	    vbox.getChildren().add(centeredPane);
	    vbox.setAlignment(Pos.CENTER);

	    HBox hbox = new HBox();
	    hbox.getChildren().add(centeredPane);
	    hbox.setAlignment(Pos.CENTER);
	    return new Pair<>(centeredPane, z.getKey());
    }

    public static void main(String[] args) {
        launch(args);
    }

    static ApplicationController applicationController;
}
