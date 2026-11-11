package dovewing.domain;

public interface RentalPolicy {
    RentalQuote calculateQuote(int days, RenterType type);

    /** Display text only. Nothing in the system branches on it. */
    String category();
}