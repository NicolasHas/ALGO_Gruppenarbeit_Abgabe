package at.hochschule.burgenland.bswe.algo.sorting;

import at.hochschule.burgenland.bswe.algo.model.Route;

import java.util.Comparator;
import java.util.List;

/**
 * Unstable sorting algorithm using Quick Sort.
 * This implementation sorts Route objects using a provided Comparator.
 * Uses the last element as pivot point and is unstable.
 * Time complexity: O(n log n) average, O(n²) worst case
 * Space complexity: O(log n) for recursion stack
 */
public class QuickSort {

    /**
     * Sorts a list of routes using Quick Sort with the given comparator.
     * If the list contains fewer than two elements, no modification is required.
     *
     * @param routes the list of routes to be sorted
     * @param comparator the comparator defining the sort order
     */
    public static void sort(List<Route> routes, Comparator<Route> comparator) {
        if (routes == null || routes.size() <= 1) {
            return;
        }
        quickSort(routes, 0, routes.size() - 1, comparator);
    }

    /**
     * Main Quick Sort workflow:
     * Recursively sorts the sublist between the given indices.
     *
     * @param routes the list being sorted
     * @param low the lower index of the sublist
     * @param high the upper index of the sublist
     * @param comparator the comparator defining the sort order
     */
    private static void quickSort(List<Route> routes, int low, int high, Comparator<Route> comparator) {
        if (low < high) {
            int pivotIndex = partition(routes, low, high, comparator);

            quickSort(routes, low, pivotIndex - 1, comparator);
            quickSort(routes, pivotIndex + 1, high, comparator);
        }
    }

    /**
     * Partitions the sublist around a pivot element.
     * All elements less than or equal to the pivot are moved to the left
     * of the pivot, and all greater elements to the right.
     *
     * @param routes´the list being partitioned
     * @param low the lower index of the partition range
     * @param high the upper index of the partition range (pivot)
     * @param comparator the comparator defining the sort order
     * @return the final index position of the pivot
     */
    private static int partition(List<Route> routes, int low, int high, Comparator<Route> comparator) {
        Route pivot = routes.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (comparator.compare(routes.get(j), pivot) <= 0) {
                i++;
                swap(routes, i, j);
            }
        }

        swap(routes, i + 1, high);
        return i + 1;
    }

    /**
     * Swaps two elements in the given list with the help of a temp object.
     *
     * @param routes the list containing the elements to swap
     * @param i the index of the first element
     * @param j the index of the second element
     */
    private static void swap(List<Route> routes, int i, int j) {
        Route temp = routes.get(i);
        routes.set(i, routes.get(j));
        routes.set(j, temp);
    }
}
