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

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
    	System.out.println("ver 2");
	    Locale.setDefault(new Locale("ru", "RU"));
	    System.out.println(Locale.getDefault());

	    final double sceneWidth = 1400;
        final double sceneHeight = 700;

        Pane[] animated = generateAnimatedPane();
        Pane animationPane = animated[0];
        Pane groundPane = animated[1];
        Pane buildingPane = animated[2];

	    Button playButton = new Button("Запуск анимации");
	    Button openFileButton = new Button("Загрузить данные");

	    HBox hbox = new HBox();
	    hbox.setSpacing(3);

	    hbox.getChildren().addAll(playButton);
	    hbox.getChildren().addAll(openFileButton);

	    BorderPane borderPaneLayout = new BorderPane();
	    borderPaneLayout.setCenter(animationPane);
	    borderPaneLayout.setBottom(hbox);

        Scene scene = new Scene(borderPaneLayout, sceneWidth, sceneHeight);
        primaryStage.setTitle("Землятресения");
        primaryStage.setScene(scene);
        primaryStage.show();

	    ShakingBuildingController shakingController = new ShakingBuildingController(groundPane, buildingPane);

	    applicationController = new ApplicationController(shakingController, playButton, animationPane);
	    playButton.setOnAction(event -> applicationController.startShaking());

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
			    5.0, -40.0,
			    -5.0, -40.0 });

	    Pane buildingPane = new Pane();
        buildingPane.getChildren().addAll(rect, centerMarkk);
        return buildingPane;
    }

    static Pane generateGround() {
        final double w = 450;
        final double h = 50;
        Rectangle rect = new Rectangle(-w/2, 30, w, h);
	    rect.setFill(Color.LIGHTSLATEGREY);
	    rect.setStroke(Color.DARKGRAY);
	    rect.setStrokeWidth(2);

	    Polygon centerMarkk = new Polygon();
	    centerMarkk.getPoints().addAll(new Double[]{
			    0.0, 30.0,
			    5.0, 70.0,
			    -5.0, 70.0 });

	    Pane groundPane = new Pane();
	    groundPane.getChildren().addAll(rect, centerMarkk);
	    return groundPane;
    }

    static Pane[] generateAnimatedPane() {
        Pane centeredPane = new Pane();
	    Group animatedItemsGroup = new Group();
	    Pane buildingPane = generateBox();
	    Pane groundPane = generateGround();
	    animatedItemsGroup.getChildren().addAll(buildingPane, groundPane);
	    centeredPane.getChildren().add(animatedItemsGroup);
	    animatedItemsGroup.setAutoSizeChildren(false);

	    List<Polygon> majorTicks = generateTicks(-5, 6, 5, 7, 20, 100);
	    List<Polygon> minorTicks = generateTicks(-50, 51,10, 2, 10, 10);

	    double tickDepth = 5;
	    double tickWidth = 7;
	    double tickHeight = 20;

	    animatedItemsGroup.getChildren().addAll(minorTicks);
	    animatedItemsGroup.getChildren().addAll(majorTicks);

	    Polygon centerMark = new Polygon(
	    		0, tickDepth,
			    tickWidth / 2, tickHeight / 2 + tickDepth,
				0,  tickHeight + tickDepth,
			    -tickWidth / 2, tickHeight / 2 + tickDepth
	    );

	    centerMark.setFill(Color.BLACK);
	    centerMark.setStroke(Color.GRAY);
	    animatedItemsGroup.getChildren().addAll(centerMark);

	    centeredPane.widthProperty().addListener((__, oldWidth, newWidth) -> {
		    animatedItemsGroup.setTranslateX(newWidth.doubleValue() / 2);
	    });

	    centeredPane.heightProperty().addListener((__, oldHeight, newHeight) -> {
		    animatedItemsGroup.setTranslateY(newHeight.doubleValue() * 0.80);
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

	private static List<Polygon> generateTicks(int from, int to, double tickDepth, double tickWidth, double tickHeight, double tickStep) {
		return IntStream.range(from, to)
				.mapToObj(offset -> {
					double[] points = {
							offset * tickStep, tickDepth,
							offset * tickStep + tickWidth / 2, tickHeight / 2 + tickDepth,
							offset * tickStep, tickHeight + tickDepth,
							offset * tickStep - tickWidth / 2, tickHeight / 2 + tickDepth
					};
					return new Polygon(points);
				})
				.map(tick -> {
					tick.setFill(Color.LIGHTGRAY);
					return tick;
				})
				.map(tick -> {
					tick.setStroke(Color.GRAY);
					return tick;
				})
				.collect(Collectors.toList());
	}

	public static void main(String[] args) {
        launch(args);
    }

    static ApplicationController applicationController;
}
