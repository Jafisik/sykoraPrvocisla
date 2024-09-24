import static org.junit.jupiter.api.Assertions.assertEquals;

import cz.sykora.filip.MathChecker;
import org.junit.Test;

public class MathCheckerTester {
    @Test
    public void Testing() {
        //Testuju pro určitá čísla
        assertEquals(true, MathChecker.isPrime(3));
        assertEquals(false,MathChecker.isPrime(21));
        assertEquals(false,MathChecker.isPrime(-50));
        assertEquals(true,MathChecker.isPrime(2147483647));
        assertEquals(false,MathChecker.isPrime(828932904));
    }
}
