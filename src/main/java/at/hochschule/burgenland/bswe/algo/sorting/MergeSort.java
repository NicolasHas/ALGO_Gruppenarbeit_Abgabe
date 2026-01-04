package at.hochschule.burgenland.bswe.algo.sorting;

import at.hochschule.burgenland.bswe.algo.model.Route;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Stable sorting utility using Merge Sort.
 * This implementation sorts Route objects using a provided Comparator.
 * It guarantees stable ordering, routes considered equal by the comparator
 * retain their original relative order.
 * Time complexity: O(n log n) in all cases
 * Space complexity: O(n)
 */
public class MergeSort {

    /**
     * Sorts a list of routes using Merge Sort with the given comparator.
     * If the list contains fewer than two elements, no modification is required.
     *
     * @param routes the list of routes to be sorted
     * @param comparator the comparator defining the sort order
     */
    public static void sort(List<Route> routes, Comparator<Route> comparator) {
        if (routes == null || routes.size() <= 1) {
            return;
        }
        mergeSort(routes, 0, routes.size() - 1, comparator);
    }

    /**
     * Main Merge Sort workflow:
     * Recursively divides the list into sublists, sorts them and merges
     * the sublists back together in a sorted order.
     *
     * @param routes the list being sorted
     * @param left the left boundary index
     * @param right the right boundary index
     * @param comparator the comparator defining the sort order
     */
    private static void mergeSort(List<Route> routes, int left, int right, Comparator<Route> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            mergeSort(routes, left, mid, comparator);
            mergeSort(routes, mid + 1, right, comparator);

            merge(routes, left, mid, right, comparator);
        }
    }

    /**
     * Merge step of the Merge Sort.
     * Merges two adjacent sorted sublists into a single sorted segment.
     *
     * @param routes the list containing the sublists to merge
     * @param left the start index of the left sublist
     * @param mid the end index of the left sublist
     * @param right the end index of the right sublist
     * @param comparator the comparator defining the sort order
     */
    private static void merge(List<Route> routes, int left, int mid, int right, Comparator<Route> comparator) {
        List<Route> leftArray = new ArrayList<>(routes.subList(left, mid + 1));
        List<Route> rightArray = new ArrayList<>(routes.subList(mid + 1, right + 1));

        int i = 0, j = 0, k = left;

        while (i < leftArray.size() && j < rightArray.size()) {
            if (comparator.compare(leftArray.get(i), rightArray.get(j)) <= 0) {
                routes.set(k++, leftArray.get(i++));
            } else {
                routes.set(k++, rightArray.get(j++));
            }
        }

        while (i < leftArray.size()) {
            routes.set(k++, leftArray.get(i++));
        }

        while (j < rightArray.size()) {
            routes.set(k++, rightArray.get(j++));
        }
    }
}