package dovewing.domain.policy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import dovewing.domain.model.RentalQuote;
import dovewing.domain.model.RenterType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RentalPoliciesTest {

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    private final RentalPolicy economy = new EconomyPolicy(bd("150.00"), bd("500.00"));
    private final RentalPolicy premium = new PremiumPolicy(bd("600.00"), bd("2000.00"));

    @Test
    void economyCasualQuote() {
        RentalQuote quote = economy.calculateQuote(3, RenterType.CASUAL);
        assertEquals(bd("450.00"), quote.cost());
        assertEquals(bd("500.00"), quote.deposit());
    }

    @Test
    void economyMemberDepositIsWaived() {
        RentalQuote quote = economy.calculateQuote(3, RenterType.MEMBER);
        assertEquals(bd("450.00"), quote.cost());
        assertEquals(bd("0.00"), quote.deposit());
    }

    @Test
    void premiumCasualQuote() {
        RentalQuote quote = premium.calculateQuote(2, RenterType.CASUAL);
        assertEquals(bd("1200.00"), quote.cost());
        assertEquals(bd("2000.00"), quote.deposit());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    void rejectsNonPositiveDays(int days) {
        assertThrows(IllegalArgumentException.class,
                () -> economy.calculateQuote(days, RenterType.CASUAL));
    }

    @Test
    void rejectsNullRenterType() {
        assertThrows(IllegalArgumentException.class, () -> economy.calculateQuote(1, null));
    }

    @Test
    void rejectsInvalidRateOrDeposit() {
        assertThrows(IllegalArgumentException.class, () -> new EconomyPolicy(null, bd("1.00")));
        assertThrows(IllegalArgumentException.class, () -> new EconomyPolicy(bd("-1.00"), bd("1.00")));
        assertThrows(IllegalArgumentException.class, () -> new PremiumPolicy(bd("1.00"), null));
        assertThrows(IllegalArgumentException.class, () -> new PremiumPolicy(bd("1.00"), bd("-1.00")));
    }

    @Test
    void resultsAlwaysHaveScaleTwo() {
        RentalPolicy oddScale = new EconomyPolicy(bd("150"), bd("500"));
        RentalQuote casual = oddScale.calculateQuote(3, RenterType.CASUAL);
        RentalQuote member = oddScale.calculateQuote(3, RenterType.MEMBER);
        assertEquals(2, casual.cost().scale());
        assertEquals(2, casual.deposit().scale());
        assertEquals(2, member.cost().scale());
        assertEquals(2, member.deposit().scale());
    }

    @Test
    void categoryLabelDoesNotAffectQuote() {
        RentalPolicy relabelled = new DailyRatePolicy(bd("150.00"), bd("500.00")) {
            @Override
            public String category() {
                return "SOMETHING_ELSE";
            }
        };
        assertEquals(economy.calculateQuote(3, RenterType.CASUAL),
                relabelled.calculateQuote(3, RenterType.CASUAL));
        assertEquals(economy.calculateQuote(3, RenterType.MEMBER),
                relabelled.calculateQuote(3, RenterType.MEMBER));
    }
}