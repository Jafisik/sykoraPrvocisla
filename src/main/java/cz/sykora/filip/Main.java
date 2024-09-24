package cz.sykora.filip;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    public static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        //Stopwatch pro zjišťování trvání programu (jen tak pro info
        // a abych ukázal, že to umím a záleží mi na rychlosti)
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //Pokud nemá argument, tak to uživateli řekne a skončí
        if(args.length != 1){
            logger.error("Zadejte cestu k excel souboru");
            return;
        }

        //Celá logika čtení z excelu a zjišťování jestli je prvočíslo
        FileReader.primeReaderRow(args[0],0,1);

        stopWatch.stop();
        logger.info("Provedeni programu trvalo: " + stopWatch);
    }
}

