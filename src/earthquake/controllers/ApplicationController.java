package earthquake.controllers;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApplicationController {
	public ApplicationController(ShakingBuildingController shaking) {
		this.shaking = shaking;
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

	public void openDataFile(File dataFile) {
		if (dataFile != null) {
			try {
				System.out.println("Starting to load " + dataFile.toString());

				readCsvDataFile(dataFile);
				shaking.loadEarthquakeData(readCsvDataFile(dataFile));
				System.out.println("File loaded " + dataFile.toString());
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
		String[] tokens;
		tokens = text.split(";");

		if (tokens.length != 3) {
			return Optional.empty();
		}
		else {
			Double[] values = new Double[3];
			values[0] = Double.valueOf(tokens[0].replaceAll(",", "."));
			values[1] = Double.valueOf(tokens[1].replaceAll(",", "."));
			values[2] = Double.valueOf(tokens[2].replaceAll(",", "."));
			return Optional.of(values);
		}
	}

	private static List<Pair<Double, Double>> readCsvDataFile(File dataFile) throws IOException {
		List<Pair<Double, Double>> result = new ArrayList<>();
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
				result.add(new Pair<>(time, buildingOffset * 1000));
			}
		} while (currentLine != null);

		return result;
	}

	private ShakingBuildingController shaking;
}
