import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PeaksAndValleys {
    private List<Double> numbers;

    public PeaksAndValleys(String csvFilePath) throws IOException, CsvException {
        this.numbers = readNumbersFromCSV(csvFilePath);
    }

    public List<int[]> findPeaksAndValleys(double threshold) {
        List<int[]> results = new ArrayList<>();
        int n = numbers.size();

        if (n < 3) {
            // If less than 3 numbers, no valid peaks and valleys can exist
            return results;
        }

        // Track the last peak or valley index and value
        int lastIndex = -1;
        double lastValue = 0;
        boolean lookingForPeak = true;

        for (int i = 1; i < n - 1; i++) {
            double current = numbers.get(i);
            double prev = numbers.get(i - 1);
            double next = numbers.get(i + 1);

            if (lookingForPeak && current > prev && current > next && (lastIndex == -1 || Math.abs(current - lastValue) >= threshold)) {
                // Found a peak
                results.add(new int[]{i, (int) current});
                lastIndex = i;
                lastValue = current;
                lookingForPeak = false; // Now look for a valley
            } else if (!lookingForPeak && current < prev && current < next && (lastIndex == -1 || Math.abs(current - lastValue) >= threshold)) {
                // Found a valley
                results.add(new int[]{i, (int) current});
                lastIndex = i;
                lastValue = current;
                lookingForPeak = true; // Now look for a peak
            }
        }

        return results;
    }

    private List<Double> readNumbersFromCSV(String csvFilePath) throws IOException, CsvException {
        List<Double> numbers = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            List<String[]> csvData = reader.readAll();
            for (int i = 1; i < csvData.size(); i++) { // Start from index 1 to skip the header row
                String[] row = csvData.get(i);
                for (String cell : row) {
                    numbers.add(Double.parseDouble(cell.trim()));
                }
            }
        }

        return numbers;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Example usage with a CSV file
        System.out.print("Enter the path to your CSV file: ");
        String csvFilePath = scanner.nextLine().trim();

        System.out.print("Enter the threshold value: ");
        double threshold = scanner.nextDouble();
        scanner.nextLine(); // Consume newline left after nextDouble()

        try {
            PeaksAndValleys pv = new PeaksAndValleys(csvFilePath);
            List<int[]> peaksAndValleys = pv.findPeaksAndValleys(threshold);
            System.out.println("Indices and values of peaks and valleys:");
            for (int i = 0; i < peaksAndValleys.size(); i += 2) {
                if (i < peaksAndValleys.size()) {
                    int[] peak = peaksAndValleys.get(i);
                    System.out.println("Peak: Index = " + peak[0] + ", Value = " + pv.numbers.get(peak[0]));
                }
                if (i + 1 < peaksAndValleys.size()) {
                    int[] valley = peaksAndValleys.get(i + 1);
                    System.out.println("Valley: Index = " + valley[0] + ", Value = " + pv.numbers.get(valley[0]));
                    System.out.println();
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
