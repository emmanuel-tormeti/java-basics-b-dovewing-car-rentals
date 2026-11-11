package dovewing.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class VehicleTest {

    private static final RentalPolicy ECONOMY =
            new EconomyPolicy(new BigDecimal("150.00"), new BigDecimal("500.00"));

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void rejectsBlankRegistration(String registration) {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(registration, "Toyota Vitz", ECONOMY));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void rejectsBlankModel(String model) {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("GT-1234-20", model, ECONOMY));
    }

    @Test
    void rejectsNullPolicy() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("GT-1234-20", "Toyota Vitz", null));
    }

    @Test
    void equalityUsesRegistrationOnly() {
        Vehicle a = new Vehicle("GT-1234-20", "Toyota Vitz", ECONOMY);
        Vehicle b = new Vehicle("GT-1234-20", "Different model",
                new PremiumPolicy(new BigDecimal("600.00"), new BigDecimal("2000.00")));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void differentRegistrationsAreNotEqual() {
        Vehicle a = new Vehicle("GT-1234-20", "Toyota Vitz", ECONOMY);
        Vehicle b = new Vehicle("GR-5678-22", "Toyota Vitz", ECONOMY);
        assertNotEquals(a, b);
    }

    @Test
    void worksAsMapKeyThroughAnEqualVehicle() {
        Map<Vehicle, String> map = new HashMap<>();
        map.put(new Vehicle("GT-1234-20", "Toyota Vitz", ECONOMY), "rental");
        assertEquals("rental", map.get(new Vehicle("GT-1234-20", "Toyota Vitz", ECONOMY)));
    }

    @Test
    void quoteAndCategoryComeFromThePolicy() {
        Vehicle vitz = new Vehicle("GT-1234-20", "Toyota Vitz", ECONOMY);
        assertEquals("ECONOMY", vitz.getCategory());
        assertEquals(new RentalQuote(new BigDecimal("450.00"), new BigDecimal("500.00")),
                vitz.calculateQuote(3, RenterType.CASUAL));
    }
}