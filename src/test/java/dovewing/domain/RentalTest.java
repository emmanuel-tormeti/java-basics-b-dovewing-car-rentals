package dovewing.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RentalTest {

    private final Renter kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
    private final Vehicle vitz = new Vehicle("GT-1234-20", "Toyota Vitz",
            new EconomyPolicy(new BigDecimal("150.00"), new BigDecimal("500.00")));
    private final RentalQuote quote =
            new RentalQuote(new BigDecimal("450.00"), new BigDecimal("500.00"));

    private Rental newRental() {
        return new Rental(1, kofi, vitz, 3, quote);
    }

    @Test
    void startsActiveWithTheGivenValues() {
        Rental rental = newRental();
        assertEquals(1, rental.getId());
        assertSame(kofi, rental.getRenter());
        assertSame(vitz, rental.getVehicle());
        assertEquals(3, rental.getDays());
        assertEquals(new BigDecimal("450.00"), rental.getCost());
        assertEquals(new BigDecimal("500.00"), rental.getDeposit());
        assertEquals(RentalStatus.ACTIVE, rental.getStatus());
    }

    @Test
    void markReturnedChangesOnlyTheStatus() {
        Rental rental = newRental();
        rental.markReturned();
        assertEquals(RentalStatus.RETURNED, rental.getStatus());
        assertEquals(1, rental.getId());
        assertSame(kofi, rental.getRenter());
        assertSame(vitz, rental.getVehicle());
        assertEquals(3, rental.getDays());
        assertEquals(new BigDecimal("450.00"), rental.getCost());
        assertEquals(new BigDecimal("500.00"), rental.getDeposit());
    }

    @Test
    void returningTwiceIsRejectedAndStatusStaysReturned() {
        Rental rental = newRental();
        rental.markReturned();
        assertThrows(IllegalStateException.class, rental::markReturned);
        assertEquals(RentalStatus.RETURNED, rental.getStatus());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rejectsNonPositiveId(int id) {
        assertThrows(IllegalArgumentException.class, () -> new Rental(id, kofi, vitz, 3, quote));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rejectsNonPositiveDays(int days) {
        assertThrows(IllegalArgumentException.class, () -> new Rental(1, kofi, vitz, days, quote));
    }

    @Test
    void rejectsNullArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Rental(1, null, vitz, 3, quote));
        assertThrows(IllegalArgumentException.class, () -> new Rental(1, kofi, null, 3, quote));
        assertThrows(IllegalArgumentException.class, () -> new Rental(1, kofi, vitz, 3, null));
    }
}