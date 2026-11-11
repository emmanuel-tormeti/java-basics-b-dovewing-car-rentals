package dovewing.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record RentalQuote(BigDecimal cost, BigDecimal deposit) {
    public RentalQuote {
        Objects.requireNonNull(cost, "cost must not be null");
        Objects.requireNonNull(deposit, "deposit must not be null");
    }
}