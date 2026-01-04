package at.hochschule.burgenland.bswe.algo.graph;

import at.hochschule.burgenland.bswe.algo.model.Airport;
import at.hochschule.burgenland.bswe.algo.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlightGraphTest {

    private FlightGraph graph;

    @BeforeEach
    void setUp() {
        graph = new FlightGraph();
    }

    @Test
    void testAddAirport() {
        Airport airport = new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3);

        graph.addAirport(airport);

        assertTrue(graph.hasAirport("VIE"));
        assertEquals(airport, graph.getAirport("VIE"));
    }

    @Test
    void testAddMultipleAirports() {
        Airport vie = new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3);
        Airport jfk = new Airport(2, "JFK", "New York", "USA", 40.6, -73.8);
        Airport lhr = new Airport(3, "LHR", "London", "UK", 51.5, -0.4);

        graph.addAirport(vie);
        graph.addAirport(jfk);
        graph.addAirport(lhr);

        assertTrue(graph.hasAirport("VIE"));
        assertTrue(graph.hasAirport("JFK"));
        assertTrue(graph.hasAirport("LHR"));
    }

    @Test
    void testGetAirportExists() {
        Airport airport = new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3);

        graph.addAirport(airport);

        Airport retrieved = graph.getAirport("VIE");

        assertNotNull(retrieved);
        assertEquals("VIE", retrieved.getIata());
        assertEquals("Vienna", retrieved.getCity());
        assertEquals("Austria", retrieved.getCountry());
    }

    @Test
    void testGetAirportNotExists() {
        Airport retrieved = graph.getAirport("XYZ");

        assertNull(retrieved);
    }

    @Test
    void testHasAirportExists() {
        Airport airport = new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3);

        graph.addAirport(airport);

        assertTrue(graph.hasAirport("VIE"));
    }

    @Test
    void testHasAirportNotExists() {
        assertFalse(graph.hasAirport("XYZ"));
    }

    @Test
    void testAddFlight() {
        graph.addAirport(new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3));
        graph.addAirport(new Airport(2, "LHR", "London", "UK", 51.5, -0.4));

        Flight flight = new Flight(1, "VIE", "LHR", "Austrian", "OS100", 120, 250, LocalTime.of(8, 0));

        graph.addFlight(flight);

        List<Flight> flights = graph.getFlightsFrom("VIE");

        assertEquals(1, flights.size());
        assertEquals(flight, flights.get(0));
    }

    @Test
    void testAddMultipleFlightsFromSameOrigin() {
        graph.addAirport(new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3));
        graph.addAirport(new Airport(2, "LHR", "London", "UK", 51.5, -0.4));
        graph.addAirport(new Airport(3, "FRA", "Frankfurt", "Germany", 50.0, 8.5));

        Flight flight1 = new Flight(1, "VIE", "LHR", "Austrian", "OS100", 120, 250, LocalTime.of(8, 0));
        Flight flight2 = new Flight(2, "VIE", "FRA", "Lufthansa", "LH200", 90, 180, LocalTime.of(9, 30));

        graph.addFlight(flight1);
        graph.addFlight(flight2);

        List<Flight> flights = graph.getFlightsFrom("VIE");

        assertEquals(2, flights.size());
        assertTrue(flights.contains(flight1));
        assertTrue(flights.contains(flight2));
    }

    @Test
    void testGetFlightsFromExists() {
        graph.addAirport(new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3));
        graph.addAirport(new Airport(2, "LHR", "London", "UK", 51.5, -0.4));

        Flight flight = new Flight(1, "VIE", "LHR", "Austrian", "OS100", 120, 250, LocalTime.of(8, 0));

        graph.addFlight(flight);

        List<Flight> flights = graph.getFlightsFrom("VIE");

        assertNotNull(flights);
        assertEquals(1, flights.size());
        assertEquals("VIE", flights.get(0).getOrigin());
        assertEquals("LHR", flights.get(0).getDestination());
    }

    @Test
    void testGetFlightsFromNotExists() {
        List<Flight> flights = graph.getFlightsFrom("XYZ");

        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testGetFlightsFromNoFlights() {
        graph.addAirport(new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3));

        List<Flight> flights = graph.getFlightsFrom("VIE");

        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testGetFlightById() {
        graph.addAirport(new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3));
        graph.addAirport(new Airport(2, "LHR", "London", "UK", 51.5, -0.4));
        Flight flight = new Flight(1, "VIE", "LHR", "Austrian", "OS100", 120, 250, LocalTime.of(8, 0));

        graph.addFlight(flight);

        Flight retrieved = graph.getFlightById(1);

        assertNotNull(retrieved);
        assertEquals(flight, retrieved);
        assertEquals(1, retrieved.getId());
    }

    @Test
    void testGetFlightByIdNotExists() {
        Flight retrieved = graph.getFlightById(999);

        assertNull(retrieved);
    }

    @Test
    void testGetAllFlights() {
        graph.addAirport(new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3));
        graph.addAirport(new Airport(2, "JFK", "New York", "USA", 40.6, -73.8));
        graph.addAirport(new Airport(3, "LHR", "London", "UK", 51.5, -0.4));
        graph.addAirport(new Airport(4, "FRA", "Frankfurt", "Germany", 50.0, 8.5));

        Flight flight1 = new Flight(1, "VIE", "LHR", "Austrian", "OS100", 120, 250, LocalTime.of(8, 0));
        Flight flight2 = new Flight(2, "VIE", "FRA", "Lufthansa", "LH200", 90, 180, LocalTime.of(9, 30));
        Flight flight3 = new Flight(3, "LHR", "JFK", "British Airways", "BA400", 420, 650, LocalTime.of(11, 0));

        graph.addFlight(flight1);
        graph.addFlight(flight2);
        graph.addFlight(flight3);

        Collection<Flight> allFlights = graph.getAllFlights();

        assertNotNull(allFlights);
        assertEquals(3, allFlights.size());
        assertTrue(allFlights.contains(flight1));
        assertTrue(allFlights.contains(flight2));
        assertTrue(allFlights.contains(flight3));
    }

    @Test
    void testGetAllFlightsEmpty() {
        Collection<Flight> allFlights = graph.getAllFlights();

        assertNotNull(allFlights);
        assertTrue(allFlights.isEmpty());
    }

    @Test
    void testAddFlightWithoutAddingAirports() {
        graph.addAirport(new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3));
        graph.addAirport(new Airport(2, "LHR", "London", "UK", 51.5, -0.4));
        Flight flight = new Flight(1, "VIE", "LHR", "Austrian", "OS100", 120, 250, LocalTime.of(8, 0));

        graph.addFlight(flight);

        List<Flight> flights = graph.getFlightsFrom("VIE");

        assertEquals(1, flights.size());
    }

    @Test
    void testGraphStructureIntegrity() {
        graph.addAirport(new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3));
        graph.addAirport(new Airport(2, "LHR", "London", "UK", 51.5, -0.4));
        graph.addAirport(new Airport(3, "JFK", "New York", "USA", 40.6, -73.8));

        Flight f1 = new Flight(1, "VIE", "LHR", "Austrian", "OS100", 120, 250, LocalTime.of(8, 0));
        Flight f2 = new Flight(2, "LHR", "JFK", "British Airways", "BA400", 420, 650, LocalTime.of(11, 0));
        Flight f3 = new Flight(3, "VIE", "JFK", "Austrian", "OS200", 500, 800, LocalTime.of(10, 0));

        graph.addFlight(f1);
        graph.addFlight(f2);
        graph.addFlight(f3);

        assertEquals(2, graph.getFlightsFrom("VIE").size());
        assertEquals(1, graph.getFlightsFrom("LHR").size());
        assertEquals(0, graph.getFlightsFrom("JFK").size());
        assertEquals(3, graph.getAllFlights().size());
    }

    @Test
    void testMultipleFlightsBetweenSameAirports() {
        graph.addAirport(new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3));
        graph.addAirport(new Airport(2, "LHR", "London", "UK", 51.5, -0.4));

        Flight morning = new Flight(1, "VIE", "LHR", "Austrian", "OS100", 120, 250, LocalTime.of(8, 0));
        Flight afternoon = new Flight(2, "VIE", "LHR", "Austrian", "OS102", 120, 280, LocalTime.of(14, 0));
        Flight evening = new Flight(3, "VIE", "LHR", "British Airways", "BA200", 120, 300, LocalTime.of(18, 0));

        graph.addFlight(morning);
        graph.addFlight(afternoon);
        graph.addFlight(evening);

        List<Flight> flights = graph.getFlightsFrom("VIE");

        assertEquals(3, flights.size());
        assertTrue(flights.stream().allMatch(f -> f.getOrigin().equals("VIE")));
        assertTrue(flights.stream().allMatch(f -> f.getDestination().equals("LHR")));
    }

    @Test
    void testBidirectionalFlights() {
        graph.addAirport(new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3));
        graph.addAirport(new Airport(2, "LHR", "London", "UK", 51.5, -0.4));

        Flight vieToLhr = new Flight(1, "VIE", "LHR", "Austrian", "OS100", 120, 250, LocalTime.of(8, 0));
        Flight lhrToVie = new Flight(2, "LHR", "VIE", "British Airways", "BA200", 120, 280, LocalTime.of(14, 0));

        graph.addFlight(vieToLhr);
        graph.addFlight(lhrToVie);

        assertEquals(1, graph.getFlightsFrom("VIE").size());
        assertEquals(1, graph.getFlightsFrom("LHR").size());
        assertEquals("LHR", graph.getFlightsFrom("VIE").get(0).getDestination());
        assertEquals("VIE", graph.getFlightsFrom("LHR").get(0).getDestination());
    }

    @Test
    void testComplexGraphNetwork() {
        graph.addAirport(new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3));
        graph.addAirport(new Airport(2, "LHR", "London", "UK", 51.5, -0.4));
        graph.addAirport(new Airport(3, "FRA", "Frankfurt", "Germany", 50.0, 8.5));
        graph.addAirport(new Airport(4, "JFK", "New York", "USA", 40.6, -73.8));

        graph.addFlight(new Flight(1, "VIE", "LHR", "Austrian", "OS100", 120, 250, LocalTime.of(8, 0)));
        graph.addFlight(new Flight(2, "VIE", "FRA", "Lufthansa", "LH200", 90, 180, LocalTime.of(9, 0)));
        graph.addFlight(new Flight(3, "LHR", "JFK", "British Airways", "BA300", 420, 650, LocalTime.of(11, 0)));
        graph.addFlight(new Flight(4, "FRA", "JFK", "Lufthansa", "LH400", 450, 700, LocalTime.of(12, 0)));

        assertEquals(2, graph.getFlightsFrom("VIE").size());
        assertEquals(1, graph.getFlightsFrom("LHR").size());
        assertEquals(1, graph.getFlightsFrom("FRA").size());
        assertEquals(0, graph.getFlightsFrom("JFK").size());
        assertEquals(4, graph.getAllFlights().size());
    }

    @Test
    void testAddDuplicateAirport() {
        Airport airport1 = new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3);
        Airport airport2 = new Airport(2, "VIE", "Vienna International", "Austria", 48.2, 16.3);

        graph.addAirport(airport1);
        graph.addAirport(airport2);

        Airport retrieved = graph.getAirport("VIE");

        assertEquals("Vienna International", retrieved.getCity());
        assertEquals(2, retrieved.getId());
    }

    @Test
    void testFlightIdUniqueness() {
        graph.addAirport(new Airport(1, "VIE", "Vienna", "Austria", 48.2, 16.3));
        graph.addAirport(new Airport(2, "LHR", "London", "UK", 51.5, -0.4));
        graph.addAirport(new Airport(3, "FRA", "Frankfurt", "Germany", 50.0, 8.5));


        Flight f1 = new Flight(1, "VIE", "LHR", "Austrian", "OS100", 120, 250, LocalTime.of(8, 0));
        Flight f2 = new Flight(1, "VIE", "FRA", "Lufthansa", "LH200", 90, 180, LocalTime.of(9, 0));

        graph.addFlight(f1);
        graph.addFlight(f2);

        Flight retrieved = graph.getFlightById(1);

        assertEquals("LH200", retrieved.getFlightNumber());
        assertEquals("FRA", retrieved.getDestination());
    }

    @Test
    void testEmptyGraph() {
        assertFalse(graph.hasAirport("VIE"));
        assertNull(graph.getAirport("VIE"));
        assertTrue(graph.getFlightsFrom("VIE").isEmpty());
        assertNull(graph.getFlightById(1));
        assertTrue(graph.getAllFlights().isEmpty());
    }
}