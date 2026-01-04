package at.hochschule.burgenland.bswe.algo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an airport with its identifying information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Airport {
    private int id;
    private String iata;
    private String city;
    private String country;
    private double latitude;
    private double longitude;

    /**
     * Constructor used for creation via CSV.
     *
     * @param line CSV line to be parsed
     * @return created Airport object
     */
    public static Airport fromCsv(String line) {
        String[] parts = line.split(",");

        if (parts.length != 6) {
            throw new IllegalArgumentException("Ungültige Zeile: " + line);
        }

        return new Airport(
            Integer.parseInt(parts[0].trim()),
            parts[1].trim(),
            parts[2].trim(),
            parts[3].trim(),
            Double.parseDouble(parts[4].trim()),
            Double.parseDouble(parts[5].trim())
        );
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s, %s [Lat: %.2f, Lon: %.2f]",
                iata, id, city, country, latitude, longitude);
    }
}