package at.hochschule.burgenland.bswe.algo.algorithm;

import at.hochschule.burgenland.bswe.algo.graph.FlightGraph;
import at.hochschule.burgenland.bswe.algo.model.Flight;
import at.hochschule.burgenland.bswe.algo.model.Route;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Calculates optimal routes between airports based on given criteria.
 * Supports the following optimization strategies:
 * - Cheapest route (PRICE)
 * - Fastest route (DURATION)
 * - Slowest route (DURATION_MAX)
 * - Fewest stopovers (STOPOVERS)
 * Routes are represented as graph searches over the given flight graph.
 * Uses a priority queue to expand partial routes in order of selected cost.
 * Tracks visited airports with their best cost via RouteState to avoid redundancy.
 * Works with restraints for time required between flights and maximum amount of stopovers.
 */
public class Calculator {

    private static final int MIN_CONNECTION_TIME = 20; // Minutes
    private static final int MAX_STOPOVERS = 3; // Maximum 3 stopovers = 4 flights

    private final FlightGraph graph;

    public Calculator(FlightGraph graph) {
        this.graph = graph;
    }

    /**
     * Finds the route with the lowest price from origin to destination.
     *
     * @param origin IATA of origin
     * @param destination IATA ot destination
     * @return the cheapest route
     */
    public Route findCheapestRoute(String origin, String destination) {
        return findOptimalRoute(origin, destination, RouteOptimization.PRICE);
    }

    /**
     * Finds the route which takes the most amount of time from origin to destination.
     *
     * @param origin IATA of origin
     * @param destination IATA ot destination
     * @return the slowest route
     */
    public Route findSlowestRoute(String origin, String destination) {
        return findOptimalRoute(origin, destination, RouteOptimization.DURATION_MAX);
    }

    /**
     * Finds the route which takes the least amount of time from origin to destination.
     *
     * @param origin IATA of origin
     * @param destination IATA ot destination
     * @return the fastest route
     */
    public Route findFastestRoute(String origin, String destination) {
        return findOptimalRoute(origin, destination, RouteOptimization.DURATION);
    }

    /**
     * Finds the route with the fewest stopovers from origin to destination.
     *
     * @param origin IATA of origin
     * @param destination IATA ot destination
     * @return the route with the fewest stopovers
     */
    public Route findFewestStopoverRoute(String origin, String destination) {
        return findOptimalRoute(origin, destination, RouteOptimization.STOPOVERS);
    }

    /**
     * Generic method to find optimal route based on optimization.
     * Uses a modified Dijkstra's algorithm:
     * 1. Initialize PriorityQueue with Comparator based on optimization
     * 2. While the queue is not empty expand the next best route (= route with the lowest cost)
     * 3. Check if the destination has been reached and stopovers were not exceeded
     * 4. Check if a better costed route to current airport exists
     * 5. Explore outgoing flights from this airport
     * 6. Return the best route found
     *
     * @param origin IATA of origin
     * @param destination IATA of destination
     * @param optimization RouteOptimization selected for cost calculation
     * @return the best route calculated
     */
    private Route findOptimalRoute(String origin, String destination, RouteOptimization optimization) {
        if (!graph.hasAirport(origin) || !graph.hasAirport(destination)) {
            return null;
        }

        PriorityQueue<RouteState> queue = new PriorityQueue<>(
            Comparator.comparingDouble(state -> state.getCost(optimization))
        );

        Map<String, Double> bestCost = new HashMap<>();
        RouteState bestSolution = null;

        queue.add(new RouteState(origin, new ArrayList<>(), null));

        while (!queue.isEmpty()) {
            RouteState current = queue.poll();

            if (current.currentAirport.equals(destination)) {
                if (bestSolution == null || current.getCost(optimization) < bestSolution.getCost(optimization)) {
                    bestSolution = current;
                }
                continue;
            }

            if (current.flightPath.size() > MAX_STOPOVERS + 1) {
                continue;
            }

            String stateKey = current.currentAirport + "_" + current.flightPath.size();
            if (bestCost.containsKey(stateKey) && bestCost.get(stateKey) <= current.getCost(optimization)) {
                continue;
            }
            bestCost.put(stateKey, current.getCost(optimization));

            for (Flight flight : graph.getFlightsFrom(current.currentAirport)) {
                if (isValidConnection(current.lastFlight, flight)) {
                    List<Flight> newPath = new ArrayList<>(current.flightPath);
                    newPath.add(flight);
                    queue.add(new RouteState(flight.getDestination(), newPath, flight));
                }
            }
        }

        return bestSolution != null ? bestSolution.toRoute() : null;
    }

    /**
     * Determines whether a connection between two flights is valid.
     * A connection is considered valid if the layover time MIN_CONNECTION_TIME is not exceeded.
     * Day wraparounds are handled by adding 24 hours, as flights depart daily at the same time.
     *
     * @param previous previous flight or null if this is the first flight
     * @param next the current flight being considered
     * @return true if valid,
     *         false otherwise
     */
    private boolean isValidConnection(Flight previous, Flight next) {
        if (previous == null) {
            return true;
        }

        LocalTime arrival = previous.getDepartureTime().plusMinutes(previous.getDuration());
        LocalTime departure = next.getDepartureTime();

        if (departure.isBefore(arrival)) {
            departure = departure.plusHours(24);
        }

        return ChronoUnit.MINUTES.between(arrival, departure) >= MIN_CONNECTION_TIME;
    }

    /**
     * Represents the current state of a partial route during route search.
     * Used by the route-finding algorithm to track:
     * - current airport being considered
     * - sequence of flights taken so far
     * - the last flight in the current path
     * Provides helper methods to calculate the cost based on the selected optimization.
     */
    private static class RouteState {
        String currentAirport;
        List<Flight> flightPath;
        Flight lastFlight;

        RouteState(String currentAirport, List<Flight> flightPath, Flight lastFlight) {
            this.currentAirport = currentAirport;
            this.flightPath = flightPath;
            this.lastFlight = lastFlight;
        }

        /**
         * Calculates the total cost of this partial route based on the given optimization.
         *
         * @param optimization optimization selected
         * @return the total cost according to the optimization
         */
        double getCost(RouteOptimization optimization) {
            if (flightPath.isEmpty()) {
                return 0;
            }

            return switch (optimization) {
                case PRICE -> flightPath.stream().mapToDouble(Flight::getPrice).sum();
                case DURATION -> flightPath.stream().mapToInt(Flight::getDuration).sum();
                case DURATION_MAX -> -flightPath.stream().mapToInt(Flight::getDuration).sum();
                case STOPOVERS -> flightPath.size();
            };
        }

        /**
         * Converts the current route state into a Route object.
         *
         * @return a Route that represents the current flight sequence or null if the path is empty
         */
        Route toRoute() {
            if (flightPath.isEmpty()) {
                return null;
            }
            return new Route(0, flightPath);
        }
    }

    /**
     * Enum defining the possible optimization criteria for route finding.
     */
    private enum RouteOptimization {
        PRICE,
        DURATION,
        DURATION_MAX,
        STOPOVERS
    }
}