package dovewing.domain.model;

import dovewing.domain.validation.Checks;

import java.util.ArrayList;
import java.util.List;

public final class Renter {

    private final String name;
    private final RenterType type;
    private final List<String> history = new ArrayList<>();

    public Renter(String name, RenterType type) {
        this.name = Checks.requireText(name, "name");
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public RenterType getType() {
        return type;
    }

    /** Read-only snapshot of the renter's rental history. */
    public List<String> getHistory() {
        return List.copyOf(history);
    }

    void recordRental(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("vehicle must not be null");
        }
        history.add("Rented " + vehicle.getModel());
    }
}