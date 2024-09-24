package cz.sykora.filip;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class FileReader {
    private static final Logger logger = LogManager.getLogger(FileReader.class);

    //Je to bool, abych pak mohl otestovat, že vše proběhlo v pořádku
    public static boolean primeReaderRow(String path, int sheetNum, int rowNum) {
        try (FileInputStream fis = new FileInputStream(path)) {
            //Čtení z xlsx souboru
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(sheetNum);

            // Zapnutí čtení jednotlivých řádků paralelně (je to asi o 30% rychlejší při vyplněných 100 000 buňkách)
            // Uvnitř se rozdělí na poloviny a ty udělají to samé dokud počet prvků v podúkolu není menší než threshold
            // S thresholdem 50 jsem to měl nejrychlejší, něco kolem 2.2 sec
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            forkJoinPool.invoke(new ReadRowsTask(sheet, rowNum, 0, sheet.getPhysicalNumberOfRows()));
            logger.info("Nalezeno prvocisel: " + ReadRowsTask.primeCounter);
            return true;
        //Abych ukázal, že vím jak fungujou různé typy exception
        } catch (IOException e) {
            logger.error("Chyba pri nacitani souboru: " + e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Nespecifikovana chyba: " + e.getMessage());
            return false;
        }

    }

    private static class ReadRowsTask extends RecursiveAction {
        private final Sheet sheet;
        private final int rowNum;
        private final int startRow;
        private final int endRow;
        private static final int THRESHOLD = 50;
        // Jen tak pro zajímavost, abychom věděli, kolik prvočísel tam je
        static int primeCounter;

        public ReadRowsTask(Sheet sheet, int rowNum, int startRow, int endRow) {
            this.sheet = sheet;
            this.rowNum = rowNum;
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        protected void compute() {
            //Pokud je rozsah rows větší než threshold, tak rozdělit na 2 další podprobémy
            //Prostě rozdělí sloupec v půlce na 2
            if (endRow - startRow <= THRESHOLD) {
                processRows();
            } else {
                int mid = (startRow + endRow) / 2;
                invokeAll(new ReadRowsTask(sheet, rowNum, startRow, mid),
                        new ReadRowsTask(sheet, rowNum, mid, endRow));
            }
        }

        //Processování jednotlivých podproblémů (sloupců) a buněk v nich
        private void processRows() {
            for (int i = startRow; i < endRow; i++) {
                Row currRow = sheet.getRow(i);
                if (currRow != null) {
                    Cell cell = currRow.getCell(rowNum);
                    if (cell != null) {
                        try {
                            //Pro jednotlivé buňky to zjistí jejich decimal (nechtělo mi to brát vědecký zápis do long)
                            BigDecimal cellNum = new BigDecimal(cell.toString().trim());
                            //Checkne jestli je pozitivní a jestli je celé číslo (jakože 0 desetinných míst)
                            if (cellNum.signum() > 0 && cellNum.scale() == 0) {
                                long cellNumber = cellNum.longValue();
                                //Zavolá isPrime a pokud je, tak zalogguje
                                if (MathChecker.isPrime(cellNumber)) {
                                    logger.info(cellNumber);
                                    primeCounter++;
                                }
                            }
                            //Pokud je parse do BigDecimal špatný, tak jen tak pro info vypíše do debugu
                            //Pokud to chcete vidět, tak si upravte v resources log4j2.xml
                        } catch (Exception e) {
                            logger.debug("Chyba pri zpracováni bunky: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
