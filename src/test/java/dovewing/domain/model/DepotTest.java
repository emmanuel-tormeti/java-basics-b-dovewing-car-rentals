package dovewing.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import dovewing.domain.policy.EconomyPolicy;
import dovewing.domain.policy.PremiumPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class DepotTest {

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    private Depot depot;
    private Vehicle vitz;
    private Vehicle prado;
    private Renter kofi;
    private Renter ama;

    @BeforeEach
    void setUp() {
        depot = new Depot("Test Depot");
        vitz = new Vehicle("GT-1234-20", "Toyota Vitz", new EconomyPolicy(bd("150.00"), bd("500.00")));
        prado = new Vehicle("GR-5678-22", "Toyota Prado", new PremiumPolicy(bd("600.00"), bd("2000.00")));
        kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        ama = new Renter("Ama Owusu", RenterType.MEMBER);
        depot.addVehicle(vitz);
        depot.addVehicle(prado);
        depot.addRenter(kofi);
        depot.addRenter(ama);
    }

    @Test
    void casualRentalUsesTheQuoteAndMakesTheVehicleUnavailable() {
        Rental rental = depot.rent(kofi, vitz, 3);
        assertEquals(bd("450.00"), rental.getCost());
        assertEquals(bd("500.00"), rental.getDeposit());
        assertEquals(RentalStatus.ACTIVE, rental.getStatus());
        assertFalse(depot.isAvailable(vitz));
        assertTrue(depot.isAvailable(prado));
    }

    @Test
    void memberDepositIsWaived() {
        Rental rental = depot.rent(ama, prado, 2);
        assertEquals(bd("1200.00"), rental.getCost());
        assertEquals(bd("0.00"), rental.getDeposit());
    }

    @Test
    void secondRentalOfTheSameVehicleIsRejected() {
        Rental first = depot.rent(kofi, vitz, 3);
        IllegalStateException ex =
                assertThrows(IllegalStateException.class, () -> depot.rent(ama, vitz, 2));
        assertTrue(ex.getMessage().contains("GT-1234-20"));
        assertEquals(List.of(first), depot.getActiveRentals());
        assertEquals(List.of(first), depot.getRentals());
    }

    @Test
    void returnClosesOnlyItsOwnRental() {
        Rental forVitz = depot.rent(kofi, vitz, 3);
        Rental forPrado = depot.rent(ama, prado, 2);
        assertSame(forPrado, depot.returnVehicle(prado));
        assertEquals(RentalStatus.RETURNED, forPrado.getStatus());
        assertEquals(RentalStatus.ACTIVE, forVitz.getStatus());
        assertTrue(depot.isAvailable(prado));
        assertFalse(depot.isAvailable(vitz));
    }

    @Test
    void returnedVehicleCanBeRentedAgain() {
        depot.rent(kofi, vitz, 3);
        depot.returnVehicle(vitz);
        Rental again = depot.rent(ama, vitz, 2);
        assertEquals(2, again.getId());
        assertFalse(depot.isAvailable(vitz));
        assertEquals(2, depot.getRentals().size());
    }

    @Test
    void returnWithoutAnActiveRentalIsRejected() {
        assertThrows(IllegalStateException.class, () -> depot.returnVehicle(vitz));
        assertTrue(depot.isAvailable(vitz));
    }

    @Test
    void returningTwiceIsRejected() {
        depot.rent(kofi, vitz, 3);
        depot.returnVehicle(vitz);
        assertThrows(IllegalStateException.class, () -> depot.returnVehicle(vitz));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rejectsNonPositiveDays(int days) {
        assertThrows(IllegalArgumentException.class, () -> depot.rent(kofi, vitz, days));
        assertTrue(depot.isAvailable(vitz));
    }

    @Test
    void nullsAreRejectedWithoutChangingState() {
        assertThrows(IllegalArgumentException.class, () -> depot.rent(null, vitz, 1));
        assertThrows(IllegalArgumentException.class, () -> depot.rent(kofi, null, 1));
        assertThrows(IllegalArgumentException.class, () -> depot.returnVehicle(null));
        assertThrows(IllegalArgumentException.class, () -> depot.addVehicle(null));
        assertThrows(IllegalArgumentException.class, () -> depot.addRenter(null));
        assertTrue(depot.getRentals().isEmpty());
        assertTrue(depot.isAvailable(vitz));
        assertEquals(2, depot.getVehicles().size());
        assertEquals(2, depot.getRenters().size());
        assertTrue(kofi.getHistory().isEmpty());
    }

    @Test
    void duplicateRegistrationIsRejected() {
        Vehicle clone = new Vehicle("GT-1234-20", "Other model",
                new PremiumPolicy(bd("1.00"), bd("1.00")));
        assertThrows(IllegalArgumentException.class, () -> depot.addVehicle(clone));
        assertThrows(IllegalArgumentException.class, () -> depot.addVehicle(vitz));
        assertEquals(List.of(vitz, prado), depot.getVehicles());
    }

    @Test
    void sameRenterCannotBeAddedTwice() {
        assertThrows(IllegalArgumentException.class, () -> depot.addRenter(kofi));
        assertEquals(2, depot.getRenters().size());
    }

    @Test
    void unregisteredVehicleOrRenterIsRejected() {
        Vehicle stranger = new Vehicle("XX-0000-00", "Unknown", new EconomyPolicy(bd("1.00"), bd("1.00")));
        Renter outsider = new Renter("Outsider", RenterType.CASUAL);
        assertThrows(IllegalArgumentException.class, () -> depot.rent(kofi, stranger, 1));
        assertThrows(IllegalArgumentException.class, () -> depot.rent(outsider, vitz, 1));
        assertThrows(IllegalArgumentException.class, () -> depot.isAvailable(stranger));
        assertThrows(IllegalArgumentException.class, () -> depot.returnVehicle(stranger));
        assertTrue(depot.getRentals().isEmpty());
    }

    @Test
    void equalVehicleThatIsNotTheFleetInstanceIsRejected() {
        Vehicle impostor = new Vehicle("GT-1234-20", "Toyota Vitz", new EconomyPolicy(bd("1.00"), bd("1.00")));
        assertThrows(IllegalArgumentException.class, () -> depot.rent(kofi, impostor, 1));
        assertTrue(depot.getRentals().isEmpty());
    }

    @Test
    void failedRentLeavesNoPartialState() {
        depot.rent(kofi, vitz, 3);
        assertThrows(IllegalStateException.class, () -> depot.rent(ama, vitz, 2));
        assertThrows(IllegalArgumentException.class, () -> depot.rent(ama, prado, 0));
        assertEquals(1, depot.getRentals().size());
        assertEquals(1, depot.getActiveRentals().size());
        assertTrue(ama.getHistory().isEmpty());
        assertTrue(depot.isAvailable(prado));
        assertEquals(2, depot.rent(ama, prado, 2).getId());
    }

    @Test
    void rentalIdsStartAtOneAndIncrease() {
        assertEquals(1, depot.rent(kofi, vitz, 3).getId());
        assertEquals(2, depot.rent(ama, prado, 2).getId());
    }

    @Test
    void eachDepotNumbersItsOwnRentals() {
        Depot other = new Depot("Other Depot");
        Vehicle otherVitz = new Vehicle("GT-1234-20", "Toyota Vitz", new EconomyPolicy(bd("150.00"), bd("500.00")));
        Renter otherKofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        other.addVehicle(otherVitz);
        other.addRenter(otherKofi);
        depot.rent(kofi, vitz, 3);
        assertEquals(1, other.rent(otherKofi, otherVitz, 3).getId());
    }

    @Test
    void renterHistoryRecordsEveryRental() {
        depot.rent(ama, prado, 2);
        depot.returnVehicle(prado);
        depot.rent(ama, vitz, 2);
        assertEquals(List.of("Rented Toyota Prado", "Rented Toyota Vitz"), ama.getHistory());
    }

    @Test
    void collectionsAreReadOnlySnapshots() {
        Rental rental = depot.rent(kofi, vitz, 3);
        assertThrows(UnsupportedOperationException.class, () -> depot.getVehicles().clear());
        assertThrows(UnsupportedOperationException.class, () -> depot.getRenters().clear());
        assertThrows(UnsupportedOperationException.class, () -> depot.getRentals().add(rental));
        assertThrows(UnsupportedOperationException.class, () -> depot.getActiveRentals().clear());
        assertFalse(depot.isAvailable(vitz));
        assertEquals(1, depot.getRentals().size());
    }

    @Test
    void returnedRentalsStayInHistoryButLeaveTheActiveList() {
        Rental rental = depot.rent(kofi, vitz, 3);
        depot.returnVehicle(vitz);
        assertEquals(List.of(rental), depot.getRentals());
        assertEquals(RentalStatus.RETURNED, rental.getStatus());
        assertTrue(depot.getActiveRentals().isEmpty());
    }

    @Test
    void activeRentalsAreInRentalOrder() {
        Rental first = depot.rent(ama, prado, 2);
        Rental second = depot.rent(kofi, vitz, 3);
        assertEquals(List.of(first, second), depot.getActiveRentals());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void rejectsBlankDepotName(String name) {
        assertThrows(IllegalArgumentException.class, () -> new Depot(name));
    }
}