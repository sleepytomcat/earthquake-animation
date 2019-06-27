package earthquake.controllers;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApplicationController {
	public ApplicationController(ShakingBuildingController shaking,
	                             Button pauseResumeAnimationButton,
	                             Pane animated,
	                             StringProperty dataFileName) {
		this.shaking = shaking;
		//this.startAnimationButton = startAnimationButton;
		this.pauseResumeAnimationButton = pauseResumeAnimationButton;
		this.animatedPane = animated;
		this.dataFileName = dataFileName;
		onNoData();
	}

	public void startShaking() {
		shaking.startShaking();
	}

	public void pauseShaking() {
		shaking.pauseShaking();
	}

	public void stopShaking() {
		shaking.stopShaking();
	}

	public void shakingSpeed(double factor) {
		shaking.shakingSpeed(factor);
	}
	public void pauseResume() {
		shaking.pauseResume();
	}

	public void openDataFile(File dataFile) {
		if (dataFile != null) {
			try {
				System.out.println("Starting to load " + dataFile.toString());
				shaking.loadEarthquakeData(readCsvDataFile(dataFile));
				System.out.println("File loaded " + dataFile.toString());
				onDataLoaded(dataFile);
			}
			catch (IOException ex) {
				System.out.println(ex);
			}
		}
		else {
			System.out.println("File was null");
		}
	}

	private static Optional<Double[]> extractValues(String text) {
		System.out.println("extractValues " + text);
		String[] tokens;
		tokens = text.split(";");
		System.out.println("tokens " + tokens);

		if (tokens.length != 3) {
			throw new IllegalArgumentException(tokens.toString());
			//return Optional.empty();
		}
		else {
			Double[] values = new Double[3];

			System.out.println("converting " + tokens[0]);
			values[0] = Double.valueOf(tokens[0].replaceAll(",", "."));

			System.out.println("converting " + tokens[1]);
			values[1] = Double.valueOf(tokens[1].replaceAll(",", "."));

			System.out.println("converting " + tokens[2]);
			values[2] = Double.valueOf(tokens[2].replaceAll(",", "."));

			return Optional.of(values);
		}
	}

	private static List<Double[]> readCsvDataFile(File dataFile) throws IOException {
		List<Double[]> result = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(dataFile));
		String currentLine;
		do {
			currentLine = reader.readLine();
			if (currentLine != null) {
				String msg = currentLine;
				Double[] values =
						extractValues(currentLine)
						.orElseThrow(() -> new IllegalArgumentException(msg));
				Double time = values[0];
				Double groundOffset = values[1];
				Double buildingOffset = values[2];
				System.out.println("[" + time.toString() + "s] -> (" + groundOffset.toString() + "m;" + buildingOffset.toString() + "m");

				result.add(values);
			}
		} while (currentLine != null);

		return result;
	}

	private void onNoData() {
		//startAnimationButton.setDisable(true);
		pauseResumeAnimationButton.setDisable(true);
		animatedPane.opacityProperty().setValue(0.2);
	}

	private void onDataLoaded(File dataFile) {
		//startAnimationButton.setDisable(false);
		pauseResumeAnimationButton.setDisable(false);
		animatedPane.opacityProperty().setValue(1);
		dataFileName.setValue("Загружены данные: " + dataFile.getName());
	}

	private ShakingBuildingController shaking;
	//private Button startAnimationButton;
	private Button pauseResumeAnimationButton;
	private Pane animatedPane;
	private StringProperty dataFileName;
}
