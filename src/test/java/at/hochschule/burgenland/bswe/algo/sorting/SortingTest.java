package at.hochschule.burgenland.bswe.algo.sorting;

import at.hochschule.burgenland.bswe.algo.comparator.CombinationComparator;
import at.hochschule.burgenland.bswe.algo.comparator.DurationComparator;
import at.hochschule.burgenland.bswe.algo.comparator.PriceComparator;
import at.hochschule.burgenland.bswe.algo.comparator.StopoverComparator;
import at.hochschule.burgenland.bswe.algo.model.Flight;
import at.hochschule.burgenland.bswe.algo.model.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortingTest {

    private List<Route> testRoutes;

    @BeforeEach
    void setUp() {
        testRoutes = new ArrayList<>();

        testRoutes.add(new Route(1,
            Arrays.asList(new Flight(1, "VIE", "LHR", "OS", "OS100", 120, 300, LocalTime.of(8, 0)),
                         new Flight(2, "LHR", "JFK", "BA", "BA150", 400, 500, LocalTime.of(11, 0)),
                         new Flight(3, "JFK", "LAX", "AA", "AA200", 350, 400, LocalTime.of(20, 0)))));
        testRoutes.add(new Route(2,
            Arrays.asList(new Flight(4, "VIE", "FRA", "LH", "LH300", 90, 150, LocalTime.of(9, 0)),
                         new Flight(5, "FRA", "LAX", "LH", "LH400", 600, 700, LocalTime.of(12, 0)))));
        testRoutes.add(new Route(3,
            Arrays.asList(new Flight(6, "VIE", "ZRH", "OS", "OS500", 100, 200, LocalTime.of(7, 0)),
                         new Flight(7, "ZRH", "JFK", "LX", "LX600", 450, 600, LocalTime.of(10, 0)))));
        testRoutes.add(new Route(4,
            Arrays.asList(new Flight(8, "VIE", "JFK", "OS", "OS700", 500, 800, LocalTime.of(10, 0)))));
    }

    @Test
    void testMergeSortByPrice() {
        List<Route> routes = new ArrayList<>(testRoutes);

        MergeSort.sort(routes, new PriceComparator());

        assertTrue(routes.get(0).getTotalPrice() <= routes.get(1).getTotalPrice());
        assertTrue(routes.get(1).getTotalPrice() <= routes.get(2).getTotalPrice());
        assertTrue(routes.get(2).getTotalPrice() <= routes.get(3).getTotalPrice());
    }

    @Test
    void testQuickSortByPrice() {
        List<Route> routes = new ArrayList<>(testRoutes);

        QuickSort.sort(routes, new PriceComparator());

        assertTrue(routes.get(0).getTotalPrice() <= routes.get(1).getTotalPrice());
        assertTrue(routes.get(1).getTotalPrice() <= routes.get(2).getTotalPrice());
        assertTrue(routes.get(2).getTotalPrice() <= routes.get(3).getTotalPrice());
    }

    @Test
    void testMergeSortByDuration() {
        List<Route> routes = new ArrayList<>(testRoutes);

        MergeSort.sort(routes, new DurationComparator());

        for (int i = 0; i < routes.size() - 1; i++) {
            assertTrue(routes.get(i).getTotalDuration() <= routes.get(i + 1).getTotalDuration(),
                "Route " + i + " should have duration <= Route " + (i + 1));
        }
    }

    @Test
    void testQuickSortByDuration() {
        List<Route> routes = new ArrayList<>(testRoutes);

        QuickSort.sort(routes, new DurationComparator());

        for (int i = 0; i < routes.size() - 1; i++) {
            assertTrue(routes.get(i).getTotalDuration() <= routes.get(i + 1).getTotalDuration(),
                "Route " + i + " should have duration <= Route " + (i + 1));
        }
    }

    @Test
    void testMergeSortByStopovers() {
        List<Route> routes = new ArrayList<>(testRoutes);

        MergeSort.sort(routes, new StopoverComparator());

        for (int i = 0; i < routes.size() - 1; i++) {
            assertTrue(routes.get(i).getStopovers() <= routes.get(i + 1).getStopovers(),
                "Route " + i + " should have stopovers <= Route " + (i + 1));
        }
    }

    @Test
    void testQuickSortByStopovers() {
        List<Route> routes = new ArrayList<>(testRoutes);

        QuickSort.sort(routes, new StopoverComparator());

        for (int i = 0; i < routes.size() - 1; i++) {
            assertTrue(routes.get(i).getStopovers() <= routes.get(i + 1).getStopovers(),
                "Route " + i + " should have stopovers <= Route " + (i + 1));
        }
    }

    @Test
    void testMergeSortWithEmptyList() {
        List<Route> routes = new ArrayList<>();

        assertDoesNotThrow(() -> MergeSort.sort(routes, new PriceComparator()));
        assertTrue(routes.isEmpty());
    }

    @Test
    void testQuickSortWithEmptyList() {
        List<Route> routes = new ArrayList<>();

        assertDoesNotThrow(() -> QuickSort.sort(routes, new PriceComparator()));
        assertTrue(routes.isEmpty());
    }

    @Test
    void testMergeSortWithSingleElement() {
        List<Route> routes = new ArrayList<>();
        routes.add(testRoutes.get(0));

        MergeSort.sort(routes, new PriceComparator());

        assertEquals(1, routes.size());
    }

    @Test
    void testQuickSortWithSingleElement() {
        List<Route> routes = new ArrayList<>();
        routes.add(testRoutes.get(0));

        QuickSort.sort(routes, new PriceComparator());

        assertEquals(1, routes.size());
    }

    @Test
    void testMergeSortWithNull() {
        assertDoesNotThrow(() -> MergeSort.sort(null, new PriceComparator()));
    }

    @Test
    void testQuickSortWithNull() {
        assertDoesNotThrow(() -> QuickSort.sort(null, new PriceComparator()));
    }

    @Test
    void testMergeSortStability() {
        Route route5 = new Route(5, Arrays.asList(new Flight(9, "VIE", "MUC", "OS", "OS800", 60, 100, LocalTime.of(8, 0))));
        Route route6 = new Route(6, Arrays.asList(new Flight(10, "VIE", "BER", "OS", "OS900", 90, 100, LocalTime.of(9, 0))));
        List<Route> routes = Arrays.asList(route5, route6);

        MergeSort.sort(routes, new PriceComparator());

        assertEquals(5, routes.get(0).getId());
        assertEquals(6, routes.get(1).getId());
    }

    @Test
    void testSortingPreservesOriginalList() {
        List<Route> originalRoutes = new ArrayList<>(testRoutes);
        List<Route> routesToSort = new ArrayList<>(testRoutes);

        MergeSort.sort(routesToSort, new PriceComparator());

        assertEquals(originalRoutes.size(), testRoutes.size());
        for (int i = 0; i < originalRoutes.size(); i++) {
            assertEquals(originalRoutes.get(i).getId(), testRoutes.get(i).getId());
        }
    }

    @Test
    void testBothAlgorithmsProduceSameResult() {
        List<Route> mergeRoutes = new ArrayList<>(testRoutes);
        List<Route> quickRoutes = new ArrayList<>(testRoutes);

        MergeSort.sort(mergeRoutes, new PriceComparator());
        QuickSort.sort(quickRoutes, new PriceComparator());

        assertEquals(mergeRoutes.size(), quickRoutes.size());
        for (int i = 0; i < mergeRoutes.size(); i++) {
            assertEquals(mergeRoutes.get(i).getTotalPrice(), quickRoutes.get(i).getTotalPrice());
        }
    }

    @Test
    void testCombinedComparatorSortingOrder() {
        Route r1 = new Route(1, List.of(), 300, 200, 1);
        Route r2 = new Route(2, List.of(), 200, 200, 2);
        Route r3 = new Route(3, List.of(), 300, 150, 1);
        List<Route> routes = new ArrayList<>(List.of(r1, r2, r3));
        Comparator<Route> comparator = new CombinationComparator();

        MergeSort.sort(routes, comparator);

        assertEquals(3, routes.get(0).getId());
        assertEquals(2, routes.get(1).getId());
        assertEquals(1, routes.get(2).getId());
    }

    @Test
    void testCombinedComparatorStableSortPreservesOrder() {
        Route r1 = new Route(1, List.of(), 300, 200, 1);
        Route r2 = new Route(2, List.of(), 300, 200, 1);
        Route r3 = new Route(3, List.of(), 300, 200, 1);
        List<Route> routes = new ArrayList<>(List.of(r1, r2, r3));

        MergeSort.sort(routes, new CombinationComparator());

        assertEquals(1, routes.get(0).getId());
        assertEquals(2, routes.get(1).getId());
        assertEquals(3, routes.get(2).getId());
    }
}