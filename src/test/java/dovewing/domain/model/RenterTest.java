package dovewing.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import dovewing.domain.policy.EconomyPolicy;
import dovewing.domain.policy.PremiumPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class RenterTest {

    private final Vehicle vitz = new Vehicle("GT-1234-20", "Toyota Vitz",
            new EconomyPolicy(new BigDecimal("150.00"), new BigDecimal("500.00")));
    private final Vehicle prado = new Vehicle("GR-5678-22", "Toyota Prado",
            new PremiumPolicy(new BigDecimal("600.00"), new BigDecimal("2000.00")));

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void rejectsBlankName(String name) {
        assertThrows(IllegalArgumentException.class, () -> new Renter(name, RenterType.CASUAL));
    }

    @Test
    void rejectsNullType() {
        assertThrows(IllegalArgumentException.class, () -> new Renter("Kofi Mensah", null));
    }

    @Test
    void historyStartsEmpty() {
        assertTrue(new Renter("Kofi Mensah", RenterType.CASUAL).getHistory().isEmpty());
    }

    @Test
    void recordRentalAppendsModelInOrder() {
        Renter ama = new Renter("Ama Owusu", RenterType.MEMBER);
        ama.recordRental(prado);
        ama.recordRental(vitz);
        assertEquals(List.of("Rented Toyota Prado", "Rented Toyota Vitz"), ama.getHistory());
    }

    @Test
    void historyIsReadOnly() {
        Renter ama = new Renter("Ama Owusu", RenterType.MEMBER);
        ama.recordRental(vitz);
        assertThrows(UnsupportedOperationException.class, () -> ama.getHistory().add("tampered"));
        assertEquals(1, ama.getHistory().size());
    }

    @Test
    void rentersWithTheSameNameAreDistinct() {
        assertNotEquals(new Renter("Ama Owusu", RenterType.MEMBER),
                new Renter("Ama Owusu", RenterType.MEMBER));
    }
}