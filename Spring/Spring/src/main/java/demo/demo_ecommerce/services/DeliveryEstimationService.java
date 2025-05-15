package demo.demo_ecommerce.services;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class DeliveryEstimationService {

    private static final double WAREHOUSE_LAT = 41.9028;  // Roma
    private static final double WAREHOUSE_LON = 12.4964;

    // Mappa CAP → [lat, lon]
    private final Map<String, double[]> capCoordinates = new HashMap<>();

    @PostConstruct
    public void loadCapCoordinates() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("cap_coord_completo.csv"))))) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) { isFirstLine = false; continue; } // skip header
                String[] parts = line.split(",");

                if (parts.length >= 5) {
                    String cap = parts[0].trim();
                    double lat = Double.parseDouble(parts[3].trim());
                    double lon = Double.parseDouble(parts[4].trim());
                    capCoordinates.put(cap, new double[]{lat, lon});
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LocalDate estimateDeliveryDate(String cap) {
        double[] coords = capCoordinates.get(cap);
        if (coords == null) {
            return LocalDate.now().plusDays(3); // fallback
        }

        double distance = haversine(WAREHOUSE_LAT, WAREHOUSE_LON, coords[0], coords[1]);
        int days = calculateDays(distance);
        return LocalDate.now().plusDays(days);
    }

    private int calculateDays(double km) {
        if (km <= 100) return 1;
        else if (km <= 400) return 2;
        else if (km <= 800) return 3;
        else return 4;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
