package at.hochschule.burgenland.bswe.algo.graph;

import at.hochschule.burgenland.bswe.algo.model.Airport;
import at.hochschule.burgenland.bswe.algo.model.Flight;

import java.util.*;

/**
 * Directed graph structure modeling a flight network where
 * vertices represent airports and edges represent flights.
 * The direction is from origin airport to destination airport.
 * Includes:
 * - a node registry map, mapping the IATA of airports to their respective objects
 * - an adjacency map, mapping the IATA of airports to all possible direct flights (the actual graph)
 * - a flight map, mapping the ids of flights to their respective flight objects
 */
public class FlightGraph {

    private final Map<String, Airport> airports;
    private final Map<String, List<Flight>> adjacencyList;
    private final Map<Integer, Flight> flightById;

    public FlightGraph() {
        this.airports = new HashMap<>();
        this.adjacencyList = new HashMap<>();
        this.flightById = new HashMap<>();
    }

    /**
     * Adds an airport (vertex) to the graph.
     * Does not add any flights -> vertex is isolated.
     *
     * @param airport airport to be added
     */
    public void addAirport(Airport airport) {
        airports.put(airport.getIata(), airport);
        adjacencyList.putIfAbsent(airport.getIata(), new ArrayList<>());
    }

    /**
     * Adds a flight (directed edge) to the graph.
     * Throws IllegalArgumentException if the airport is not recognized.
     *
     * @param flight flight to be added
     */
    public void addFlight(Flight flight) {
        if (!airports.containsKey(flight.getOrigin()) ||
            !airports.containsKey(flight.getDestination())) {
            throw new IllegalArgumentException(
                "Flug referenziert unbekannten Flughafen."
            );
        }

        adjacencyList
            .computeIfAbsent(flight.getOrigin(), k -> new ArrayList<>())
            .add(flight);

        flightById.put(flight.getId(), flight);
    }

    /**
     * Gets all outgoing flights from a specific airport.
     *
     * @param iata iata of the airport to get flights from
     */
    public List<Flight> getFlightsFrom(String iata) {
        return adjacencyList.getOrDefault(iata, new ArrayList<>());
    }

    /**
     * Gets an airport by IATA code.
     *
     * @param iata iata of the airport to get
     */
    public Airport getAirport(String iata) {
        return airports.get(iata);
    }

    /**
     * Gets a flight by ID.
     *
     * @param id id of the flight to get
     */
    public Flight getFlightById(int id) {
        return flightById.get(id);
    }

    /**
     * Gets all flights.
     */
    public Collection<Flight> getAllFlights() {
        return flightById.values();
    }

    /**
     * Checks if an airport (vertex) exists in the graph.
     *
     * @param iata iata to be searched for
     */
    public boolean hasAirport(String iata) {
        return airports.containsKey(iata);
    }
}