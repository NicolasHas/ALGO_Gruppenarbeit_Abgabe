package at.hochschule.burgenland.bswe.algo;

import at.hochschule.burgenland.bswe.algo.graph.FlightGraph;
import at.hochschule.burgenland.bswe.algo.io.CsvReader;
import at.hochschule.burgenland.bswe.algo.model.Airport;
import at.hochschule.burgenland.bswe.algo.model.Flight;
import at.hochschule.burgenland.bswe.algo.model.Route;
import at.hochschule.burgenland.bswe.algo.ui.Menu;

import java.util.Iterator;
import java.util.List;

public class FlightPlannerApplication {

    /**
     * Main workflow consisting of the following steps:
     * 1. Load data from resources
     * 2. Initialize flight graph
     * 3. Start menu (with optional routes)
     */
    public static void run() {

        System.out.println("\n=== Flugrouten-Planung System ===");
        System.out.println("Initialisierung...\n");

        List<Airport> airports = CsvReader.readAirports("airports.csv");
        List<Flight> flights = CsvReader.readFlights("flights.csv");
        List<Route> routes = CsvReader.readRoutes("routes.csv");

        if (airports.isEmpty() || flights.isEmpty()) {
            System.err.println("Fehler beim Laden der Daten!");
            System.err.println("Flughäfen: " + airports.size() + ", Flüge: " + flights.size());
            return;
        }


        FlightGraph graph = new FlightGraph();
        airports.forEach(graph::addAirport);

        Iterator<Flight> iterator = flights.iterator();

        while (iterator.hasNext()) {
            Flight flight = iterator.next();
            try {
                graph.addFlight(flight);
            } catch (IllegalArgumentException e) {
                System.out.println("Flug " + flight.getFlightNumber() + " konnte nicht geladen werden: " + e.getMessage());
                iterator.remove();
            }
        }

        System.out.println("Erfolgreich geladen:");
        System.out.println("  - " + airports.size() + " Flughäfen");
        System.out.println("  - " + flights.size() + " Flüge");
        if (!routes.isEmpty()) {
            System.out.println("  - " + routes.size() + " gespeicherte Routen");
        }

        Menu menu;
        if (!routes.isEmpty()) {
            menu = new Menu(graph, routes);
        } else {
            menu = new Menu(graph);
        }
        menu.start();
    }
}
