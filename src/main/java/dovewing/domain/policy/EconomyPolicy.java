package dovewing.domain.policy;

import java.math.BigDecimal;

public final class EconomyPolicy extends DailyRatePolicy {

    public EconomyPolicy(BigDecimal dailyRate, BigDecimal deposit) {
        super(dailyRate, deposit);
    }

    @Override
    public String category() {
        return "ECONOMY";
    }
}