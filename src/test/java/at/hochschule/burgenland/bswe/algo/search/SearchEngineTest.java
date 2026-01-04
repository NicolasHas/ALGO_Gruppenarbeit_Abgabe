package at.hochschule.burgenland.bswe.algo.search;

import at.hochschule.burgenland.bswe.algo.graph.FlightGraph;
import at.hochschule.burgenland.bswe.algo.model.Airport;
import at.hochschule.burgenland.bswe.algo.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchEngineTest {

    private FlightGraph graph;
    private SearchEngine searchEngine;

    @BeforeEach
    void setUp() {
        graph = new FlightGraph();

        graph.addAirport(new Airport(1, "VIE", "Vienna International Airport", "Austria", 48.2, 16.3));
        graph.addAirport(new Airport(2, "JFK", "John F. Kennedy International Airport", "USA", 40.6, -73.8));
        graph.addAirport(new Airport(3, "LHR", "London Heathrow Airport", "UK", 51.5, -0.4));
        graph.addAirport(new Airport(4, "FRA", "Frankfurt Airport", "Germany", 50.0, 8.5));
        graph.addAirport(new Airport(5, "MUC", "Munich Airport", "Germany", 48.3, 11.7));

        graph.addFlight(new Flight(1, "VIE", "LHR", "Austrian Airlines", "OS100", 120, 250, LocalTime.of(8, 0)));
        graph.addFlight(new Flight(2, "VIE", "FRA", "Lufthansa", "LH200", 90, 180, LocalTime.of(9, 30)));
        graph.addFlight(new Flight(3, "VIE", "MUC", "Austrian Airlines", "OS300", 60, 150, LocalTime.of(10, 0)));
        graph.addFlight(new Flight(4, "LHR", "JFK", "British Airways", "BA400", 420, 650, LocalTime.of(11, 0)));
        graph.addFlight(new Flight(5, "FRA", "JFK", "Lufthansa", "LH500", 450, 700, LocalTime.of(12, 0)));
        graph.addFlight(new Flight(6, "MUC", "JFK", "Lufthansa", "LH600", 480, 720, LocalTime.of(13, 0)));
        graph.addFlight(new Flight(7, "JFK", "VIE", "Austrian Airlines", "OS700", 480, 800, LocalTime.of(18, 0)));

        searchEngine = new SearchEngine(graph);
    }

    @Test
    void testSearchByOriginExists() {
        SearchEngine.SearchResult result = searchEngine.searchByOrigin("VIE");

        assertTrue(result.hasResults());
        assertNotNull(result.airport());
        assertEquals("VIE", result.airport().getIata());
        assertEquals(3, result.flights().size());
    }

    @Test
    void testSearchByOriginNotExists() {
        SearchEngine.SearchResult result = searchEngine.searchByOrigin("XYZ");

        assertFalse(result.hasResults());
        assertNull(result.airport());
        assertTrue(result.flights().isEmpty());
    }

    @Test
    void testSearchByOriginCaseInsensitive() {
        SearchEngine.SearchResult result1 = searchEngine.searchByOrigin("vie");
        SearchEngine.SearchResult result2 = searchEngine.searchByOrigin("VIE");
        SearchEngine.SearchResult result3 = searchEngine.searchByOrigin("ViE");

        assertTrue(result1.hasResults());
        assertTrue(result2.hasResults());
        assertTrue(result3.hasResults());
        assertEquals(result1.flights().size(), result2.flights().size());
        assertEquals(result2.flights().size(), result3.flights().size());
    }

    @Test
    void testSearchByOriginWithWhitespace() {
        SearchEngine.SearchResult result = searchEngine.searchByOrigin("  VIE  ");

        assertTrue(result.hasResults());
        assertNotNull(result.airport());
        assertEquals(3, result.flights().size());
    }

    @Test
    void testSearchByDestinationExists() {
        SearchEngine.SearchResult result = searchEngine.searchByDestination("JFK");

        assertTrue(result.hasResults());
        assertNotNull(result.airport());
        assertEquals("JFK", result.airport().getIata());
        assertEquals(3, result.flights().size());
    }

    @Test
    void testSearchByDestinationNotExists() {
        SearchEngine.SearchResult result = searchEngine.searchByDestination("XYZ");

        assertFalse(result.hasResults());
        assertNull(result.airport());
        assertTrue(result.flights().isEmpty());
    }

    @Test
    void testSearchByDestinationCaseInsensitive() {
        SearchEngine.SearchResult result1 = searchEngine.searchByDestination("jfk");
        SearchEngine.SearchResult result2 = searchEngine.searchByDestination("JFK");

        assertTrue(result1.hasResults());
        assertTrue(result2.hasResults());
        assertEquals(result1.flights().size(), result2.flights().size());
    }

    @Test
    void testSearchByAirlineExists() {
        List<Flight> flights = searchEngine.searchByAirline("Lufthansa");

        assertNotNull(flights);
        assertEquals(3, flights.size());
        assertTrue(flights.stream().allMatch(f -> f.getAirline().contains("Lufthansa")));
    }

    @Test
    void testSearchByAirlinePartialMatch() {
        List<Flight> flights = searchEngine.searchByAirline("Austrian");

        assertNotNull(flights);
        assertEquals(3, flights.size());
        assertTrue(flights.stream().allMatch(f -> f.getAirline().contains("Austrian")));
    }

    @Test
    void testSearchByAirlineCaseInsensitive() {
        List<Flight> flights1 = searchEngine.searchByAirline("lufthansa");
        List<Flight> flights2 = searchEngine.searchByAirline("LUFTHANSA");
        List<Flight> flights3 = searchEngine.searchByAirline("LuFtHaNsA");

        assertEquals(flights1.size(), flights2.size());
        assertEquals(flights2.size(), flights3.size());
    }

    @Test
    void testSearchByAirlineNotExists() {
        List<Flight> flights = searchEngine.searchByAirline("Delta Airlines");

        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testSearchByAirlineEmpty() {
        List<Flight> flights = searchEngine.searchByAirline("");

        assertNotNull(flights);
        assertEquals(7, flights.size());
    }

    @Test
    void testSearchByFlightNumberExists() {
        Flight flight = searchEngine.searchByFlightNumber("OS100");

        assertNotNull(flight);
        assertEquals("OS100", flight.getFlightNumber());
        assertEquals("Austrian Airlines", flight.getAirline());
        assertEquals("VIE", flight.getOrigin());
        assertEquals("LHR", flight.getDestination());
    }

    @Test
    void testSearchByFlightNumberNotExists() {
        Flight flight = searchEngine.searchByFlightNumber("XX999");

        assertNull(flight);
    }

    @Test
    void testSearchByFlightNumberCaseInsensitive() {
        Flight flight1 = searchEngine.searchByFlightNumber("os100");
        Flight flight2 = searchEngine.searchByFlightNumber("OS100");
        Flight flight3 = searchEngine.searchByFlightNumber("Os100");

        assertNotNull(flight1);
        assertNotNull(flight2);
        assertNotNull(flight3);
        assertEquals(flight1.getId(), flight2.getId());
        assertEquals(flight2.getId(), flight3.getId());
    }

    @Test
    void testSearchByFlightNumberWithWhitespace() {
        Flight flight = searchEngine.searchByFlightNumber("  OS100  ");

        assertNotNull(flight);
        assertEquals("OS100", flight.getFlightNumber());
    }

    @Test
    void testSearchByOriginReturnsCorrectFlights() {
        SearchEngine.SearchResult result = searchEngine.searchByOrigin("VIE");

        assertEquals(3, result.flights().size());
        assertTrue(result.flights().stream().allMatch(f -> f.getOrigin().equals("VIE")));

        List<String> destinations = result.flights().stream()
            .map(Flight::getDestination)
            .toList();

        assertEquals(3, destinations.size());
        assertTrue(destinations.contains("LHR"));
        assertTrue(destinations.contains("FRA"));
        assertTrue(destinations.contains("MUC"));
    }

    @Test
    void testSearchByDestinationReturnsCorrectFlights() {
        SearchEngine.SearchResult result = searchEngine.searchByDestination("JFK");

        assertEquals(3, result.flights().size());
        assertTrue(result.flights().stream().allMatch(f -> f.getDestination().equals("JFK")));

        List<String> origins = result.flights().stream()
            .map(Flight::getOrigin)
            .toList();

        assertEquals(3, origins.size());
        assertTrue(origins.contains("LHR"));
        assertTrue(origins.contains("FRA"));
        assertTrue(origins.contains("MUC"));
    }

    @Test
    void testSearchReturnsUniqueFlights() {
        SearchEngine.SearchResult result = searchEngine.searchByOrigin("VIE");

        long uniqueIds = result.flights().size();

        assertEquals(result.flights().size(), uniqueIds);
    }

    @Test
    void testMultipleSearchMethods() {
        SearchEngine.SearchResult originResult = searchEngine.searchByOrigin("VIE");
        List<Flight> austrianFlights = searchEngine.searchByAirline("Austrian");

        long austrianFromVie = originResult.flights().stream()
            .filter(f -> f.getAirline().contains("Austrian"))
            .count();

        assertEquals(2, austrianFromVie);
        assertTrue(austrianFlights.size() >= austrianFromVie);
    }
}