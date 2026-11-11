package dovewing.domain.model;

/**
 * Represents the current status of a rental.
 */
public enum RentalStatus {

    /** Rental is active and the vehicle has not been returned. */
    ACTIVE,

    /** Rental has been completed and the vehicle returned. */
    RETURNED
}
