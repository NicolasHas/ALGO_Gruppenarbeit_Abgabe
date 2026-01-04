package at.hochschule.burgenland.bswe.algo.io;

import at.hochschule.burgenland.bswe.algo.model.Route;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Utility class for writing CSV files.
 */
public class CsvWriter {

    /**
     * Writes a list of Route objects to a CSV file in the resources folder.
     * Each route is written as a single row with the following columns:
     * - id
     * - flights
     * - totalDuration
     * - totalPrice
     * - stopovers
     *
     * @param fileName name of the CSV file to write
     * @param routes the list of routes to write
     */
    public static void writeRoutes(String fileName, List<Route> routes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/" + fileName))) {

            writer.write("id,flights,totalDuration,totalPrice,stopovers");
            writer.newLine();

            for (Route route : routes) {
                String flightIds = route.getFlights().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining("-"));

                writer.write(String.format(Locale.ROOT, "%d,%s,%d,%.2f,%d",
                        route.getId(),
                        flightIds,
                        route.getTotalDuration(),
                        route.getTotalPrice(),
                        route.getStopovers()));
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error writing routes: " + e.getMessage());
        }
    }
}