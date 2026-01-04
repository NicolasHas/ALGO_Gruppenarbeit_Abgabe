package at.hochschule.burgenland.bswe.algo.comparator;

import at.hochschule.burgenland.bswe.algo.model.Route;

import java.util.Comparator;

/**
 * Compares routes by total duration in ascending order (fastest first).
 */
public class DurationComparator implements Comparator<Route> {

    @Override
    public int compare(Route r1, Route r2) {
        return Integer.compare(r1.getTotalDuration(), r2.getTotalDuration());
    }
}