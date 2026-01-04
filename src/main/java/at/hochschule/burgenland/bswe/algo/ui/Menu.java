package at.hochschule.burgenland.bswe.algo.ui;

import at.hochschule.burgenland.bswe.algo.algorithm.Calculator;
import at.hochschule.burgenland.bswe.algo.comparator.*;
import at.hochschule.burgenland.bswe.algo.graph.FlightGraph;
import at.hochschule.burgenland.bswe.algo.io.CsvWriter;
import at.hochschule.burgenland.bswe.algo.model.Flight;
import at.hochschule.burgenland.bswe.algo.model.Route;
import at.hochschule.burgenland.bswe.algo.search.SearchEngine;
import at.hochschule.burgenland.bswe.algo.sorting.MergeSort;
import at.hochschule.burgenland.bswe.algo.sorting.QuickSort;

import java.util.*;

/**
 * Console menu for the flight routing system.
 */
public class Menu {

    private final Scanner scanner;
    private final FlightGraph graph;
    private final Calculator calculator;
    private final SearchEngine searchEngine;
    private final List<Route> savedRoutes;
    private int nextRouteId = 1;

    /**
     * Constructor without preloaded routes.
     *
     * @param graph the constructed graph
     */
    public Menu(FlightGraph graph) {
        this.scanner = new Scanner(System.in);
        this.graph = graph;
        this.calculator = new Calculator(graph);
        this.searchEngine = new SearchEngine(graph);
        this.savedRoutes = new ArrayList<>();
    }

    /**
     * Constructor with preloaded routes.
     * Initializes nextRouteId with the next available id.
     *
     * @param graph the constructed graph
     * @param existingRoutes the preloaded routes
     */
    public Menu(FlightGraph graph, List<Route> existingRoutes) {
        this.scanner = new Scanner(System.in);
        this.graph = graph;
        this.calculator = new Calculator(graph);
        this.searchEngine = new SearchEngine(graph);
        this.savedRoutes = new ArrayList<>(existingRoutes);

        this.nextRouteId = existingRoutes.stream()
                .mapToInt(Route::getId)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Starts the main menu UI loop.
     * Prompts the user to select which feature of the application should be used.
     */
    public void start() {
        while (true) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleRoutePlanning();
                    break;
                case "2":
                    handleSorting();
                    break;
                case "3":
                    handleSearch();
                    break;
                case "4":
                    handleSaveRoutes();
                    break;
                case "5":
                    System.out.println("\nVielen Dank! Auf Wiedersehen!");
                    return;
                default:
                    System.out.println("\nUngültige Eingabe. Bitte wählen Sie 1-5.");
            }
        }
    }

    /**
     * Prints the available menu options for the main menu.
     */
    private void printMainMenu() {
        System.out.println("\n");
        printSpacer();
        System.out.println("       FLUGROUTEN-PLANUNG");
        printSpacer();
        System.out.println("1. Routenplanung");
        System.out.println("2. Sortierung");
        System.out.println("3. Suche");
        System.out.println("4. Routen speichern");
        System.out.println("5. Beenden");
        printSpacer();
        System.out.print("Ihre Wahl: ");
    }

    /**
     * Handles the menu action "Routenplanung":
     * 1. Prompt the user to enter both origin and destination for their route
     * 2. Prompt the user to select the route using a selected metric
     * 3. Delegate calculation to Calculator class
     * 4. Evaluate and print result
     */
    private void handleRoutePlanning() {
        System.out.println("\n--- ROUTENPLANUNG ---");

        System.out.print("Start (IATA-Code, z.B. VIE): ");
        String origin = scanner.nextLine().trim().toUpperCase();

        if (!graph.hasAirport(origin)) {
            System.out.println("Fehler: Flughafen " + origin + " nicht gefunden!");
            return;
        }

        System.out.print("Ziel (IATA-Code, z.B. JFK): ");
        String destination = scanner.nextLine().trim().toUpperCase();

        if (!graph.hasAirport(destination)) {
            System.out.println("Fehler: Flughafen " + destination + " nicht gefunden!");
            return;
        }

        System.out.println("\nKriterium wählen:");
        System.out.println("1. Günstigste Route");
        System.out.println("2. Langsamste Route");
        System.out.println("3. Schnellste Route");
        System.out.println("4. Wenigste Umstiege");
        System.out.print("Ihre Wahl: ");

        String criteriaChoice = scanner.nextLine().trim();
        Route route = null;
        String criteriaName = "";

        switch (criteriaChoice) {
            case "1":
                route = calculator.findCheapestRoute(origin, destination);
                criteriaName = "Günstigste Route";
                break;
            case "2":
                route = calculator.findSlowestRoute(origin, destination);
                criteriaName = "Langsamste Route";
                break;
            case "3":
                route = calculator.findFastestRoute(origin, destination);
                criteriaName = "Schnellste Route";
                break;
            case "4":
                route = calculator.findFewestStopoverRoute(origin, destination);
                criteriaName = "Wenigste Umstiege";
                break;
            default:
                System.out.println("Ungültige Auswahl!");
                return;
        }

        if (route == null) {
            System.out.println("\nKeine Route von " + origin + " nach " + destination + " gefunden!");
        } else {
            route = new Route(nextRouteId++, route.getFlights(), route.getTotalDuration(),
                    route.getTotalPrice(), route.getStopovers());
            savedRoutes.add(route);

            System.out.println("\n" + criteriaName + " gefunden:");
            System.out.println(route);
            printRouteDetails(route);
        }
    }

    /**
     * Handles the menu action "Sortierung":
     * 1. Check for and display saved routes
     * 2. Prompt the user to select which routes to sort
     * 3. Prompt the user to select sorting algorithm
     * 4. Prompt the user to select sorting criteria
     * 5. Delegate sorting to sorting classes
     * 6. Print result
     */
    private void handleSorting() {
        System.out.println("\n--- SORTIERUNG ---");

        if (savedRoutes.isEmpty()) {
            System.out.println("Keine gespeicherten Routen vorhanden!");
            System.out.println("Bitte berechnen Sie zuerst Routen.");
            return;
        }

        System.out.println("\nVerfügbare Routen:");
        savedRoutes.forEach(System.out::println);

        System.out.print("\nRouten-IDs zum Sortieren (kommagetrennt, z.B. 1,2,3): ");
        String input = scanner.nextLine().trim();

        List<Route> routesToSort = new ArrayList<>();
        try {
            String[] ids = input.split(",");
            for (String id : ids) {
                int routeId = Integer.parseInt(id.trim());
                Route route = savedRoutes.stream()
                        .filter(r -> r.getId() == routeId)
                        .findFirst()
                        .orElse(null);

                if (route != null) {
                    if (!routesToSort.contains(route)) {
                        routesToSort.add(route);
                    } else {
                        System.out.println("Route mit ID " + routeId + " wurde bereits ausgewählt.");
                    }
                } else {
                    System.out.println("Route mit ID " + routeId + " nicht gefunden!");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Fehler: Ungültiges Format!");
            return;
        }

        if (routesToSort.isEmpty()) {
            System.out.println("Keine gültigen Routen zum Sortieren!");
            return;
        }

        System.out.println("\nSortieralgorithmus wählen:");
        System.out.println("1. Merge Sort (stabil)");
        System.out.println("2. Quick Sort (instabil)");
        System.out.print("Ihre Wahl: ");
        String algorithmChoice = scanner.nextLine().trim();

        System.out.println("\nSortierkriterium wählen:");
        System.out.println("1. Preis (aufsteigend)");
        System.out.println("2. Dauer (aufsteigend)");
        System.out.println("3. Umstiege (aufsteigend)");
        System.out.println("4. Kombination (Preis, Dauer, Umstiege)");
        System.out.print("Ihre Wahl: ");
        String criteraChoice = scanner.nextLine().trim();

        Comparator<Route> comparator = getComparator(criteraChoice);
        if (comparator == null) {
            System.out.println("Ungültige Comparator-Auswahl!");
            return;
        }

        List<Route> sortedRoutes = new ArrayList<>(routesToSort);

        if ("1".equals(algorithmChoice)) {
            MergeSort.sort(sortedRoutes, comparator);
            System.out.println("\nSortiert mit: Stable Merge Sort");
        } else if ("2".equals(algorithmChoice)) {
            QuickSort.sort(sortedRoutes, comparator);
            System.out.println("\nSortiert mit: Unstable Quick Sort");
        } else {
            System.out.println("Ungültige Algorithmus-Auswahl!");
            return;
        }

        System.out.println("\nSortierte Routen:");
        sortedRoutes.forEach(System.out::println);
    }

    /**
     * Handles the menu action "Suche":
     * 1. Prompt the user to select search criteria
     * 2. Calls selected search method
     */
    private void handleSearch() {
        System.out.println("\n--- SUCHE ---");
        System.out.println("1. Nach Origin (Abflugort)");
        System.out.println("2. Nach Destination (Zielort)");
        System.out.println("3. Nach Airline");
        System.out.println("4. Nach Flugnummer");
        System.out.print("Ihre Wahl: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                searchByOrigin();
                break;
            case "2":
                searchByDestination();
                break;
            case "3":
                searchByAirline();
                break;
            case "4":
                searchByFlightNumber();
                break;
            default:
                System.out.println("Ungültige Auswahl!");
        }
    }

    /**
     * Handles the menu action "Routen speichern":
     * 1. Check for routes to save
     * 2. Write them into routes.csv
     */
    private void handleSaveRoutes() {
        if (savedRoutes.isEmpty()) {
            System.out.println("\nKeine Routen zum Speichern vorhanden!");
            return;
        }

        CsvWriter.writeRoutes("routes.csv", savedRoutes);
        System.out.println("\n" + savedRoutes.size() + " Routen wurden gespeichert!");
    }

    /**
     * Handles search by origin:
     * 1. Prompt user to enter origin
     * 2. Delegate searching to SearchEngine class
     * 3. Print result
     */
    private void searchByOrigin() {
        System.out.print("\nOrigin (IATA-Code): ");
        String iata = scanner.nextLine().trim();

        SearchEngine.SearchResult result = searchEngine.searchByOrigin(iata);

        if (!result.hasResults()) {
            System.out.println("Keine Ergebnisse gefunden!");
            return;
        }

        System.out.println("\nFlughafen: " + result.airport());
        System.out.println("\nAbflüge von " + iata + " (" + result.flights().size() + " Flüge):");
        result.flights().forEach(System.out::println);
    }

    /**
     * Handles search by destination:
     * 1. Prompt user to enter destination
     * 2. Delegate searching to SearchEngine class
     * 3. Print result
     */
    private void searchByDestination() {
        System.out.print("\nDestination (IATA-Code): ");
        String iata = scanner.nextLine().trim();

        SearchEngine.SearchResult result = searchEngine.searchByDestination(iata);

        if (!result.hasResults()) {
            System.out.println("Keine Ergebnisse gefunden!");
            return;
        }

        System.out.println("\nFlughafen: " + result.airport());
        System.out.println("\nAnkünfte in " + iata + " (" + result.flights().size() + " Flüge):");
        result.flights().forEach(System.out::println);
    }

    /**
     * Handles search by airline:
     * 1. Prompt user to enter origin
     * 2. Delegate searching to SearchEngine class, which performs a
     * case-insensitive partial match -> input can match any part of the airline name
     * 3. Print result
     */
    private void searchByAirline() {
        System.out.print("\nAirline: ");
        String airline = scanner.nextLine().trim();

        List<Flight> flights = searchEngine.searchByAirline(airline);

        if (flights.isEmpty()) {
            System.out.println("Keine Flüge gefunden!");
            return;
        }

        System.out.println("\nGefundene Flüge (" + flights.size() + "):");
        flights.forEach(System.out::println);
    }

    /**
     * Handles search by flight number:
     * 1. Prompt user to enter flight number
     * 2. Delegate searching to SearchEngine class
     * 3. Print result
     */
    private void searchByFlightNumber() {
        System.out.print("\nFlugnummer: ");
        String flightNumber = scanner.nextLine().trim();

        Flight flight = searchEngine.searchByFlightNumber(flightNumber);

        if (flight == null) {
            System.out.println("Flug nicht gefunden!");
            return;
        }

        System.out.println("\nFlug Details:");
        System.out.println("  ID:           " + flight.getId());
        System.out.println("  Flugnummer:   " + flight.getFlightNumber());
        System.out.println("  Airline:      " + flight.getAirline());
        System.out.println("  Strecke:      " + flight.getOrigin() + " -> " + flight.getDestination());
        System.out.println("  Abflug:       " + flight.getDepartureTime());
        System.out.println("  Dauer:        " + flight.getDuration() + " Minuten");
        System.out.println("  Preis:        €" + String.format("%.2f", flight.getPrice()));
    }

    /**
     * Selects and returns the comparator based on the given choice.
     *
     * @param choice choice based on user input
     * @return selected Comparator
     */
    private Comparator<Route> getComparator(String choice) {
        return switch (choice) {
            case "1" -> new PriceComparator();
            case "2" -> new DurationComparator();
            case "3" -> new StopoverComparator();
            case "4" -> new CombinationComparator();
            default -> null;
        };
    }

    /**
     * Prints the details for each flight of a given route.
     *
     * @param route route for which details should be displayed
     */
    private void printRouteDetails(Route route) {
        System.out.println("\nRoute Details:");
        for (int flightId : route.getFlights()) {
            Flight flight = graph.getFlightById(flightId);
            if (flight != null) {
                System.out.println("  - " + flight);
            }
        }
    }


    /**
     * Prints equals signs as spacer.
     */
    private void printSpacer() {
        System.out.println("=".repeat(50));
    }
}