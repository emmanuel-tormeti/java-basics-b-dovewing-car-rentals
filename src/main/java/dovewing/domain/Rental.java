package dovewing.domain;

import java.math.BigDecimal;

public final class Rental {

    private final int id;
    private final Renter renter;
    private final Vehicle vehicle;
    private final int days;
    private final BigDecimal cost;
    private final BigDecimal deposit;
    private RentalStatus status = RentalStatus.ACTIVE;

    Rental(int id, Renter renter, Vehicle vehicle, int days, RentalQuote quote) {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be positive, got " + id);
        }
        if (renter == null || vehicle == null || quote == null) {
            throw new IllegalArgumentException("renter, vehicle and quote must not be null");
        }
        if (days <= 0) {
            throw new IllegalArgumentException("days must be positive, got " + days);
        }
        this.id = id;
        this.renter = renter;
        this.vehicle = vehicle;
        this.days = days;
        this.cost = quote.cost();
        this.deposit = quote.deposit();
    }

    public int getId() {
        return id;
    }

    public Renter getRenter() {
        return renter;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public int getDays() {
        return days;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public RentalStatus getStatus() {
        return status;
    }

    void markReturned() {
        if (status == RentalStatus.RETURNED) {
            throw new IllegalStateException("Rental #" + id + " is already returned");
        }
        status = RentalStatus.RETURNED;
    }
}