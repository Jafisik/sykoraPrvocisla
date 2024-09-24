import cz.sykora.filip.FileReader;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class FileReaderTest {
    @Test
    public void testPrimeReaderRow(){
        String path = "src/main/resources/vzorek_dat_test.xlsx";
        assertTrue(FileReader.primeReaderRow(path, 0, 1));
    }

    @Test
    public void testPrimeReaderRowIncorrectPath(){
        String path = "src/vzorek_dat_test.xlsx";
        assertTrue(!FileReader.primeReaderRow(path, 0, 1));
    }
}
