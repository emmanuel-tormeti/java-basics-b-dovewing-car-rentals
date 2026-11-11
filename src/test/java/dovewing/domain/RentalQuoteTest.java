package dovewing.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class RentalQuoteTest {

    @Test
    void holdsCostAndDeposit() {
        RentalQuote quote = new RentalQuote(new BigDecimal("450.00"), new BigDecimal("500.00"));
        assertEquals(new BigDecimal("450.00"), quote.cost());
        assertEquals(new BigDecimal("500.00"), quote.deposit());
    }

    @Test
    void rejectsNullCost() {
        assertThrows(NullPointerException.class,
                () -> new RentalQuote(null, new BigDecimal("0.00")));
    }

    @Test
    void rejectsNullDeposit() {
        assertThrows(NullPointerException.class,
                () -> new RentalQuote(new BigDecimal("0.00"), null));
    }
}