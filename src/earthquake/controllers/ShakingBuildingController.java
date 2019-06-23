package earthquake.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.List;

public class ShakingBuildingController {
	public ShakingBuildingController(Pane moving) {
		this.timeline = new Timeline();
		this.moving = moving;
	}

	public void startShaking() {
		timeline.play();
	}

	public void pauseShaking() {
		timeline.pause();
	}

	public void stopShaking() {
		timeline.stop();
	}

	public void shakingSpeed(double factor) {
		timeline.setRate(factor);
	}

	public void loadEarthquakeData(List<Pair<Double, Double>> earthquakePairs) {
		timeline.getKeyFrames().removeAll();
		earthquakePairs
				.stream()
				.map(pair -> new Pair<>(new KeyValue(moving.translateXProperty(), pair.getValue()), pair.getKey()))
				.map(pair -> new KeyFrame(Duration.seconds(pair.getValue()), pair.getKey()))
				.forEach(keyframe -> timeline.getKeyFrames().add(keyframe));
		System.out.println("Frames loaded: " + timeline.getKeyFrames().size());
	}

	private Timeline timeline;
	private Pane moving;
}
