package at.hochschule.burgenland.bswe.algo.io;

import at.hochschule.burgenland.bswe.algo.model.Airport;
import at.hochschule.burgenland.bswe.algo.model.Flight;
import at.hochschule.burgenland.bswe.algo.model.Route;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for reading CSV files from resources folder.
 */
public class CsvReader {

    /**
     * Reads airports from a given CSV file.
     */
    public static List<Airport> readAirports(String fileName) {
        List<Airport> airports = new ArrayList<>();

        try (InputStream is = CsvReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                System.err.println("Datei nicht gefunden: " + fileName);
                return airports;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                reader.readLine();
                String line;

                while ((line = reader.readLine()) != null) {
                    try {
                        airports.add(Airport.fromCsv(line));
                    } catch (Exception e) {
                        System.err.println(fileName + ": Fehler in Zeile: " + line);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(fileName + "Fehler beim Laden der Flughäfen: " + e.getMessage());
        }
        return airports;
    }

    /**
     * Reads flights from a given CSV file.
     */
    public static List<Flight> readFlights(String fileName) {
        List<Flight> flights = new ArrayList<>();

        try (InputStream is = CsvReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                System.err.println("Datei nicht gefunden: " + fileName);
                return flights;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                reader.readLine();
                String line;

                while ((line = reader.readLine()) != null) {
                    try {
                            flights.add(Flight.fromCsv(line));
                    } catch (Exception e) {
                        System.err.println(fileName + "Fehler in Zeile: " + line);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(fileName + "Fehler beim Laden der Flüge: " + e.getMessage());
        }
        return flights;
    }

    /**
     * Reads routes from a given CSV file.
     */
    public static List<Route> readRoutes(String fileName) {
        List<Route> routes = new ArrayList<>();
        try (InputStream is = CsvReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                System.out.println("Keine Routen definiert.");
                return routes;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                reader.readLine();
                String line;

                while ((line = reader.readLine()) != null) {
                    try {
                        routes.add(Route.fromCsv(line));
                    } catch (Exception e) {
                        System.err.println(fileName + "Fehler in Zeile: " + line);
                    }
                }
            }
        } catch (Exception e) {
            // Loading routes is optional
        }
        return routes;
    }
}