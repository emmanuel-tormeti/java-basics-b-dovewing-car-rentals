package dovewing.domain.policy;

import dovewing.domain.model.RentalQuote;
import dovewing.domain.model.RenterType;

import java.math.BigDecimal;
import java.math.RoundingMode;

abstract class DailyRatePolicy implements RentalPolicy {

    private static final BigDecimal NO_DEPOSIT = BigDecimal.ZERO.setScale(2);

    private final BigDecimal dailyRate;
    private final BigDecimal deposit;

    DailyRatePolicy(BigDecimal dailyRate, BigDecimal deposit) {
        this.dailyRate = normalise(dailyRate, "dailyRate");
        this.deposit = normalise(deposit, "deposit");
    }

    @Override
    public RentalQuote calculateQuote(int days, RenterType type) {
        if (days <= 0) {
            throw new IllegalArgumentException("days must be positive, got " + days);
        }
        if (type == null) {
            throw new IllegalArgumentException("renter type must not be null");
        }
        BigDecimal cost = dailyRate.multiply(BigDecimal.valueOf(days));
        BigDecimal due = switch (type) {
            case CASUAL -> deposit;
            case MEMBER -> NO_DEPOSIT;
        };
        return new RentalQuote(cost, due);
    }

    private static BigDecimal normalise(BigDecimal value, String name) {
        if (value == null || value.signum() < 0) {
            throw new IllegalArgumentException(name + " must be a non-negative amount");
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}