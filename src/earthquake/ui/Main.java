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
import java.util.Locale;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
    	System.out.println("ver 1");
	    Locale.setDefault(new Locale("ru", "RU"));
	    System.out.println(Locale.getDefault());
    	final double sceneWidth = 1400;
        final double sceneHeight = 700;

        Pane[] animated = generateAnimatedPane();
        Pane animationPane = animated[0];
        Pane groundPane = animated[1];
        Pane buildingPane = animated[2];

	    Button playButton = new Button("Запуск");
	    Button pauseButton = new Button("Пауза");
	    Button stopButton = new Button("Стоп");
	    Button openFileButton = new Button("Загрузить данные");

	    HBox hbox = new HBox();
	    hbox.setSpacing(3);

	    hbox.setBackground(new Background(new BackgroundFill(Color.FIREBRICK, null, null)));

	    hbox.getChildren().addAll(playButton, pauseButton, stopButton);
	    hbox.getChildren().addAll(openFileButton);
	    animated[0].setBackground(new Background(new BackgroundFill(Color.PINK, null, null)));

	    BorderPane borderPaneLayout = new BorderPane();
	    borderPaneLayout.setCenter(animationPane);
	    borderPaneLayout.setBottom(hbox);
	    borderPaneLayout.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, null, null)));

        Scene scene = new Scene(borderPaneLayout, sceneWidth, sceneHeight);
        scene.setFill(Color.TURQUOISE);
        primaryStage.setTitle("Землятресения");
        primaryStage.setScene(scene);
        primaryStage.show();

	    ShakingBuildingController shakingController = new ShakingBuildingController(groundPane, buildingPane);

	    applicationController = new ApplicationController(shakingController);
	    playButton.setOnAction(event -> applicationController.startShaking());
	    pauseButton.setOnAction(event -> applicationController.pauseShaking());
	    stopButton.setOnAction(event -> applicationController.stopShaking());

	    openFileButton.setOnAction(event -> {
		    FileChooser fileChooser = new FileChooser();
		    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Файлы CSV", "*.csv"));
		    fileChooser.setTitle("Открыть файл");

		    File dataFile = fileChooser.showOpenDialog(primaryStage);
		    applicationController.openDataFile(dataFile);
	    });
    }

    static Pane generateBox() {
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

	    Pane buildingPane = new Pane();
        buildingPane.getChildren().addAll(rect, centerMarkk);
        return buildingPane;
    }

    static Pane generateFloor() {
        final double w = 700;
        final double h = 5;
        Rectangle rect = new Rectangle(-w/2, 0, w, h);
	    rect.setFill(Color.LIGHTGRAY);
	    rect.setStroke(Color.DARKGRAY);
	    rect.setStrokeWidth(2);

	    return new Pane(rect);
    }

    static Pane[] generateAnimatedPane() {
        Pane centeredPane = new Pane();
	    centeredPane.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
	    Group g = new Group();
	    Pane buildingPane = generateBox();
	    Pane groundPane = generateFloor();
	    g.getChildren().addAll(buildingPane, groundPane);
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

	    Pane[] panes = new Pane[3];
	    panes[0] = centeredPane;
	    panes[1] = groundPane;
	    panes[2] = buildingPane;
	    return panes;
    }

    public static void main(String[] args) {
        launch(args);
    }

    static ApplicationController applicationController;
}
