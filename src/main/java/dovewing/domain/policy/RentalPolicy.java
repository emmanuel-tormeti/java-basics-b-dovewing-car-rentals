package dovewing.domain.policy;

import dovewing.domain.model.RentalQuote;
import dovewing.domain.model.RenterType;

public interface RentalPolicy {

    /**
     * Calculates the rental cost and applicable security deposit.
     *
     * @param days the rental duration in days; must be positive
     * @param type the renter's membership category; must not be null
     * @return the calculated rental quote
     * @throws IllegalArgumentException if days is not positive or type is null
     */
    RentalQuote calculateQuote(int days, RenterType type);

    /**
     * Returns the display category associated with this rental policy.
     *
     * @return the policy's display category
     */
    String category();
}