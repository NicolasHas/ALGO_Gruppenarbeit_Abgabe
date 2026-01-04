package at.hochschule.burgenland.bswe.algo.comparator;

import at.hochschule.burgenland.bswe.algo.model.Route;

import java.util.Comparator;

/**
 * Compares routes by total price in ascending order (cheapest first).
 */
public class PriceComparator implements Comparator<Route> {

    @Override
    public int compare(Route r1, Route r2) {
        return Double.compare(r1.getTotalPrice(), r2.getTotalPrice());
    }
}