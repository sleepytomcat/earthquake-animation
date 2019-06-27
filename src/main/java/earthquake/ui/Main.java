package earthquake.ui;

import earthquake.controllers.ApplicationController;
import earthquake.controllers.ShakingBuildingController;
import javafx.application.Application;
import javafx.beans.property.StringProperty;
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
import io.vavr.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
	    Locale.setDefault(new Locale("ru", "RU"));

	    final double sceneWidth = 1400;
        final double sceneHeight = 700;

        Tuple5<Pane, Pane, Pane, StringProperty, StringProperty> animated = generateAnimatedPane();
        Pane animationPane = animated._1;
        Pane groundPane = animated._2;
        Pane buildingPane = animated._3;
	    StringProperty groundOffsetString = animated._4;
	    StringProperty buildingOffsetString = animated._5;

	    //Button playButton = new Button("Запуск анимации");
	    Button playPauseResume = new Button("Запуск / Пауза");
	    Button openFileButton = new Button("Загрузить данные");

	    HBox buttons = new HBox();
	    buttons.getChildren().addAll(playPauseResume, openFileButton);

	    HBox animationTime = new HBox();
	    animationTime.setAlignment(Pos.TOP_CENTER);
	    Text time = new Text();
	    time.setStyle("-fx-font-size: 30");
	    animationTime.getChildren().addAll(time);

	    ProgressBar animationProgress = new ProgressBar(0);
		animationProgress.setMaxWidth(Double.MAX_VALUE);
	    VBox vbox = new VBox(animationTime, animationProgress, buttons);

	    BorderPane borderPaneLayout = new BorderPane();
	    borderPaneLayout.setCenter(animationPane);
	    borderPaneLayout.setBottom(vbox);

        Scene scene = new Scene(borderPaneLayout, sceneWidth, sceneHeight);
        primaryStage.setTitle("Землятресения, версия 1.4");
        primaryStage.setScene(scene);
        primaryStage.show();

	    ShakingBuildingController shakingController = new ShakingBuildingController(
	    		groundPane,
			    buildingPane,
			    animationProgress,
			    groundOffsetString,
			    buildingOffsetString,
			    time.textProperty());

	    applicationController = new ApplicationController(shakingController, playPauseResume, animationPane, primaryStage.titleProperty());
	    //playButton.setOnAction(event -> applicationController.startShaking());

	    openFileButton.setOnAction(event -> {
		    FileChooser fileChooser = new FileChooser();
		    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Файлы CSV", "*.csv"));
		    fileChooser.setTitle("Открыть файл");

		    File dataFile = fileChooser.showOpenDialog(primaryStage);
		    applicationController.openDataFile(dataFile);
	    });

	    playPauseResume.setOnAction(event -> {
		    applicationController.pauseResume();
	    });
    }

    static Tuple2<Pane, StringProperty> generateBox() {
        final double w = 350;
        final double h = 300;
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
	    offsetText.setStyle("-fx-font-size: 30");
	    offsetText.setY(-55);
	    Pane buildingPane = new Pane();
        buildingPane.getChildren().addAll(rect, centerMarkk, offsetText);
        return Tuple.of(buildingPane, offsetText.textProperty());
    }

    static Tuple2<Pane, StringProperty> generateGround() {
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

	    Text offsetText = new Text();
	    offsetText.setStyle("-fx-font-size: 30");
	    offsetText.setY(+120);

	    Pane groundPane = new Pane();
	    groundPane.getChildren().addAll(rect, centerMarkk, offsetText);
	    return Tuple.of(groundPane, offsetText.textProperty());
    }

    static Tuple5<Pane, Pane, Pane, StringProperty, StringProperty> generateAnimatedPane() {
        Pane centeredPane = new Pane();
	    Group animatedItemsGroup = new Group();
	    Tuple2<Pane, StringProperty> buildingPane = generateBox();
	    Tuple2<Pane, StringProperty> groundPane = generateGround();
	    animatedItemsGroup.getChildren().addAll(buildingPane._1, groundPane._1);
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
		    animatedItemsGroup.setTranslateY(newHeight.doubleValue() * 0.70);
	    });

	    VBox vbox = new VBox();
	    vbox.getChildren().add(centeredPane);
	    vbox.setAlignment(Pos.CENTER);

	    HBox hbox = new HBox();
	    hbox.getChildren().add(centeredPane);
	    hbox.setAlignment(Pos.CENTER);

	    return Tuple.of(centeredPane, groundPane._1, buildingPane._1, groundPane._2, buildingPane._2);
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
