package at.hochschule.burgenland.bswe.algo.algorithm;

import at.hochschule.burgenland.bswe.algo.graph.FlightGraph;
import at.hochschule.burgenland.bswe.algo.model.Airport;
import at.hochschule.burgenland.bswe.algo.model.Flight;
import at.hochschule.burgenland.bswe.algo.model.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    private FlightGraph graph;
    private Calculator calculator;

    @BeforeEach
    void setUp() {
        graph = new FlightGraph();
        graph.addAirport(new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3));
        graph.addAirport(new Airport(2, "JFK", "New York", "USA", 40.6, -73.8));
        graph.addAirport(new Airport(3, "LHR", "London", "UK", 51.5, -0.4));

        graph.addFlight(new Flight(1, "VIE", "LHR", "Austrian", "OS100", 60, 100, LocalTime.of(8, 0)));
        graph.addFlight(new Flight(2, "LHR", "JFK", "British Airways", "BA150", 360, 400, LocalTime.of(10, 0)));
        graph.addFlight(new Flight(3, "VIE", "JFK", "Austrian", "OS101", 500, 550, LocalTime.of(9, 0)));

        calculator = new Calculator(graph);
    }

    @Test
    void testCheapestRoute() {
        Route route = calculator.findCheapestRoute("VIE", "JFK");

        assertNotNull(route);
        assertEquals(2, route.getFlights().size());
        assertEquals(500.0, route.getTotalPrice());
    }

    @Test
    void testFastestRoute() {
        Route route = calculator.findFastestRoute("VIE", "JFK");

        assertNotNull(route);
        assertEquals(2, route.getFlights().size());
        assertEquals(420, route.getTotalDuration());

        Flight firstFlight = graph.getFlightById(route.getFlights().get(0));

        assertNotNull(firstFlight);
        assertEquals("VIE", firstFlight.getOrigin());
        assertEquals("LHR", firstFlight.getDestination());

        Flight secondFlight = graph.getFlightById(route.getFlights().get(1));

        assertNotNull(secondFlight);
        assertEquals("LHR", secondFlight.getOrigin());
        assertEquals("JFK", secondFlight.getDestination());
    }

    @Test
    void testNoRoute() {
        Route route = calculator.findCheapestRoute("VIE", "XYZ");

        assertNull(route);
    }

    @Test
    void testFewestStopovers() {
        Route route = calculator.findFewestStopoverRoute("VIE", "JFK");

        assertNotNull(route);
        assertEquals(1, route.getFlights().size());
        assertEquals(0, route.getStopovers());
    }

    @Test
    void testSlowestRoute() {
        graph.addAirport(new Airport(4, "CDG", "Paris", "France", 49.0, 2.5));
        graph.addFlight(new Flight(
            4, "VIE", "CDG", "Austrian", "OS200",
            300, 120, LocalTime.of(6, 0)
        ));
        graph.addFlight(new Flight(
            5, "CDG", "JFK", "Air France", "AF100",
            400, 380, LocalTime.of(13, 0)
        ));

        Route route = calculator.findSlowestRoute("VIE", "JFK");

        assertNotNull(route);
        assertEquals(700, route.getTotalDuration());
        assertEquals(2, route.getFlights().size());
    }

    @Test
    void testConnectionTimeConstraint() {
        graph.addFlight(new Flight(4, "LHR", "JFK", "BA", "BA200", 360, 400, LocalTime.of(8, 15)));

        Route route = calculator.findCheapestRoute("VIE", "JFK");

        assertNotNull(route);
        assertFalse(route.getFlights().stream()
                .anyMatch(flightId -> {
                    Flight f = graph.getFlightById(flightId);
                    return f != null && f.getFlightNumber().equals("BA200");
                }));
    }

    @Test
    void testStopoverLimit() {
        graph.addAirport(new Airport(4, "AAA", "A", "A", 0, 0));
        graph.addAirport(new Airport(5, "BBB", "B", "B", 1, 1));
        graph.addAirport(new Airport(6, "CCC", "C", "C", 2, 2));

        graph.addFlight(new Flight(5, "VIE", "AAA", "OS", "OS200", 60, 50, LocalTime.of(7, 0)));
        graph.addFlight(new Flight(6, "AAA", "BBB", "OS", "OS201", 60, 50, LocalTime.of(8, 0)));
        graph.addFlight(new Flight(7, "BBB", "CCC", "OS", "OS202", 60, 50, LocalTime.of(9, 0)));
        graph.addFlight(new Flight(8, "CCC", "JFK", "OS", "OS203", 60, 50, LocalTime.of(10, 0)));

        Route route = calculator.findCheapestRoute("VIE", "JFK");

        assertNotEquals(5, route.getFlights().size());
    }

    @Test
    void testRouteCreationFromState() {
        Route route = calculator.findFastestRoute("VIE", "JFK");

        assertNotNull(route);
        assertFalse(route.getFlights().isEmpty());
        assertEquals(route.getFlights().size() - 1, route.getStopovers());
    }

    @Test
    void testRouteWithUnknownAirport() {
        Route route = calculator.findFastestRoute("XXX", "JFK");

        assertNull(route);
    }

    @Test
    void testRouteWithEmptyGraph() {
        Calculator emptyCalculator = new Calculator(new FlightGraph());

        Route route = emptyCalculator.findFastestRoute("VIE", "JFK");

        assertNull(route);
    }
}

