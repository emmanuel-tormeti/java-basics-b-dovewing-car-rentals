package dovewing.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Depot {

    private final String name;
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<Renter> renters = new ArrayList<>();
    private final List<Rental> rentals = new ArrayList<>();
    private final Map<Vehicle, Rental> activeRentals = new LinkedHashMap<>();
    private int nextRentalId = 1;

    public Depot(String name) {
        this.name = Checks.requireText(name, "name");
    }

    public String getName() {
        return name;
    }

    public void addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("vehicle must not be null");
        }
        if (vehicles.contains(vehicle)) {
            throw new IllegalArgumentException(
                    "A vehicle with registration " + vehicle.getRegistration() + " already exists");
        }
        vehicles.add(vehicle);
    }

    public void addRenter(Renter renter) {
        if (renter == null) {
            throw new IllegalArgumentException("renter must not be null");
        }
        if (renters.contains(renter)) {
            throw new IllegalArgumentException("renter is already registered");
        }
        renters.add(renter);
    }

    public Rental rent(Renter renter, Vehicle vehicle, int days) {
        if (renter == null || vehicle == null) {
            throw new IllegalArgumentException("renter and vehicle must not be null");
        }
        if (days <= 0) {
            throw new IllegalArgumentException("days must be positive, got " + days);
        }
        requireInFleet(vehicle);
        if (!renters.contains(renter)) {
            throw new IllegalArgumentException("renter is not registered with this depot");
        }
        if (activeRentals.containsKey(vehicle)) {
            throw new IllegalStateException(describe(vehicle) + " is already rented");
        }
        RentalQuote quote = vehicle.calculateQuote(days, renter.getType());
        Rental rental = new Rental(nextRentalId, renter, vehicle, days, quote);

        nextRentalId++;
        rentals.add(rental);
        activeRentals.put(vehicle, rental);
        renter.recordRental(vehicle);
        return rental;
    }

    public Rental returnVehicle(Vehicle vehicle) {
        requireInFleet(vehicle);
        Rental rental = activeRentals.get(vehicle);
        if (rental == null) {
            throw new IllegalStateException(describe(vehicle) + " has no active rental");
        }
        rental.markReturned();
        activeRentals.remove(vehicle);
        return rental;
    }

    public boolean isAvailable(Vehicle vehicle) {
        requireInFleet(vehicle);
        return !activeRentals.containsKey(vehicle);
    }

    public List<Vehicle> getVehicles() {
        return List.copyOf(vehicles);
    }

    public List<Renter> getRenters() {
        return List.copyOf(renters);
    }

    /** Every rental ever made, in creation order, including returned ones. */
    public List<Rental> getRentals() {
        return List.copyOf(rentals);
    }

    /** Rentals that are currently active, in creation order. */
    public List<Rental> getActiveRentals() {
        return List.copyOf(activeRentals.values());
    }

    private void requireInFleet(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("vehicle must not be null");
        }
        if (vehicles.stream().noneMatch(v -> v == vehicle)) {
            throw new IllegalArgumentException(
                    "vehicle " + vehicle.getRegistration() + " is not in this depot's fleet");
        }
    }

    private static String describe(Vehicle vehicle) {
        return vehicle.getModel() + " (" + vehicle.getRegistration() + ")";
    }
}