package at.hochschule.burgenland.bswe.algo.search;

import at.hochschule.burgenland.bswe.algo.graph.FlightGraph;
import at.hochschule.burgenland.bswe.algo.model.Airport;
import at.hochschule.burgenland.bswe.algo.model.Flight;

import java.util.ArrayList;
import java.util.List;

/**
 * Search engine for flights and airports stored in a FlightGraph.
 * Provides read-only, linear search operations.
 */
public class SearchEngine {

    private final FlightGraph graph;

    /**
     * Constructor with the graph to query.
     *
     * @param graph the graph containing airports and flights to be searched
     */
    public SearchEngine(FlightGraph graph) {
        this.graph = graph;
    }

    /**
     * Searches for an airport by origin (IATA code) and returns the airport
     * along with all flights departing from it.
     *
     * @param iata the IATA code of the origin airport
     * @return a SearchResult containing the airport and all outgoing flights
     */
    public SearchResult searchByOrigin(String iata) {
        Airport airport = graph.getAirport(iata.trim().toUpperCase());
        if (airport == null) {
            return new SearchResult(null, new ArrayList<>());
        }

        List<Flight> flights = graph.getFlightsFrom(iata.trim().toUpperCase());
        return new SearchResult(airport, flights);
    }

    /**
     * Searches for an airport by destination (IATA code) and returns the airport
     * along with all flights arriving at it.
     *
     * @param iata the IATA code of the origin airport
     * @return a SearchResult containing the airport and all incoming flights
     */
    public SearchResult searchByDestination(String iata) {
        Airport airport = graph.getAirport(iata.trim().toUpperCase());
        if (airport == null) {
            return new SearchResult(null, new ArrayList<>());
        }

        String destination = iata.trim().toUpperCase();
        List<Flight> flights = graph.getAllFlights().stream()
                .filter(f -> f.getDestination().equalsIgnoreCase(destination))
                .toList();

        return new SearchResult(airport, flights);
    }

    /**
     * Searches for all flights by airline name.
     * Uses case-insensitive partial matching.
     *
     * @param airline the airline name or partial name
     * @return a list of matching flights
     */
    public List<Flight> searchByAirline(String airline) {
        String searchTerm = airline.trim().toLowerCase();
        return graph.getAllFlights().stream()
                .filter(f -> f.getAirline().toLowerCase().contains(searchTerm))
                .toList();
    }

    /**
     * Searches for a specific flight by flight number.
     * Returns the first matching flight or null if not found.
     *
     * @param flightNumber the flight number to search for
     * @return the matching flight, or null if none is found
     */
    public Flight searchByFlightNumber(String flightNumber) {
        String searchTerm = flightNumber.trim().toUpperCase();
        return graph.getAllFlights().stream()
                .filter(f -> f.getFlightNumber().equalsIgnoreCase(searchTerm))
                .findFirst()
                .orElse(null);
    }

    /**
     * Container object holding the result of an airport-related search.
     * Combines the found airport with a list of associated flights
     *
     * @param airport the found airport
     * @param flights its associated flights
     */
    public record SearchResult(Airport airport, List<Flight> flights) {

        /**
         * Determines whether this search result contains any data.
         *
         * @return true if an airport or at least one flight was found,
         *         false otherwise
         */
    public boolean hasResults() {
            return airport != null || !flights.isEmpty();
        }
    }
}