package at.hochschule.burgenland.bswe.algo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Represents a flight connection between two airports.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    private int id;
    private String origin;
    private String destination;
    private String airline;
    private String flightNumber;
    private int duration;
    private double price;
    private LocalTime departureTime;

    /**
     * Constructor used for creation via CSV.
     *
     * @param line CSV line to be parsed
     * @return created Flight object
     */
    public static Flight fromCsv(String line) {
        String[] parts = line.split(",");

        if (parts.length != 8) {
            throw new IllegalArgumentException("Ungültige Zeile: " + line);
        }

        return new Flight(
            Integer.parseInt(parts[0].trim()),
            parts[1].trim(),
            parts[2].trim(),
            parts[3].trim(),
            parts[4].trim(),
            Integer.parseInt(parts[5].trim()),
            Double.parseDouble(parts[6].trim()),
            LocalTime.parse(parts[7].trim())
        );
    }

    @Override
    public String toString() {
        return String.format("%s %s: %s -> %s | %d min | €%.2f | Dep: %s",
                airline, flightNumber, origin, destination, duration, price, departureTime);
    }
}