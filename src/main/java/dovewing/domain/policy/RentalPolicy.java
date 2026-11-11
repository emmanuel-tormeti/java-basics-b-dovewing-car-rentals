package dovewing.domain.policy;

import dovewing.domain.model.RentalQuote;
import dovewing.domain.model.RenterType;

public interface RentalPolicy {
    RentalQuote calculateQuote(int days, RenterType type);

    /** Display text only. Nothing in the system branches on it. */
    String category();
}