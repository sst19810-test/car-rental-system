package com.carrentalsystem.app.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class FineCalculator {

    private static final double FINE_MULTIPLIER = 1.5; // 150% of hourly price as penalty

    /**
     * Calculates fine based on delayed return.
     *
     * expectedReturnTime LocalDateTime when car was expected
     *  actualReturnTime   LocalDateTime when car was actually returned
     *  pricePerHour       Hourly rate of the car
     * return fine amount (0 if returned on time or early)
     */
    public static double calculateFine(LocalDateTime expectedReturnTime,
                                       LocalDateTime actualReturnTime,
                                       double pricePerHour) {
        if (actualReturnTime.isBefore(expectedReturnTime) || actualReturnTime.isEqual(expectedReturnTime)) {
            return 0.0;
        }

        long delayInMinutes = Duration.between(expectedReturnTime, actualReturnTime).toMinutes();
        double delayInHours = delayInMinutes / 60.0;

        return delayInHours * pricePerHour * FINE_MULTIPLIER;
    }

    /**
     * Overloaded for controllers where only time is available,
     * and price is fetched separately.
     */
    public static double calculateFine(LocalDateTime expectedReturnTime, LocalDateTime actualReturnTime) {
        // default fallback fine if price is not passed (can be overridden in controller logic)
        return calculateFine(expectedReturnTime, actualReturnTime, 100); // default dummy rate
    }
}
