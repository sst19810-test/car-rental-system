package com.carrentalsystem.app.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.PublicKey;

@AllArgsConstructor
public enum CarType {
    SEDAN("Sedan"),
    SUV("SUV"),
    HATCHBACK("Hatchback"),
    CONVERTIBLE("Convertible"),
    COUPE("Coupe"),
    MINIVAN("Minivan"),
    PICKUP_TRUCK("Pickup Truck"),
    LUXURY("Luxury"),
    SPORTS("Sports"),
    CROSSOVER("Crossover");

    private final String type;
    public String getType() {
        return type;
    }
    public static boolean isValidType(String type) {
        for (CarType carType : CarType.values()) {
            if (carType.name().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }


    public boolean equalsIgnoreCase(String type) {
        if (type == null) {
            return false;
        }
        for (CarType carType : CarType.values()) {
            if (carType.type.equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
}
