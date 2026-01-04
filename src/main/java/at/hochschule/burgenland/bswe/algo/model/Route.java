package at.hochschule.burgenland.bswe.algo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a complete route consisting of one or more flights.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    private int id;
    private List<Integer> flights;
    private int totalDuration;
    private double totalPrice;
    private int stopovers;

    /**
     * Constructor used for creation via CSV.
     *
     * @param line CSV line to be parsed
     * @return created Route object
     */
    public static Route fromCsv(String line) {
    String[] parts = line.split(",");

    if (parts.length != 5) {
        throw new IllegalArgumentException("Ungültige Zeile: " + line);
    }

    List<Integer> flightIds = Arrays.stream(parts[1].split("-"))
        .map(Integer::parseInt)
        .toList();

    return new Route(
        Integer.parseInt(parts[0].trim()),
        flightIds,
        Integer.parseInt(parts[2].trim()),
        Double.parseDouble(parts[3].trim()),
        Integer.parseInt(parts[4].trim())
        );
}

    /**
     * Creates a route from a list of flight objects.
     */
    public Route(int id, List<Flight> flightList) {
        this.id = id;
        this.flights = flightList.stream().map(Flight::getId).toList();
        this.totalDuration = flightList.stream().mapToInt(Flight::getDuration).sum();
        this.totalPrice = flightList.stream().mapToDouble(Flight::getPrice).sum();
        this.stopovers = Math.max(0, flightList.size() - 1);
    }

    @Override
    public String toString() {
        return String.format("Route %d: %d flight(s) | %d min | €%.2f | %d stopover(s) | Flights: %s",
                id, flights.size(), totalDuration, totalPrice, stopovers,
                flights.stream().map(String::valueOf).collect(Collectors.joining("-")));
    }
}