package earthquake.ui;

import earthquake.controllers.ApplicationController;
import earthquake.controllers.ShakingBuildingController;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
	    Locale.setDefault(new Locale("ru", "RU"));

	    final double sceneWidth = 1400;
        final double sceneHeight = 700;

        Pane[] animated = generateAnimatedPane();
        Pane animationPane = animated[0];
        Pane groundPane = animated[1];
        Pane buildingPane = animated[2];

	    Button playButton = new Button("Запуск анимации");
	    Button openFileButton = new Button("Загрузить данные");


	    HBox buttons = new HBox();
	    buttons.getChildren().addAll(playButton);
	    buttons.getChildren().addAll(openFileButton);

	    ProgressBar animationProgress = new ProgressBar(0);
		animationProgress.setMaxWidth(Double.MAX_VALUE);
	    VBox vbox = new VBox(animationProgress, buttons);

	    BorderPane borderPaneLayout = new BorderPane();
	    borderPaneLayout.setCenter(animationPane);
	    borderPaneLayout.setBottom(vbox);

        Scene scene = new Scene(borderPaneLayout, sceneWidth, sceneHeight);
        primaryStage.setTitle("Землятресения, версия 1.2");
        primaryStage.setScene(scene);
        primaryStage.show();

	    ShakingBuildingController shakingController = new ShakingBuildingController(
	    		groundPane,
			    buildingPane,
			    animationProgress);

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

    static Pair<Pane, Text> generateBox() {
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

	    Text offsetText = new Text();
	    //offsetText.setText("hello");
	    offsetText.setY(-55);
	    Pane buildingPane = new Pane();
        buildingPane.getChildren().addAll(rect, centerMarkk, offsetText);
        return new Pair<>(buildingPane, offsetText);
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
	    Pair<Pane , Text> buildingPane = generateBox();
	    Pane groundPane = generateGround();
	    animatedItemsGroup.getChildren().addAll(buildingPane.getKey(), groundPane);
	    centeredPane.getChildren().add(animatedItemsGroup);
	    animatedItemsGroup.setAutoSizeChildren(false);

	    List<Group> majorTicks = generateTicks(-5, 6, 5, 7, 20, 100);
	    List<Group> minorTicks = generateTicks(-50, 51,10, 2, 10, 10);

	    Text minus500mm = new Text("-500 mm");
	    minus500mm.setX(-65);
	    minus500mm.setY(+14);
	    majorTicks.get(0).getChildren().add(minus500mm);

	    Text plus500mm = new Text("+500 mm");
	    plus500mm.setX(+10);
	    plus500mm.setY(+14);
	    majorTicks.get(majorTicks.size() - 1).getChildren().add(plus500mm);

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
	    panes[2] = buildingPane.getKey();
	    return panes;
    }

	private static List<Group> generateTicks(int from, int to, double tickDepth, double tickWidth, double tickHeight, double tickStep) {
		return IntStream.range(from, to)
				.mapToObj(offset -> {
					Group tickGroup = new Group();
					tickGroup.setTranslateX(offset * tickStep);
					tickGroup.setTranslateY(tickDepth);
					return tickGroup;
				})
				.map(group -> {
					double[] points = {
							0, 0,
							tickWidth / 2, tickHeight / 2,
							0, tickHeight,
							- tickWidth / 2, tickHeight / 2
					};
					Polygon polygon = new Polygon(points);
					polygon.setFill(Color.LIGHTGRAY);
					polygon.setStroke(Color.GRAY);
					group.getChildren().add(polygon);
					return group;
				})
				.collect(Collectors.toList());
	}

	public static void main(String[] args) {
        launch(args);
    }

    static ApplicationController applicationController;
}