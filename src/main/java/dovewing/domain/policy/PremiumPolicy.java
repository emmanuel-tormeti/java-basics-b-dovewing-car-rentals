package dovewing.domain.policy;

import java.math.BigDecimal;

public final class PremiumPolicy extends DailyRatePolicy {

    public PremiumPolicy(BigDecimal dailyRate, BigDecimal deposit) {
        super(dailyRate, deposit);
    }

    @Override
    public String category() {
        return "PREMIUM";
    }
}