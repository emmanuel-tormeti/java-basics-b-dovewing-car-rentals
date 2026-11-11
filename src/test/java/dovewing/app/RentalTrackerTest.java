package dovewing.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dovewing.domain.model.Depot;
import dovewing.domain.policy.EconomyPolicy;
import dovewing.domain.model.Vehicle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RentalTrackerTest {

    private static final String REJECTED_RENTAL =
            "Rental rejected: Toyota Vitz (GT-1234-20) is already rented";
    private static final String REPLACED_REPORT_LINE =
            "  Rental #3: GT-1234-20 -> Ama Owusu (2 days)";

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final RentalTracker tracker =
            new RentalTracker(new PrintStream(buffer, true, StandardCharsets.UTF_8));

    private List<String> output() {
        return buffer.toString(StandardCharsets.UTF_8).lines().toList();
    }

    @Test
    void demoMatchesTheOriginalExceptForTheDeliberateCorrection() throws IOException {
        List<String> original = Files.readAllLines(Path.of("ORIGINAL_OUTPUT.txt"));

        // Guard: the baseline must contain exactly the two things that are allowed to differ.
        assertTrue(original.get(3).startsWith("Rented Toyota Vitz (GT-1234-20) to Ama Owusu"));
        assertTrue(original.contains(REPLACED_REPORT_LINE));

        List<String> expected = new ArrayList<>(original);
        expected.set(3, REJECTED_RENTAL);
        expected.remove(REPLACED_REPORT_LINE);

        tracker.runDemo();
        assertEquals(expected, output());
    }

    @ParameterizedTest
    @CsvSource({"450.00,450.0", "0.00,0.0", "1200.00,1200.0", "450.50,450.5", "450.55,450.55", "100,100.0"})
    void moneyKeepsAtLeastOneDecimal(String amount, String expected) {
        assertEquals(expected, RentalTracker.money(new BigDecimal(amount)));
    }

    @Test
    void rejectedReturnIsReportedNotThrown() {
        Depot depot = new Depot("Test Depot");
        Vehicle vitz = new Vehicle("GT-1234-20", "Toyota Vitz",
                new EconomyPolicy(new BigDecimal("150.00"), new BigDecimal("500.00")));
        depot.addVehicle(vitz);

        tracker.returnVehicle(depot, vitz);

        assertEquals(List.of("Return rejected: Toyota Vitz (GT-1234-20) has no active rental"), output());
    }
}