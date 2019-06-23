package earthquake.controllers;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

	private static List<Pair<Double, Double>> readCsvDataFile(File dataFile) throws IOException {
		List<Pair<Double, Double>> result = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(dataFile));
		String currentLine;
		do {
			currentLine = reader.readLine();
			if (currentLine != null) {
				String[] valuesArray = currentLine.split(",");
				String[] alternativeVluesArray = currentLine.split(";"); // for compatibility with Excel CSV format
				String[] effectiveValues;

				if (valuesArray.length == 2) {
					effectiveValues = valuesArray;
				}
				else if (alternativeVluesArray.length == 2) {
					effectiveValues = alternativeVluesArray;
				}
				else {
					throw new IllegalArgumentException(currentLine);
				}

				Double time = Double.valueOf(effectiveValues[0]);
				Double offset = Double.valueOf(effectiveValues[1]);
				System.out.println(time.toString() + " -> " + offset.toString());
				result.add(new Pair<>(time, offset * 1000));
			}
		} while (currentLine != null);

		return result;
	}

	private ShakingBuildingController shaking;
}
