package at.hochschule.burgenland.bswe.algo.comparator;

import at.hochschule.burgenland.bswe.algo.model.Route;

import java.util.Comparator;

/**
 * Combines all 3 comparators in a fixed order
 * 1. Price (ascending)
 * 2. Duration (ascending)
 * 3. Stopovers (ascending)
 * Compares by price, if the prices are equal,
 * it compares by duration. If both are equal,
 * it finally compares by the number of stopovers.
 * If all values are equal, 0 is returned.
 */
public class CombinationComparator implements Comparator<Route> {

    private final PriceComparator priceComparator;
    private final DurationComparator durationComparator;
    private final StopoverComparator stopoverComparator;

    public CombinationComparator() {
        this.priceComparator = new PriceComparator();
        this.durationComparator = new DurationComparator();
        this.stopoverComparator = new StopoverComparator();
    }

    @Override
    public int compare(Route r1, Route r2) {
        int result = priceComparator.compare(r1, r2);
        if (result != 0) return result;

        result = durationComparator.compare(r1, r2);
        if (result != 0) return result;

        return stopoverComparator.compare(r1, r2);
    }
}