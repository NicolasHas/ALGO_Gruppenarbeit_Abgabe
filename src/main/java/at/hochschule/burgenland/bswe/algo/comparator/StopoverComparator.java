package at.hochschule.burgenland.bswe.algo.comparator;

import at.hochschule.burgenland.bswe.algo.model.Route;

import java.util.Comparator;

/**
 * Compares routes by number of stopovers in ascending order (fewest first).
 */
public class StopoverComparator implements Comparator<Route> {

    @Override
    public int compare(Route r1, Route r2) {
        return Integer.compare(r1.getStopovers(), r2.getStopovers());
    }
}