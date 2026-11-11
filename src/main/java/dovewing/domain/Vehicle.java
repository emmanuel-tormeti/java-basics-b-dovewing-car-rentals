package dovewing.domain;

public final class Vehicle {

    private final String registration;
    private final String model;
    private final RentalPolicy rentalPolicy;

    public Vehicle(String registration, String model, RentalPolicy rentalPolicy) {
        this.registration = Checks.requireText(registration, "registration");
        this.model = Checks.requireText(model, "model");
        if (rentalPolicy == null) {
            throw new IllegalArgumentException("rentalPolicy must not be null");
        }
        this.rentalPolicy = rentalPolicy;
    }

    public String getRegistration() {
        return registration;
    }

    public String getModel() {
        return model;
    }

    /** Display text supplied by the policy. Nothing branches on it. */
    public String getCategory() {
        return rentalPolicy.category();
    }

    public RentalQuote calculateQuote(int days, RenterType type) {
        return rentalPolicy.calculateQuote(days, type);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Vehicle that && registration.equals(that.registration);
    }

    @Override
    public int hashCode() {
        return registration.hashCode();
    }
}