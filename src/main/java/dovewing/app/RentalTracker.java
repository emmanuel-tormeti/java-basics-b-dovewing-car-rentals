package dovewing.app;

import dovewing.domain.model.Depot;
import dovewing.domain.policy.EconomyPolicy;
import dovewing.domain.policy.PremiumPolicy;
import dovewing.domain.model.Rental;
import dovewing.domain.model.Renter;
import dovewing.domain.model.RenterType;
import dovewing.domain.model.Vehicle;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Objects;

public final class RentalTracker {

    private final PrintStream out;

    public RentalTracker(PrintStream out) {
        this.out = Objects.requireNonNull(out, "out");
    }

    public static void main(String[] args) {
        new RentalTracker(System.out).runDemo();
    }

    void runDemo() {
        Depot depot = new Depot("Dovewing Car Rentals");

        Vehicle vitz = new Vehicle("GT-1234-20", "Toyota Vitz",
                new EconomyPolicy(new BigDecimal("150.00"), new BigDecimal("500.00")));
        Vehicle prado = new Vehicle("GR-5678-22", "Toyota Prado",
                new PremiumPolicy(new BigDecimal("600.00"), new BigDecimal("2000.00")));
        depot.addVehicle(vitz);
        depot.addVehicle(prado);

        Renter kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        Renter ama = new Renter("Ama Owusu", RenterType.MEMBER);
        depot.addRenter(kofi);
        depot.addRenter(ama);

        rent(depot, kofi, vitz, 3);
        rent(depot, ama, prado, 2);
        returnVehicle(depot, prado);
        rent(depot, ama, vitz, 2); // rejected: the Vitz is already out with Kofi

        printReport(depot);
    }

    void rent(Depot depot, Renter renter, Vehicle vehicle, int days) {
        try {
            Rental rental = depot.rent(renter, vehicle, days);
            out.println("Rented " + vehicle.getModel() + " (" + vehicle.getRegistration() + ") to "
                    + renter.getName() + " for " + days + " days, cost GHS " + money(rental.getCost())
                    + ", deposit GHS " + money(rental.getDeposit()));
        } catch (IllegalStateException e) {
            out.println("Rental rejected: " + e.getMessage());
        }
    }

    void returnVehicle(Depot depot, Vehicle vehicle) {
        try {
            depot.returnVehicle(vehicle);
            out.println("Returned " + vehicle.getModel() + " (" + vehicle.getRegistration() + ")");
        } catch (IllegalStateException e) {
            out.println("Return rejected: " + e.getMessage());
        }
    }

    void printReport(Depot depot) {
        out.println();
        out.println("=== " + depot.getName() + " - end of day ===");
        out.println("Fleet status:");
        for (Vehicle vehicle : depot.getVehicles()) {
            out.println("  " + vehicle.getModel() + " (" + vehicle.getRegistration() + ", "
                    + vehicle.getCategory() + "): "
                    + (depot.isAvailable(vehicle) ? "AVAILABLE" : "RENTED"));
        }
        out.println("Active rentals:");
        for (Rental rental : depot.getActiveRentals()) {
            out.println("  Rental #" + rental.getId() + ": " + rental.getVehicle().getRegistration()
                    + " -> " + rental.getRenter().getName() + " (" + rental.getDays() + " days)");
        }
    }

    /** Trailing zeros are trimmed but at least one decimal is kept: 450.00 prints 450.0. */
    static String money(BigDecimal amount) {
        BigDecimal trimmed = amount.stripTrailingZeros();
        if (trimmed.scale() < 1) {
            trimmed = trimmed.setScale(1);
        }
        return trimmed.toPlainString();
    }
}