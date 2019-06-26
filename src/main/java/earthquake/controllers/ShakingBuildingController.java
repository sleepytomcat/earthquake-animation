package earthquake.controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.List;

public class ShakingBuildingController {
	public ShakingBuildingController(Pane groundPane, Pane buildingPane, ProgressIndicator animationProgress, StringProperty groundOffsetText, StringProperty buildingOffsetText) {
		this.buildingPane = buildingPane;
		this.groundPane = groundPane;
		this.animationProgress = animationProgress;
		this.timelineBuilding = new Timeline();
		this.timelineGround = new Timeline();
		this.animationTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				double progress = timelineBuilding.getCurrentTime().toMillis() / timelineBuilding.getTotalDuration().toMillis();
				animationProgress.setProgress(progress);

				String buildingOffsetString = String.format("%,.2f", buildingPane.getTranslateX());
				buildingOffsetText.setValue(buildingOffsetString);

				String groundOffsetString = String.format("%,.2f", groundPane.getTranslateX());
				groundOffsetText.setValue(groundOffsetString);
			}
		};
		this.buildingOffsetText = buildingOffsetText;
		this.groundOffsetText = groundOffsetText;
	}

	public void startShaking() {
		timelineBuilding.stop();
		timelineGround.stop();

		animationTimer.stop();
		animationTimer.start();

		timelineBuilding.play();
		timelineGround.play();
	}

	public void pauseShaking() {
		timelineBuilding.pause();
		timelineGround.pause();
	}

	public void stopShaking() {
		timelineBuilding.stop();
		timelineGround.stop();
		animationTimer.stop();
	}

	public void shakingSpeed(double factor) {
		timelineBuilding.setRate(factor);
		timelineGround.setRate(factor);
	}

	public void loadEarthquakeData(List<Double[]> earthquakeData) {
		final double SCALE = 1000;
		timelineBuilding.stop();
		timelineGround.stop();
		timelineBuilding.getKeyFrames().removeAll();
		timelineGround.getKeyFrames().removeAll();

		earthquakeData
				.stream()
				.map(data -> new Pair<>(new KeyValue(buildingPane.translateXProperty(), data[2] * SCALE), data[0]))
				.map(pair -> new KeyFrame(Duration.seconds(pair.getValue()), pair.getKey()))
				.forEach(keyframe -> timelineBuilding.getKeyFrames().add(keyframe));
		System.out.println("Frames loaded: " + timelineBuilding.getKeyFrames().size());

		earthquakeData
				.stream()
				.map(data -> new Pair<>(new KeyValue(groundPane.translateXProperty(), data[1] * SCALE), data[0]))
				.map(pair -> new KeyFrame(Duration.seconds(pair.getValue()), pair.getKey()))
				.forEach(keyframe -> timelineGround.getKeyFrames().add(keyframe));
		System.out.println("Frames loaded: " + timelineGround.getKeyFrames().size());
	}

	private Timeline timelineBuilding;
	private Pane buildingPane;

	private Timeline timelineGround;
	private Pane groundPane;
	private ProgressIndicator animationProgress;
	private AnimationTimer animationTimer;
	private StringProperty buildingOffsetText;
	private StringProperty groundOffsetText;
}
